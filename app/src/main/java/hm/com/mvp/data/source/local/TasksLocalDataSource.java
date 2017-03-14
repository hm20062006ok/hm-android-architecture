package hm.com.mvp.data.source.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.List;

import hm.com.mvp.data.Task;
import hm.com.mvp.data.source.TasksDataSource;
import hm.com.mvp.util.schedulers.BaseSchedulerProvider;
import hm.com.mvp.data.source.local.TasksPersistenceContract.TaskEntry;
import rx.Observable;
import rx.functions.Func1;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
public class TasksLocalDataSource implements TasksDataSource {
    private static final String TAG = TasksLocalDataSource.class.getName();
    @Nullable
    private static TasksLocalDataSource INSTANCE;

    @Nullable
    private final BriteDatabase mDatabaseHelper;

    @Nullable
    private Func1<Cursor, Task> mTaskMapperFunction;

    private TasksLocalDataSource(@NonNull Context context,
                                 @NonNull BaseSchedulerProvider schedulerProvider) {
        checkNotNull(context, "context cannot to null!");
        checkNotNull(schedulerProvider, "schedulerProvider cannot be null!");
        TasksDbHelper dbHelper = new TasksDbHelper(context);
        SqlBrite sqlBrite = SqlBrite.create();
        mDatabaseHelper = sqlBrite.wrapDatabaseHelper(dbHelper, schedulerProvider.io());
        mTaskMapperFunction = this::getTask;
    }

    @NonNull
    private Task getTask(@NonNull Cursor c) {
        String itemId = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_ENTRY_ID));
        String title = c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_TITLE));
        String description =
                c.getString(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_DESCRIPTION));
        boolean completed =
                c.getInt(c.getColumnIndexOrThrow(TaskEntry.COLUMN_NAME_COMPLETED)) == 1;
        return new Task(title, description, itemId, completed);


    }

    public static synchronized TasksLocalDataSource getInstance(
            @NonNull Context context,
            @NonNull BaseSchedulerProvider schedulerProvider) {
        if (INSTANCE == null) {
            INSTANCE = new TasksLocalDataSource(context, schedulerProvider);
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }
    @Override
    public Observable<List<Task>> getTasks() {
        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED
        };
        String sql = String.format("SELECT %s FROM %s", TextUtils.join(",", projection), TaskEntry.TABLE_NAME);
        return mDatabaseHelper.createQuery(TaskEntry.TABLE_NAME, sql)
                .mapToList(mTaskMapperFunction);
    }

    @Override
    public Observable<Task> getTask(@NonNull String taskId) {
        String[] projection = {
                TaskEntry.COLUMN_NAME_ENTRY_ID,
                TaskEntry.COLUMN_NAME_TITLE,
                TaskEntry.COLUMN_NAME_DESCRIPTION,
                TaskEntry.COLUMN_NAME_COMPLETED
        };
        String sql = String.format("SELECT %s FROM %s WHERE %s LIKE ?", TextUtils.join(",", projection), TaskEntry.TABLE_NAME);
        return mDatabaseHelper.createQuery(TaskEntry.TABLE_NAME, sql,taskId).
                mapToOneOrDefault(mTaskMapperFunction, null);
    }

    @Override
    public void saveTask(@NonNull Task task) {
        Log.d(TAG, "saveTask: begin");
        checkNotNull(task);
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_ENTRY_ID, task.getId());
        values.put(TaskEntry.COLUMN_NAME_TITLE, task.getTitle());
        values.put(TaskEntry.COLUMN_NAME_DESCRIPTION, task.getDescription());
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, task.isCompleted());
        long insert = mDatabaseHelper.insert(TaskEntry.TABLE_NAME, values, SQLiteDatabase.CONFLICT_REPLACE);
        Log.d(TAG, "saveTask: end" + insert);

    }

    @Override
    public void completeTask(@NonNull Task task) {
        completeTask(task.getId());
    }

    @Override
    public void completeTask(@NonNull String taskId) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, true);
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        mDatabaseHelper.update(TaskEntry.TABLE_NAME,values,selection, selectionArgs);
    }

    @Override
    public void activateTask(@NonNull Task task) {
        activateTask(task.getId());
    }

    @Override
    public void activateTask(@NonNull String taskId) {
        ContentValues values = new ContentValues();
        values.put(TaskEntry.COLUMN_NAME_COMPLETED, false);
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        mDatabaseHelper.update(TaskEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    @Override
    public void clearCompletedTasks() {
        String selection = TaskEntry.COLUMN_NAME_COMPLETED + " LIKE ?";
        String[] selectionArgs = {"1"};
        mDatabaseHelper.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public void refreshTasks() {

    }

    @Override
    public void deleteAllTasks() {
        mDatabaseHelper.delete(TaskEntry.TABLE_NAME, null);
    }

    @Override
    public void deleteTask(@NonNull String taskId) {
        String selection = TaskEntry.COLUMN_NAME_ENTRY_ID + " LIKE ?";
        String[] selectionArgs = {taskId};
        mDatabaseHelper.delete(TaskEntry.TABLE_NAME, selection, selectionArgs);
    }
}
