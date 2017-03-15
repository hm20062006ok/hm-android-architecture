package hm.com.mvp.statistics;


import android.support.annotation.NonNull;
import android.util.Pair;

import hm.com.mvp.data.Task;
import hm.com.mvp.data.source.TasksRepository;
import hm.com.mvp.util.schedulers.BaseSchedulerProvider;
import rx.Observable;
import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/12
 */
public class StatisticsPresenter implements StatisticsContract.Presenter {

    private final TasksRepository mTasksRepository;
    private final StatisticsContract.View mStatisticsView;
    private final BaseSchedulerProvider mSchedulerProvider;
    private final CompositeSubscription mSubscriptions;

    public StatisticsPresenter(@NonNull TasksRepository tasksRepository,
                              @NonNull StatisticsContract.View statisticsView,
                              @NonNull BaseSchedulerProvider schedulerProvider){
        mTasksRepository = checkNotNull(tasksRepository, "tasksRepository cannot be null!");
        mStatisticsView = checkNotNull(statisticsView, "statisticsView cannot be null!");
        mSchedulerProvider = checkNotNull(schedulerProvider, "schedulerProvider cannot be null");

        mSubscriptions = new CompositeSubscription();
        statisticsView.setPresenter(this);
    }
    @Override
    public void subscribe() {
        loadStatistics();
    }

    private void loadStatistics() {
        mStatisticsView.setProgressIndicator(true);
        Observable<Task> tasks = mTasksRepository
                .getTasks()
                .flatMap(Observable::from);
        Observable<Integer> completedTasks = tasks.filter(Task::isCompleted).count();
        Observable<Integer> activeTasks = tasks.filter(Task::isActive).count();
        Subscription subscription = Observable
                .zip(completedTasks, activeTasks, (completed, active) -> Pair.create(active, completed))
                .subscribeOn(mSchedulerProvider.computation())
                .observeOn(mSchedulerProvider.ui())
                .subscribe(
                        stats -> mStatisticsView.showStatistics(stats.first, stats.second),

                        throwable -> mStatisticsView.showLoadingStatisticsError(),

                        () -> mStatisticsView.setProgressIndicator(false)
                );
        mSubscriptions.add(subscription);
    }

    @Override
    public void unsubscribe() {
        mSubscriptions.clear();
    }
}
