package hm.com.mvp.tasks;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import hm.com.mvp.addedittask.AddEditTaskActivity;
import hm.com.mvp.data.Task;
import hm.com.mvp.data.source.TasksRepository;
import hm.com.mvp.util.schedulers.BaseSchedulerProvider;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
class TasksPresenter implements TasksContract.Presenter {

    private static final String TAG = TasksPresenter.class.getName();
    @NonNull
    private final TasksRepository mTaskRepository;

    @NonNull
    private final TasksContract.View mTasksView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @NonNull
    private TasksFilterType mCurrentFiltering = TasksFilterType.ALL_TASKS;

    private boolean mFirstLoad = true;

    @NonNull
    private CompositeSubscription mSubscriptions;


    public TasksPresenter(@NonNull TasksRepository taskRepository,
                          @NonNull TasksContract.View tasksView,
                          @NonNull BaseSchedulerProvider schedulerProvider) {
        mTaskRepository = checkNotNull(taskRepository, "taskRepository connot be null");
        mTasksView = checkNotNull(tasksView, "tasksView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null!");

        mSubscriptions = new CompositeSubscription();
        mTasksView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        loadTasks(false);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if (AddEditTaskActivity.REQUEST_ADD_TASK == requestCode && Activity.RESULT_OK == resultCode) {
            mTasksView.showSuccessfullySavedMessage();
        }
    }

    @Override
    public void loadTasks(boolean forceUpdate) {
        loadTasks(forceUpdate || mFirstLoad, true);
        mFirstLoad = false;
    }

    private void loadTasks(final boolean forceUpdate, final boolean showLoadingUI) {
        if (showLoadingUI) {
            mTasksView.setLoadingIndicator(true);
        }

        if (forceUpdate) {
            mTaskRepository.refreshTasks();
        }

        mSubscriptions.clear();
        Subscription subscription = mTaskRepository
                .getTasks()
                .flatMap(new Func1<List<Task>, Observable<Task>>() {
                    @Override
                    public Observable<Task> call(List<Task> tasks) {
                        return Observable.from(tasks);
                    }
                })
                .filter(task -> {
                    switch (mCurrentFiltering) {
                        case ACTIVE_TASKS:
                            return task.isActive();
                        case COMPLETED_TASKS:
                            return task.isCompleted();
                        case ALL_TASKS:
                        default:
                            return true;
                    }
                })
                .toList()
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        new Action1<List<Task>>() {
                            @Override
                            public void call(List<Task> tasks) {
                                Log.d(TAG, "tasks: ");
                                TasksPresenter.this.processTasks(tasks);
                            }
                        },
                        new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.d(TAG, "throwable: ");
                                mTasksView.showLoadingTasksError();
                            }
                        },
                        new Action0() {
                            @Override
                            public void call() {
                                Log.d(TAG, "call: xxx");
                                mTasksView.setLoadingIndicator(false);
                            }
                        });
        mSubscriptions.add(subscription);
    }

    private void processTasks(@NonNull List<Task> tasks) {

        boolean taskIsEmpty = tasks.isEmpty();
        Log.d(TAG, "task is empty : " + taskIsEmpty);
        if (taskIsEmpty) {
            // Show a message indicating there are no tasks for that filter type.
            processEmptyTasks();
        } else {
            // Show the list of tasks
            mTasksView.showTasks(tasks);
            // Set the filter label's text.
            showFilterLabel();
        }
    }

    private void showFilterLabel() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showActiveFilterLabel();
                break;
            case COMPLETED_TASKS:
                mTasksView.showCompletedFilterLabel();
                break;
            default:
                mTasksView.showAllFilterLabel();
                break;
        }
    }

    private void processEmptyTasks() {
        switch (mCurrentFiltering) {
            case ACTIVE_TASKS:
                mTasksView.showNoActiveTasks();
                break;
            case COMPLETED_TASKS:
                mTasksView.showNoCompletedTasks();
                break;
            default:
                mTasksView.showNoTasks();
                break;
        }
    }

    @Override
    public void addNewTask() {
        mTasksView.showAddTask();
    }

    @Override
    public void openTaskDetails(@NonNull Task requestedTask) {
        checkNotNull(requestedTask, "requestTask cannot be null !");
        mTasksView.showTaskDetailsUi(requestedTask.getId());
    }

    @Override
    public void completeTask(@NonNull Task completedTask) {
        checkNotNull(completedTask, "completedTask cannot be null!");
        mTaskRepository.completeTask(completedTask);
        mTasksView.showTaskMarkedComplete();
        loadTasks(false, false);
    }

    @Override
    public void activeteTask(@NonNull Task activeTaks) {
        checkNotNull(activeTaks, "activeTask cannot be null!");
        mTaskRepository.activateTask(activeTaks);
        mTasksView.showTaskMarkedActive();
        loadTasks(false, false);
    }


    @Override
    public void clearCompletedTasks() {
        mTaskRepository.clearCompletedTasks();
        mTasksView.showCompletedTasksCleared();
        loadTasks(false, false);
    }

    @Override
    public void setFiltering(TasksFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    @Override
    public TasksFilterType getFiltering() {
        return mCurrentFiltering;
    }
}
