package hm.com.mvp.tasks;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import hm.com.mvp.R;
import hm.com.mvp.statistics.StatisticsActivity;
import hm.com.mvp.test.Injection;
import hm.com.mvp.util.ActivityUtils;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
public class TaskActivity extends AppCompatActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    private DrawerLayout mDrawerLayout;
    private TasksPresenter mTaskPresenter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks_act);

        //设置 toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        //设置 navigation drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        TasksFragment taskFragment = (TasksFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        if (taskFragment == null) {
            taskFragment = TasksFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(),taskFragment, R.id.contentFrame);
        }

        //创建 presenter
        mTaskPresenter = new TasksPresenter(
                Injection.provideTasksRepository(getApplicationContext()),
                taskFragment,
                Injection.provideSchedulerProvider());

        //载入之前保存的可用数据
        if (savedInstanceState != null) {
            TasksFilterType  currentFiltering = (TasksFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mTaskPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mTaskPresenter.getFiltering());

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(menuItem ->{
            switch (menuItem.getItemId()) {
                case R.id.list_navigation_menu_item:
                    break;
                case R.id.statistics_navigation_menu_item:
                    Intent intent = new Intent(TaskActivity.this, StatisticsActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    break;
                default:
                    break;
            }
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            return true;
        });
    }
}
