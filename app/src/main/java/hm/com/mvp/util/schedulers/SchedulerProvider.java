package hm.com.mvp.util.schedulers;

import android.support.annotation.NonNull;

import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
public class SchedulerProvider  implements BaseSchedulerProvider{
    @NonNull
    private static SchedulerProvider INSTANCE;

    private SchedulerProvider(){
    }

    public static synchronized BaseSchedulerProvider getInstance() {
        if(INSTANCE == null){
            INSTANCE = new SchedulerProvider();
        }
        return  INSTANCE;
    }

    @NonNull
    @Override
    public Scheduler computation() {
        return Schedulers.computation();
    }

    @NonNull
    @Override
    public Scheduler io() {
        return Schedulers.io();
    }

    @NonNull
    @Override
    public Scheduler ui() {
        return AndroidSchedulers.mainThread();
    }
}
