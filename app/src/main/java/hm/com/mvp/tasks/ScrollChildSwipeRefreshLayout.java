package hm.com.mvp.tasks;

import android.content.Context;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;

/**
 * 可指定由哪个 子View 触发刷新动作
 * Created by HuMiao on  2017/3/11
 */
public class ScrollChildSwipeRefreshLayout extends SwipeRefreshLayout {
    private View mScrollUpChild;

    public ScrollChildSwipeRefreshLayout(Context context) {
        super(context);
    }

    public ScrollChildSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean canChildScrollUp() {
        if (null != mScrollUpChild) {
            // < 0, 检查是否可在垂直方向向上滚动
            // > 0, 检查是否可在垂直方向向下滚动
            return ViewCompat.canScrollVertically(mScrollUpChild, -1);
        }
        return super.canChildScrollUp();
    }

    public void setScrollUpChild(View view){
        mScrollUpChild = view;
    }
}
