package hm.com.mvp.taskdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;


import hm.com.mvp.R;
import hm.com.mvp.addedittask.AddEditTaskActivity;
import hm.com.mvp.addedittask.AddEditTaskFragment;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * ${Description}
 * Created by HuMiao on  2017/3/12
 */
public class TaskDetailFragment extends Fragment implements TaskDetailContract.View{

    @NonNull
    private static final String ARGUMENT_TASK_ID = "TASK_ID";
    private static final int REQUEST_EDIT_TASK = 0x33;
    private TextView mDetailTitle;
    private TextView mDetailDescription;
    private CheckBox mDetailCompleteStatus;
    private TaskDetailContract.Presenter mPresenter;

    public static TaskDetailFragment newInstance(@Nullable String taskId) {
        Bundle arguments = new Bundle();
        arguments.putString(ARGUMENT_TASK_ID, taskId);
        TaskDetailFragment fragment = new TaskDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.taskdetail_frg, container, false);
        setHasOptionsMenu(true);

        mDetailTitle = (TextView) root.findViewById(R.id.task_detail_title);
        mDetailDescription = (TextView) root.findViewById(R.id.task_detail_description);
        mDetailCompleteStatus = (CheckBox) root.findViewById(R.id.task_detail_complete);

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task);
        fab.setOnClickListener(__ -> mPresenter.editTask());
        return root;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.taskdetail_frgment_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete:
                mPresenter.deleteTask();
                return true;
        }
        return false;
    }

    @Override
    public void setPresenter(TaskDetailContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            mDetailTitle.setText("");
            mDetailDescription.setText(R.string.loading);
        }
    }

    @Override
    public void showMissingTask() {
        mDetailTitle.setText("");
        mDetailDescription.setText(R.string.no_data);
    }

    @Override
    public void hideTitle() {
        mDetailTitle.setVisibility(View.GONE);
    }

    @Override
    public void showTitle(String title) {
        mDetailTitle.setVisibility(View.VISIBLE);
        mDetailTitle.setText(title);
    }

    @Override
    public void hideDescription() {
        mDetailDescription.setVisibility(View.GONE);
    }

    @Override
    public void showDescription(String description) {
        mDetailDescription.setVisibility(View.VISIBLE);
        mDetailDescription.setText(description);
    }

    @Override
    public void showCompletionStatus(boolean complete) {
        checkNotNull(mDetailCompleteStatus);
        mDetailCompleteStatus.setChecked(complete);
        mDetailCompleteStatus.setOnCheckedChangeListener((buttonView, isChecked) ->{
            if (isChecked) {
                mPresenter.completeTask();
            }else{
                mPresenter.activateTask();
            }
        });
    }

    //跳转到编辑页面
    @Override
    public void showEditTask(String taskId) {
        Intent intent = new Intent(getContext(), AddEditTaskActivity.class);
        intent.putExtra(AddEditTaskFragment.ARGUMENT_EDIT_TASK_ID, taskId);
        startActivityForResult(intent, REQUEST_EDIT_TASK);
    }

    @Override
    public void showTaskDeleted() {
        getActivity().finish();
    }

    @Override
    public void showTaskMarkedComplete() {
        Snackbar.make(getView(),"Task marked complete", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showTaskMarkedActive() {
        Snackbar.make(getView(), "Task marked active", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode  == REQUEST_EDIT_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                getActivity().finish();
                return;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
