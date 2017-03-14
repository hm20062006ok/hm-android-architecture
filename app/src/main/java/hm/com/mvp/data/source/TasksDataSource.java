package hm.com.mvp.data.source;

import android.support.annotation.NonNull;

import java.util.List;

import hm.com.mvp.data.Task;
import rx.Observable;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
public interface TasksDataSource {
    Observable<List<Task>> getTasks();

    Observable<Task> getTask(@NonNull String taskId);

    void saveTask(@NonNull Task task);

    void completeTask(@NonNull Task task);

    void completeTask(@NonNull String taskId);

    void activateTask(@NonNull Task task);

    void activateTask(@NonNull String taskId);

    void clearCompletedTasks();

    void refreshTasks();

    void deleteAllTasks();

    void deleteTask(@NonNull String taskId);
}
