package com.payne.refreshandloadmore;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;
import android.widget.TextView;

public class RefreshLayout extends ViewGroup implements View.OnClickListener {
    private RefreshCallBack mRefreshCallBack;
    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 头部view
     */
    private View mHeadView;
    /**
     * 头部高度
     */
    private int mHeadHeight;
    /**
     * 头部view
     */
    private View mFootView;
    /**
     * 头部高度
     */
    private int mFootHeight;
    /**
     * 点击加载view
     */
    private View mClickLoadMoreView;
    /**
     * 点击加载更多高度
     */
    private int mClickLoadMoreHeight;
    /**
     * 用于刷新的view
     */
    private View mContentView;

    /**
     * 用于完成滚动操作的实例
     */
    private Scroller mScroller;

    /**
     * 判定为拖动的最小移动像素数
     */
    private int mTouchSlop;

    /**
     * 手机按下时的屏幕坐标
     */
    private float mYDown;

    /**
     * 手机当时所处的屏幕坐标
     */
    private float mYMove;

    /**
     * 上次触发ACTION_MOVE事件时的屏幕坐标
     */
    private float mYLastMove;

    /**
     * 界面可滚动的上边界
     */
    private int topBorder;

    /**
     * 界面可滚动的底边界
     */
    private int bottomBorder;
    /**
     * 垂直偏移
     */
    private final static int offset = 100;


    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }


    public RefreshLayout(Context context) {
        super(context);
        init(context);

    }

    public RefreshLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        mContentView = getChildAt(3);

        mHeadHeight = mHeadView.getMeasuredHeight();
        mFootHeight = mFootView.getMeasuredHeight();
        mClickLoadMoreHeight = mClickLoadMoreView.getMeasuredHeight();



    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed) {
            if (mContentView == null) return;
            mHeadView.layout(0, 0, mHeadView.getMeasuredWidth(), mHeadView.getMeasuredHeight());
            mContentView.layout(0, mHeadHeight, mContentView.getMeasuredWidth(),
                    mHeadHeight + mContentView.getMeasuredHeight());
            mFootView.layout(0, mHeadHeight + mContentView.getMeasuredHeight(), mFootView
                    .getMeasuredWidth(), mHeadHeight + mContentView.getMeasuredHeight() +
                    mFootHeight);


            topBorder = mHeadView.getTop();
            if (mHeadHeight + mContentView.getMeasuredHeight() -
                    getMeasuredHeight() >= 0) {
                bottomBorder = mHeadHeight + mContentView.getMeasuredHeight() + mFootHeight -
                        getMeasuredHeight();
                mFootView.setVisibility(VISIBLE);
                mClickLoadMoreView.setVisibility(GONE);


            } else {
                bottomBorder = 0;
                mFootView.setVisibility(GONE);
                mClickLoadMoreView.setVisibility(VISIBLE);

                mClickLoadMoreView.layout(0, mHeadHeight + mContentView.getMeasuredHeight(),
                        mClickLoadMoreView.getMeasuredWidth(), mHeadHeight + mContentView
                                .getMeasuredHeight() + mClickLoadMoreHeight);
            }

            scrollBy(0, mHeadView.getMeasuredHeight());
        }
    }


    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child == mClickLoadMoreView) {
            mClickLoadMoreView.setOnClickListener(this);

        }
    }


    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mYDown = ev.getRawY();
                mYLastMove = mYDown;
                break;
            case MotionEvent.ACTION_MOVE:

                mYMove = ev.getRawY();
                float diff = (mYMove - mYDown);
                mYLastMove = mYMove;

                boolean canUp = mContentView.canScrollVertically(1);
                boolean canDown = mContentView.canScrollVertically(-1);

                int scrollY = getScrollY();
                if (scrollY > mHeadHeight) {
                    return true;
                }
                if (scrollY <= mHeadHeight) {
                    if (diff > 0 && !canDown) {
                        return true;
                    }
                    if (diff < 0 && !canUp) {
                        return true;
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                mYMove = event.getRawY();
                int scrolledY = (int) (mYLastMove - mYMove);
                int a = getScrollY();
                if (scrolledY < 0) {//下拉
                    if (a >= topBorder - offset) {
                        scrollBy(0, scrolledY);
                        mYLastMove = mYMove;
                    }
                    if (a < 0) {
                        Log.e("mHeadHeight", "mHeadHeight");
                        TextView textView = (TextView) mHeadView.findViewById(R.id.tv_header);
                        textView.setText("释放刷新");
                    } else {
                        TextView textView = (TextView) mHeadView.findViewById(R.id.tv_header);
                        textView.setText("下拉刷新");
                    }
                }
                if (scrolledY > 0) {//上拉
                    if (bottomBorder > 0) {
                        if (a <= bottomBorder + offset) {
                            scrollBy(0, scrolledY);
                            mYLastMove = mYMove;
                        }
                    }
                }


                break;
            case MotionEvent.ACTION_UP:
                // 当手指抬起时，根据当前的滚动值来判定应该滚动到哪个子控件的界面
                // 第二步，调用startScroll()方法来初始化滚动数据并刷新界面

                if (getScrollY() < mHeadHeight) {
                    mScroller.startScroll(0, getScrollY(), 0, mHeadHeight - getScrollY());
                }
                if (getScrollY() > bottomBorder && bottomBorder > 0) {
                    mScroller.startScroll(0, getScrollY(), 0, bottomBorder - getScrollY());
                }
                invalidate();
                break;
        }
        return true;
    }

    @Override
    public void computeScroll() {
        // 第三步，重写computeScroll()方法，并在其内部完成平滑滚动的逻辑
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            invalidate();
        }
    }

    // 第一步，创建Scroller的实例
    private void init(Context context) {
        mContext = context;
        mScroller = new Scroller(context);
        ViewConfiguration configuration = ViewConfiguration.get(context);
        mTouchSlop = configuration.getScaledTouchSlop();


        mHeadView = LayoutInflater.from(mContext).inflate(R.layout.header_refresh, null);
        addView(mHeadView, 0);
        LayoutParams headParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        mHeadView.setLayoutParams(headParams);

        mFootView = LayoutInflater.from(mContext).inflate(R.layout.foot_loadmore, null);
        addView(mFootView, 1);
        LayoutParams footParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        footParams.width = LayoutParams.MATCH_PARENT;
        footParams.height = LayoutParams.WRAP_CONTENT;
        mFootView.setLayoutParams(footParams);

        mClickLoadMoreView = LayoutInflater.from(mContext).inflate(R.layout.click_loadmore, null);
        addView(mClickLoadMoreView, 2);

        LayoutParams clickLoadMoreParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        mClickLoadMoreView.setLayoutParams(clickLoadMoreParams);


    }

    public void setHeadView(View headView) {
        removeView(mHeadView);
        mHeadView = headView;
        LayoutParams headParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams
                .WRAP_CONTENT);
        mHeadView.setLayoutParams(headParams);
        addView(mHeadView, 0);

    }

    public void endRefresh() {

    }

    public void setRefreshCallBack(RefreshCallBack refreshCallBack) {
        mRefreshCallBack = refreshCallBack;
    }

    @Override
    public void onClick(View v) {
        if (v == mClickLoadMoreView && mRefreshCallBack != null) {
            mRefreshCallBack.beginLoadMore();
        }
    }


}
