package hm.com.mvp.addedittask;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import hm.com.mvp.data.Task;
import hm.com.mvp.data.source.TasksDataSource;
import hm.com.mvp.util.schedulers.BaseSchedulerProvider;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/12
 */
class AddEditTaskPresenter implements AddEditTaskContract.Presenter {

    private static final String TAG = AddEditTaskPresenter.class.getName();
    @Nullable
    private  String mTaskId;
    @NonNull
    private final TasksDataSource mTasksRepository;

    @NonNull
    private final AddEditTaskContract.View mAddTaskView;

    @NonNull
    private final BaseSchedulerProvider mSchedulerProvider;

    @NonNull
    private  CompositeSubscription mSubscriptions;

    private boolean mIsDataMissing;

    public AddEditTaskPresenter(@NonNull String taskId, @NonNull TasksDataSource tasksRepository,
                                @NonNull AddEditTaskContract.View addTaskView,
                                boolean shouldLoadDataFromRepo,
                                @NonNull BaseSchedulerProvider schedulerProvider) {
        mTaskId = taskId;
        mTasksRepository = checkNotNull(tasksRepository);
        mAddTaskView = checkNotNull(addTaskView);
        mIsDataMissing = shouldLoadDataFromRepo;
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null!");
        mSubscriptions = new CompositeSubscription();
        mAddTaskView.setPresenter(this);
    }

    @Override
    public void subscribe() {
        if (!isNewTask() && mIsDataMissing) {
            populateTask();
        }
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }

    @Override
    public void saveTask(String title, String description) {
        if (isNewTask()) {
            createTask(title, description);
        } else {
            updataTask(title, description);
        }
    }

    @Override
    public void populateTask() {
        if (isNewTask()) {
            throw new RuntimeException("poplataTask() was called but task is new.");
        }

        mSubscriptions.add(mTasksRepository
                .getTask(mTaskId)
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        task -> {
                            if (mAddTaskView.isActive()) {
                                mAddTaskView.setTitle(task.getTitle());
                                mAddTaskView.setDescription(task.getDescription());

                                mIsDataMissing = false;
                            }
                        },
                        __ -> {
                            if (mAddTaskView.isActive()) {
                                mAddTaskView.showEmptyTaskError();
                            }
                        }));
    }

    @Override
    public boolean isDataMissing() {
        return mIsDataMissing;
    }

    private boolean isNewTask() {
        return mTaskId == null;
    }

    private void createTask(String title, String description) {
        Log.d(TAG, "createTask: ");
        Task newTask = new Task(title, description);
        if (newTask.isEmpty()) {
            mAddTaskView.showEmptyTaskError();
        }else{
            Log.d(TAG, "createTask: saveTask");
            mTasksRepository.saveTask(newTask);
            mAddTaskView.showTasksList();
        }
    }

    private void updataTask(String title, String description) {
        if (isNewTask()) {
            throw new RuntimeException("updateTask() was called task is new. ");
        }
        mTasksRepository.saveTask(new Task(title,description, mTaskId));
        mAddTaskView.showTasksList();
    }
}
