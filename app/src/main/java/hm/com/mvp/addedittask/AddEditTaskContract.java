package hm.com.mvp.addedittask;

import hm.com.mvp.BasePresenter;
import hm.com.mvp.BaseView;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/12
 */
interface AddEditTaskContract {
    interface View extends BaseView<Presenter> {
        void showEmptyTaskError();
        void showTasksList();
        void setTitle(String title);
        void setDescription(String description);
        boolean isActive();
    }

    interface Presenter extends BasePresenter {
        void saveTask(String title, String description);
        void populateTask();
        boolean isDataMissing();
    }
}
