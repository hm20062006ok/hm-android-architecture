package hm.com.mvp.tasks;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.PopupMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hm.com.mvp.R;
import hm.com.mvp.addedittask.AddEditTaskActivity;
import hm.com.mvp.data.Task;
import hm.com.mvp.taskdetail.TaskDetailActivity;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * 显示任务列表. 用户可以筛选显示所有, 活动中的或者以完成的
 * Created by HuMiao on  2017/3/10
 */
public class TasksFragment extends Fragment implements TasksContract.View {


    private static final String TAG = TasksFragment.class.getName();
    TaskItemListener mItemListener = new TaskItemListener() {

        @Override
        public void onTaskClick(Task clickedTask) {
            mPresenter.openTaskDetails(clickedTask);
        }

        @Override
        public void onCompleteTaskClick(Task completedTask) {
            mPresenter.completeTask(completedTask);
        }

        @Override
        public void onActivateTaskClick(Task activatedTask) {
            mPresenter.activeteTask(activatedTask);
        }
    };

    private TasksAdapter mListAdapter;

    private TasksContract.Presenter mPresenter;


    private TextView mFilteringLabelView;

    private LinearLayout mTasksView;

    private View mNoTasksView;

    private ImageView mNoTaskIcon;

    private TextView mNoTaskMainView;

    private TextView mNoTaskAddView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new TasksAdapter(new ArrayList<>(0), mItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.tasks_frag, container, false);
        ListView listView = (ListView) root.findViewById(R.id.tasks_list);
        listView.setAdapter(mListAdapter);
        mFilteringLabelView = (TextView) root.findViewById(R.id.filteringLabel);
        mTasksView = (LinearLayout) root.findViewById(R.id.tasksLL);

        mNoTasksView = root.findViewById(R.id.noTasks);
        mNoTaskIcon = (ImageView) root.findViewById(R.id.noTaskIcon);
        mNoTaskMainView = (TextView) root.findViewById(R.id.noTasksMain);
        mNoTaskAddView = (TextView) root.findViewById(R.id.noTasksAdd);
        mNoTaskAddView.setOnClickListener(view -> showAddTask());

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_add_task);
        fab.setImageResource(R.drawable.ic_add);
        fab.setOnClickListener(__ -> mPresenter.addNewTask());

        ScrollChildSwipeRefreshLayout swipefreshLayout = (ScrollChildSwipeRefreshLayout) root.findViewById(R.id.refresh_layout);
        swipefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark)
        );

        swipefreshLayout.setScrollUpChild(listView);
        swipefreshLayout.setOnRefreshListener(() -> mPresenter.loadTasks(false));

        //设置有自己的 Menu
        setHasOptionsMenu(true);
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.subscribe();
    }

    @Override
    public void onPause() {
        super.onPause();
        mPresenter.unsubscribe();
    }

    public TasksFragment() {

    }

    public static TasksFragment newInstance() {
        return new TasksFragment();
    }


    @Override
    public void setPresenter(@NonNull TasksContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.tasks_fragment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void showFilteringPopUpMenu() {
        PopupMenu popup = new PopupMenu(getContext(), getActivity().findViewById(R.id.menu_filter));
        popup.getMenuInflater().inflate(R.menu.filter_tasks, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.active:
                    mPresenter.setFiltering(TasksFilterType.ACTIVE_TASKS);
                    break;
                case R.id.completed:
                    mPresenter.setFiltering(TasksFilterType.COMPLETED_TASKS);
                    break;
                default:
                    mPresenter.setFiltering(TasksFilterType.ALL_TASKS);
                    break;
            }
            mPresenter.loadTasks(false);
            return true;
        });
        popup.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPresenter.result(requestCode, resultCode);
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_clear:
                mPresenter.clearCompletedTasks();
                break;
            case R.id.menu_filter:
                showFilteringPopUpMenu();
                break;
            case R.id.menu_refresh:
                mPresenter.loadTasks(true);
                break;
        }
        return true;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (null == getView()) {
            return;
        }
        final SwipeRefreshLayout srl =
                (SwipeRefreshLayout) getView().findViewById(R.id.refresh_layout);
        srl.post(() -> srl.setRefreshing(active));
    }

    @Override
    public void showTasks(List<Task> tasks) {
        mListAdapter.replaceData(tasks);
        mTasksView.setVisibility(View.VISIBLE);
        mNoTasksView.setVisibility(View.GONE);
    }

    @Override
    public void showAddTask() {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        startActivityForResult(intent, AddEditTaskActivity.REQUEST_ADD_TASK);
    }

    @Override
    public void showTaskDetailsUi(String taskId) {
        Intent intent = new Intent(getContext(), TaskDetailActivity.class);
        intent.putExtra(TaskDetailActivity.EXTRA_TASK_ID, taskId);
        startActivity(intent);
    }

    @Override
    public void showTaskMarkedComplete() {
        showMessage(getString(R.string.task_marked_complete));
    }

    @Override
    public void showTaskMarkedActive() {
        showMessage(getString(R.string.task_marked_active));
    }

    @Override
    public void showCompletedTasksCleared() {
        showMessage(getString(R.string.task_completed_cleared));
    }

    @Override
    public void showLoadingTasksError() {
        showMessage(getString(R.string.error_while_loading_tasks));
    }

    @Override
    public void showNoTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_all),
                R.drawable.ic_assignment_turned_in_24dp,
                false);
    }

    @Override
    public void showActiveFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_active));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showCompletedFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_completed));
    }


    @Override
    public void showAllFilterLabel() {
        mFilteringLabelView.setText(getResources().getString(R.string.label_all));
    }

    @Override
    public void showNoActiveTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_active),
                R.drawable.ic_check_circle_24dp,
                false
        );
    }

    @Override
    public void showNoCompletedTasks() {
        showNoTasksViews(
                getResources().getString(R.string.no_tasks_completed),
                R.drawable.ic_verified_user_24dp,
                false
        );
    }

    @Override
    public void showSuccessfullySavedMessage() {
        showMessage(getString(R.string.successfully_saved_task_message));
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    private void showNoTasksViews(String mainText, int iconRes, boolean showAddView) {
        mTasksView.setVisibility(View.GONE);
        mNoTasksView.setVisibility(View.VISIBLE);

        mNoTaskMainView.setText(mainText);
        mNoTaskIcon.setImageDrawable(getResources().getDrawable(iconRes));
        mNoTaskAddView.setVisibility(showAddView ? View.VISIBLE : View.GONE);
    }

    private static class TasksAdapter extends BaseAdapter {
        private  TaskItemListener mItemListener;
        private List<Task> mTasks;

        public TasksAdapter(List<Task> tasks, TaskItemListener itemListener) {
            setList(tasks);
            mItemListener = itemListener;
        }

        public void replaceData(List<Task> tasks){
            setList(tasks);
            notifyDataSetChanged();
        }

        private void setList(List<Task> tasks) {
            mTasks = checkNotNull(tasks);
        }

        @Override
        public int getCount() {
            return mTasks.size();
        }

        @Override
        public Task getItem(int position) {
            return mTasks.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View rowView = convertView;
            if (null == rowView) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.task_item, parent, false);
            }
            final Task task = getItem(position);
            TextView titleTV = (TextView) rowView.findViewById(R.id.title);
            titleTV.setText(task.getTitleForList());

            CheckBox completeCB = (CheckBox) rowView.findViewById(R.id.complete);
            completeCB.setChecked(task.isCompleted());
            if (task.isCompleted()) {
                rowView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.list_completed_touch_feedback));
            }else{
                rowView.setBackgroundDrawable(parent.getContext().getResources().getDrawable(R.drawable.touch_feedback));
            }
            completeCB.setOnClickListener(__ -> {
                if (!task.isCompleted()) {
                    mItemListener.onCompleteTaskClick(task);
                }else{
                    mItemListener.onActivateTaskClick(task);
                }
            });

            rowView.setOnClickListener(__ -> mItemListener.onTaskClick(task));
            return rowView;
        }
    }

    public interface TaskItemListener{
        void onTaskClick(Task clickedTask);
        void onCompleteTaskClick(Task completedTask);
        void onActivateTaskClick(Task activatedTask);
    }
}
