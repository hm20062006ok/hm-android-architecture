package hm.com.mvp.data.source;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import hm.com.mvp.data.Task;
import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
public class TasksRepository implements TasksDataSource {
    private static final String TAG = TasksRepository.class.getName();
    @Nullable
    private static TasksRepository INSTANCE = null;

    @Nullable
    private final TasksDataSource mTasksRemoteDataSource;

    @Nullable
    private final TasksDataSource mTasksLocalDataSource;

    @Nullable
    Map<String, Task> mCachedTasks;

    boolean mCacheIsDirty = false;

    private TasksRepository(@NonNull TasksDataSource tasksRemoteDataSource,
                            @NonNull TasksDataSource tasksLocalDataSource) {
        mTasksRemoteDataSource = checkNotNull(tasksRemoteDataSource);
        mTasksLocalDataSource = checkNotNull(tasksLocalDataSource);
    }

    public static TasksRepository getInstance(@NonNull TasksDataSource tasksRemoteDataSource,
                                              @NonNull TasksDataSource tasksLocalDtaSource) {
        if (null == INSTANCE) {
            INSTANCE = new TasksRepository(tasksRemoteDataSource, tasksLocalDtaSource);
        }
        return INSTANCE;
    }

    public static void destoryInstance() {
        INSTANCE = null;
    }

    @Override
    public Observable<List<Task>> getTasks() {

      /*  // Respond immediately with cache if available and not dirty
        //如果缓存可用且不需要强制刷新, 则立即返回响应
        if (mCachedTasks != null && !mCacheIsDirty) {
            Log.d(TAG, "return Tasks from cache");
            return Observable.from(mCachedTasks.values()).toList();
        } else if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Observable<List<Task>> localTasks = getAndCacheLocalTasks();

        if (!mCacheIsDirty){
            Log.d(TAG, "return Tasks from local");
            return localTasks;
        }else{
            Log.d(TAG, "return Tasks from remote");
            Observable<List<Task>> remoteTasks = getAndSaveRemoteTasks();
            return Observable.concat(remoteTasks, localTasks)
                    .filter(tasks -> !tasks.isEmpty())
                    .first();
        }*/

        // Respond immediately with cache if available and not dirty
        if (mCachedTasks != null && !mCacheIsDirty) {
            Log.d(TAG, "return Tasks from cache");
            return Observable.from(mCachedTasks.values()).toList();
        } else if (mCachedTasks == null) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Observable<List<Task>> remoteTasks = getAndSaveRemoteTasks();

        if (mCacheIsDirty) {
            Log.d(TAG, "return Tasks from remote");
            return remoteTasks;
        } else {
            Log.d(TAG, "return Tasks from local");
            // Query the local storage if available. If not, query the network.
            Observable<List<Task>> localTasks = getAndCacheLocalTasks();
            return Observable.concat(localTasks, remoteTasks)
                    .filter(tasks -> !tasks.isEmpty())
                    .first();
        }
    }

    private Observable<List<Task>> getAndCacheLocalTasks() {
        return mTasksLocalDataSource.getTasks()
                .flatMap(new Func1<List<Task>, Observable<List<Task>>>() {
                    @Override
                    public Observable<List<Task>> call(List<Task> tasks) {
                        return Observable.from(tasks)
                                .doOnNext(task -> mCachedTasks.put(task.getId(), task))
                                .toList();
                    }
                });
    }

    private Observable<List<Task>> getAndSaveRemoteTasks() {
        return mTasksRemoteDataSource
                .getTasks()
                .flatMap(new Func1<List<Task>, Observable<List<Task>>>() {
                    @Override
                    public Observable<List<Task>> call(List<Task> tasks) {
                        return Observable.from(tasks)
                                .doOnNext(task -> {
                                    mTasksLocalDataSource.saveTask(task);
                                    mCachedTasks.put(task.getId(), task);
                                })
                                .toList();
                    }
                })
                .doOnCompleted(() -> {

                    mCacheIsDirty = false;
                    Log.d(TAG, "doOnCompleted" + mCacheIsDirty);
                });

    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        checkNotNull(taskId);

        final Task cachedTask = getTaskWithId(taskId);
        if (null != cachedTask) {
            return Observable.just(cachedTask);
        }

        if (null == cachedTask) {
            mCachedTasks = new LinkedHashMap<>();
        }

        Observable<Task> localTask = getTaskWithIdFromLocalRepository(taskId);
        Observable<Task> remoteTask = mTasksRemoteDataSource.
                getTask(taskId)
                .doOnNext(task -> {
                    mTasksLocalDataSource.saveTask(task);
                    mCachedTasks.put(task.getId(), task);
                });
        return Observable.concat(localTask, remoteTask).first()
                .map(task -> {
                    if (task == null) {
                        throw new NoSuchElementException("No task found with taskId " + taskId);
                    }
                    return task;
                });
    }

    @Override
    public void saveTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.saveTask(task);
        Log.d(TAG, "save task to remote data source");
        mTasksLocalDataSource.saveTask(task);

        Log.d(TAG, "save task to local data source");
        if (null == mCachedTasks) {
            mCachedTasks = new LinkedHashMap<>();
        }
        Log.d(TAG, "save task to cache");
        mCachedTasks.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.completeTask(task);
        mTasksLocalDataSource.completeTask(task);
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        if (null == mCachedTasks) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        checkNotNull(taskId);
        Task taskWithId = getTaskWithId(taskId);
        if (null != taskWithId) {
            completeTask(taskWithId);
        }
    }

    @Override
    public void activateTask(@NonNull Task task) {
        checkNotNull(task);
        mTasksRemoteDataSource.activateTask(task);
        mTasksLocalDataSource.activateTask(task);
        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        if (null == mCachedTasks) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.put(task.getId(), activeTask);
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        checkNotNull(taskId);
        Task taskWithId = getTaskWithId(taskId);
        if (taskWithId != null) {
            activateTask(taskWithId);
        }
    }

    @Override
    public void clearCompletedTasks() {
        mTasksRemoteDataSource.clearCompletedTasks();
        mTasksLocalDataSource.clearCompletedTasks();
        if (null == mCachedTasks) {
            mCachedTasks = new LinkedHashMap<>();
        }
        Iterator<Map.Entry<String, Task>> it = mCachedTasks.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Task> entry = it.next();
            if (entry.getValue().isCompleted()) {
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        mCacheIsDirty = true;
    }

    @Override
    public void deleteAllTasks() {
        mTasksRemoteDataSource.deleteAllTasks();
        mTasksLocalDataSource.deleteAllTasks();
        if (null == mCachedTasks) {
            mCachedTasks = new LinkedHashMap<>();
        }
        mCachedTasks.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        mTasksRemoteDataSource.deleteTask(taskId);
        mTasksLocalDataSource.deleteTask(taskId);
        mCachedTasks.remove(taskId);
    }

    @NonNull
    private Task getTaskWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedTasks == null || mCachedTasks.isEmpty()) {
            return null;
        } else {
            return mCachedTasks.get(id);
        }
    }

    @NonNull
    Observable<Task> getTaskWithIdFromLocalRepository(String taskId) {
        return mTasksLocalDataSource
                .getTask(taskId)
                .doOnNext(task -> mCachedTasks.put(taskId, task))
                .first();
    }
}
