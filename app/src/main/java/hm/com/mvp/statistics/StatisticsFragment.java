package hm.com.mvp.statistics;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hm.com.mvp.R;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/12
 */
public class StatisticsFragment extends Fragment implements StatisticsContract.View{

    private TextView mStatisticsTV;
    private StatisticsContract.Presenter mPresenter;


    public static StatisticsFragment newInstance(){ return new StatisticsFragment();}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.statistics_frag, container, false);
        mStatisticsTV = (TextView) root.findViewById(R.id.statistics);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unsubscribe();
    }

    @Override
    public void setPresenter(StatisticsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setProgressIndicator(boolean active) {
        if (active) {
            mStatisticsTV.setText(getString(R.string.loading));
        }
    }

    @Override
    public void showStatistics(int numberOfIncompleteTasks, int numberOfCompletedTasks) {
        if (numberOfCompletedTasks == 0 && numberOfIncompleteTasks == 0) {
            mStatisticsTV.setText(R.string.statistics_no_tasks);
        }else{
            String displayString = getString(R.string.statistics_active_tasks)+ " "
                    + numberOfIncompleteTasks + "\n" + getString(R.string.statistics_completed_tasks) + " " + numberOfCompletedTasks;
            mStatisticsTV.setText(displayString);
        }
    }

    @Override
    public void showLoadingStatisticsError() {
        mStatisticsTV.setText(getString(R.string.statistics_error));
    }
}
