package com.framelibrary.widget.recycleview;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * 上拉加载RecyclerView
 */

public class BaseLoadMoreRecycleView extends RecyclerView implements View.OnTouchListener {

    private OnScrollerListener onScrollerListener;
    private BaseLoadMoreRecycleAdapter baseLoadMoreRecycleAdapter;
    private int lastVisibleItem;
    private LinearLayoutManager linearLayoutManager;
    private OnLoadMoreListener onLoadMoreListener;
    private OnTouchListener onTouchListener;
    private boolean isRefresh;

    public BaseLoadMoreRecycleView(Context context) {
        super(context);
        init();
    }

    public BaseLoadMoreRecycleView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BaseLoadMoreRecycleView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        setOnTouchListener(this);
    }

    private void init() {
        addOnScrollListener(new ImageAutoLoadScrollListener());
    }

    public void setOnScrollerListener(OnScrollerListener onScrollerListener) {
        this.onScrollerListener = onScrollerListener;
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        this.onTouchListener = onTouchListener;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener onLoadMoreListener) {
        this.onLoadMoreListener = onLoadMoreListener;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (this.onTouchListener != null) {
            this.onTouchListener.onTouch(v, event);
        }
        return isRefresh;
    }

    public class ImageAutoLoadScrollListener extends OnScrollListener {


        private ImageAutoLoadScrollListener() {
        }

        //用来标记是否正在向最后一个滑动，既是否向下滑动
        boolean isSlidingToLast = false;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (onScrollerListener != null)
                onScrollerListener.onScrolled(recyclerView, dx, dy);

            if (linearLayoutManager == null) {
                LayoutManager layoutManager = getLayoutManager();
                if (layoutManager == null || !(layoutManager instanceof LinearLayoutManager)) {
                    return;
                }
                linearLayoutManager = (LinearLayoutManager) layoutManager;
            }
            lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
            //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
            if (dy > 0) {
                //大于0表示，正在向下滚动
                isSlidingToLast = true;
            } else {
                //小于等于0 表示停止或向上滚动
                isSlidingToLast = false;
            }
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            try {
                if (baseLoadMoreRecycleAdapter == null) {
                    Adapter adapter = getAdapter();
                    if (adapter != null && adapter instanceof BaseLoadMoreRecycleAdapter)
                        baseLoadMoreRecycleAdapter = (BaseLoadMoreRecycleAdapter) getAdapter();
                    else
                        return;
                }
                switch (newState) {
                    case SCROLL_STATE_IDLE: // The RecyclerView is not currently scrolling.
                        int totalItemCount = baseLoadMoreRecycleAdapter.getItemCount();
                        // 判断是否滚动到底部
                        if (lastVisibleItem == (totalItemCount - 1) && isSlidingToLast) {
                            //加载更多功能的代码
                            if (!baseLoadMoreRecycleAdapter.isFadeTips() && lastVisibleItem + 1 == totalItemCount && totalItemCount > 1) {
                                if (onLoadMoreListener != null)
                                    onLoadMoreListener.onLoadMore();
                            }
                            // 如果隐藏了提示条，我们又上拉加载时，那么最后一个条目就要比getItemCount要少2
                            if (baseLoadMoreRecycleAdapter.isFadeTips() && lastVisibleItem + 2 == totalItemCount && totalItemCount > 1) {
                                if (onLoadMoreListener != null)
                                    onLoadMoreListener.onLoadMore();
                            }
                        }

                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (onScrollerListener != null)
                onScrollerListener.onScrollStateChanged(recyclerView, newState);
        }

    }

    public void refreshStart() {
        this.isRefresh = true;
    }

    public void refreshFinish() {
        this.isRefresh = false;
    }

    public interface OnScrollerListener {
        void onScrollStateChanged(RecyclerView recyclerView, int newState);

        void onScrolled(RecyclerView recyclerView, int dx, int dy);
    }

    public interface OnTouchListener {
        void onTouch(View v, MotionEvent event);
    }

}
