package hm.com.mvp.test;

import android.content.Context;
import android.support.annotation.NonNull;

import hm.com.mvp.data.source.TasksRepository;
import hm.com.mvp.data.source.local.TasksLocalDataSource;
import hm.com.mvp.test.data.FakeTasksRemoteDataSource;
import hm.com.mvp.util.schedulers.BaseSchedulerProvider;
import hm.com.mvp.util.schedulers.SchedulerProvider;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
public class Injection {

    public static TasksRepository provideTasksRepository(@NonNull Context context) {
        checkNotNull(context);
        return TasksRepository.getInstance(FakeTasksRemoteDataSource.getInstance(),
                TasksLocalDataSource.getInstance(context, provideSchedulerProvider()));
    }

    public static BaseSchedulerProvider provideSchedulerProvider() {
        return SchedulerProvider.getInstance();
    }
}
