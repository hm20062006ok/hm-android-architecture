package hm.com.mvp.tasks;

import android.support.annotation.NonNull;

import java.util.List;

import hm.com.mvp.BasePresenter;
import hm.com.mvp.BaseView;
import hm.com.mvp.data.Task;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/11
 */
public interface TasksContract {

    interface View extends BaseView<Presenter> {
        void setLoadingIndicator(boolean active);

        void showTasks(List<Task> tasks);

        void showAddTask();

        void showTaskDetailsUi(String taskId);

        void showTaskMarkedComplete();

        void showTaskMarkedActive();

        void showCompletedTasksCleared();

        void showLoadingTasksError();

        void showNoTasks();

        void showActiveFilterLabel();

        void showCompletedFilterLabel();

        void showAllFilterLabel();

        void showNoActiveTasks();

        void showNoCompletedTasks();

        void showSuccessfullySavedMessage();

        boolean isActive();

        void showFilteringPopUpMenu();

    }

    interface Presenter extends BasePresenter {
        void result(int requestCode, int resultCode);

        void loadTasks(boolean forceUpdate);

        void addNewTask();

        void openTaskDetails(@NonNull Task requestedTask);

        void completeTask(@NonNull Task completedTask);

        void activeteTask(@NonNull Task activeTaks);

        void clearCompletedTasks();

        void setFiltering(TasksFilterType requestType);

        TasksFilterType getFiltering();

    }
}
