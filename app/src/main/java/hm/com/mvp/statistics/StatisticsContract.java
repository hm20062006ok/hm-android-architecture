package hm.com.mvp.statistics;

import hm.com.mvp.BasePresenter;
import hm.com.mvp.BaseView;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/12
 */
interface StatisticsContract {
    interface View extends BaseView<Presenter>{
        void setProgressIndicator(boolean active);

        void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks);

        void showLoadingStatisticsError();
    }

    interface Presenter extends BasePresenter{

    }
}
