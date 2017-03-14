package hm.com.mvp.taskdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import hm.com.mvp.R;
import hm.com.mvp.test.Injection;
import hm.com.mvp.util.ActivityUtils;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/11
 */
public class TaskDetailActivity extends AppCompatActivity{
    public static final String EXTRA_TASK_ID = "TASK_ID";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail_act);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        TaskDetailFragment taskDetailFragment = (TaskDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (taskDetailFragment == null) {
            taskDetailFragment = TaskDetailFragment.newInstance(taskId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    taskDetailFragment, R.id.contentFrame);
        }

        new TaskDetailPresenter(taskId, Injection.provideTasksRepository(getApplicationContext()),
                taskDetailFragment,
                Injection.provideSchedulerProvider());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
