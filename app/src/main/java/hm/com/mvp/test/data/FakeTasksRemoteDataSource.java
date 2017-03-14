package hm.com.mvp.test.data;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import hm.com.mvp.data.Task;
import hm.com.mvp.data.source.TasksDataSource;
import rx.Observable;

/**
 *
 * Created by HuMiao on  2017/3/10
 */
public class FakeTasksRemoteDataSource implements TasksDataSource {

    private static FakeTasksRemoteDataSource INSTANCE;


    private static final Map<String, Task> TASKS_SERVICE_DATA = new LinkedHashMap<>();

    private  FakeTasksRemoteDataSource(){
    }

    public static synchronized FakeTasksRemoteDataSource getInstance(){
        if (INSTANCE == null) {
            INSTANCE = new FakeTasksRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Task>> getTasks() {
        Collection<Task> values = TASKS_SERVICE_DATA.values();
        return Observable.from(values).toList();
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        return Observable.just(task);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        TASKS_SERVICE_DATA.put(task.getId(), task);
    }

    @Override
    public void completeTask(@NonNull Task task) {
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        TASKS_SERVICE_DATA.put(task.getId(), completedTask);
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        Task completedTask = new Task(task.getTitle(), task.getDescription(), task.getId(), true);
        TASKS_SERVICE_DATA.put(taskId, completedTask);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        TASKS_SERVICE_DATA.put(task.getId(), activeTask);
    }

    /**
     *  激活任务
     * @param taskId
     */
    @Override
    public void activateTask(@NonNull String taskId) {
        Task task = TASKS_SERVICE_DATA.get(taskId);
        Task activeTask = new Task(task.getTitle(), task.getDescription(), task.getId());
        TASKS_SERVICE_DATA.put(taskId, activeTask);
    }

    @Override
    public void clearCompletedTasks() {
        Iterator<Map.Entry<String, Task>> it = TASKS_SERVICE_DATA.entrySet().iterator();
        while (it.hasNext()){
            Map.Entry<String, Task> entry = it.next();
            if(entry.getValue().isCompleted()){
                it.remove();
            }
        }
    }

    @Override
    public void refreshTasks() {
        //{@link TaskRepository} 在所有可用的数据源中处理需要刷新的任务
        // TODO: 2017/3/13 刷新逻辑
    }

    @Override
    public void deleteAllTasks() {
        TASKS_SERVICE_DATA.clear();
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        TASKS_SERVICE_DATA.remove(taskId);
    }
}
