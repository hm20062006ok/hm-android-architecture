package hm.com.mvp.taskdetail;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.common.base.Strings;

import hm.com.mvp.data.Task;
import hm.com.mvp.data.source.TasksRepository;
import hm.com.mvp.util.schedulers.BaseSchedulerProvider;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/12
 */
class TaskDetailPresenter implements TaskDetailContract.Presenter{

    private  String mTaskId;
    private final TasksRepository mTasksRepository;
    private final TaskDetailContract.View mTaskDetailView;
    private final BaseSchedulerProvider mScheduProvider;

    private  CompositeSubscription mSubcriptions;

    public TaskDetailPresenter(@Nullable String taskId,
                               @NonNull TasksRepository tasksRepository,
                               @NonNull TaskDetailContract.View taskDetailView,
                               @NonNull BaseSchedulerProvider schedulerProvider) {
        mTaskId = taskId;
        mTasksRepository = checkNotNull(tasksRepository, "taskRepository cannot be null!");
        mTaskDetailView = checkNotNull(taskDetailView, "taskDetailView cannot be null!");
        mScheduProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null!");

        mSubcriptions = new CompositeSubscription();
        mTaskDetailView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        openTask();
    }

    @Override
    public void unsubscribe() {
        mSubcriptions.clear();
    }

    private void openTask(){
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskDetailView.setLoadingIndicator(true);
        mSubcriptions.add(mTasksRepository
        .getTask(mTaskId)
        .subscribeOn(mScheduProvider.computation())
        .observeOn(mScheduProvider.ui())
        .subscribe(
                this::showTask,
                throwable -> {},
                () -> mTaskDetailView.setLoadingIndicator(false)
        ));
    }

    @Override
    public void editTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTaskDetailView.showEditTask(mTaskId);
    }

    @Override
    public void deleteTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTasksRepository.deleteTask(mTaskId);
        mTaskDetailView.showTaskDeleted();
    }

    @Override
    public void completeTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTasksRepository.completeTask(mTaskId);
        mTaskDetailView.showTaskMarkedComplete();
    }

    @Override
    public void activateTask() {
        if (Strings.isNullOrEmpty(mTaskId)) {
            mTaskDetailView.showMissingTask();
            return;
        }
        mTasksRepository.activateTask(mTaskId);
        mTaskDetailView.showTaskMarkedActive();
    }

    private void showTask(@NonNull Task task) {
        String title = task.getTitle();
        String description = task.getDescription();
        if (Strings.isNullOrEmpty(title)) {
            mTaskDetailView.hideTitle();
        }else{
            mTaskDetailView.showTitle(title);
        }

        if (Strings.isNullOrEmpty(description)) {
            mTaskDetailView.hideDescription();
        }else{
            mTaskDetailView.showDescription(description);
        }
        mTaskDetailView.showCompletionStatus(task.isCompleted());
    }
}
