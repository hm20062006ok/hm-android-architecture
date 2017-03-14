package hm.com.mvp.statistics;

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
import hm.com.mvp.tasks.TaskActivity;
import hm.com.mvp.test.Injection;
import hm.com.mvp.util.ActivityUtils;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/10
 */
public class StatisticsActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistics_act);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("Statistics");
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (null != navigationView) {
            setUpDrawerContent(navigationView);
        }

        StatisticsFragment statisticsFragment = (StatisticsFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (null == statisticsFragment) {
            statisticsFragment = StatisticsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    statisticsFragment, R.id.contentFrame);
        }

        new StatisticsPresenter(
                Injection.provideTasksRepository(getApplicationContext()), statisticsFragment,
                Injection.provideSchedulerProvider());

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

    private void setUpDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                menuItem -> {
                    switch (menuItem.getItemId()) {
                        case R.id.list_navigation_menu_item:
                            Intent intent = new Intent(StatisticsActivity.this, TaskActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            break;
                        case R.id.statistics_navigation_menu_item:
                            //Do nothing
                            break;
                    }
                    menuItem.setChecked(true);
                    mDrawerLayout.closeDrawers();
                    return true;
                }
        );
    }
}
