package hm.com.mvp.util.schedulers;

import android.support.annotation.NonNull;

import rx.Scheduler;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
public interface BaseSchedulerProvider {
    @NonNull
    Scheduler computation();

    @NonNull
    Scheduler io();

    @NonNull
    Scheduler ui();
}
