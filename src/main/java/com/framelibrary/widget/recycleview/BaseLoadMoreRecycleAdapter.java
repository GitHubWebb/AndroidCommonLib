package com.framelibrary.widget.recycleview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.framelibrary.R;
import com.framelibrary.util.bean.BaseBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 上拉加载RecyclerView adapter
 */

public abstract class BaseLoadMoreRecycleAdapter extends BaseRecycleViewAdapter {
    protected String TAG = getClass().getSimpleName();
    private LayoutInflater layoutInflater;
    private int footerLayout = 0;
    protected boolean hasMore = false;
    private boolean isShowFooter = false;
    private boolean isShowHeader = false;
    private boolean fadeTips;
    private Handler mHandler = new Handler(Looper.getMainLooper()); //获取主线程的Handler

    protected BaseLoadMoreRecycleAdapter(Context context, List<BaseBean> baseBeanList) {
        layoutInflater = LayoutInflater.from(context);
        this.baseBeanList = baseBeanList;
        if (this.baseBeanList == null) {
            this.baseBeanList = new ArrayList<>();
        }
    }

    // 暴露接口，更新数据源，并修改hasMore的值，如果有增加数据，hasMore为true，否则为false
    public void updateList(List<BaseBean> newDataList, boolean hasMore) {
        if (!newDataList.isEmpty()) {
            if (baseBeanList == null) {
                baseBeanList = new ArrayList<>();
            }

            // 在原有的数据之上增加新数据
            if (newDataList != null) {
                int previousSize = baseBeanList.size();
                /*baseBeanList.clear();
                if (previousSize > 0)
                    notifyItemRangeRemoved(1, previousSize);
*/
                baseBeanList.addAll(newDataList);
                notifyItemRangeInserted(baseBeanList.size() > 1 ? 1 : 0, newDataList.size());
            }
        }
        this.hasMore = hasMore;
    }

    /**
     * 重置数据
     */
    public abstract void resetData();

    /**
     * @param showFooter 是否显示尾部
     */
    public void setShowFooter(boolean showFooter) {
        isShowFooter = showFooter;
    }

    /**
     * @return 是否显示尾部
     */
    private boolean isShowFooter() {
        return isShowFooter;
    }

    /**
     * @return 是否显示头部
     */
    private boolean isShowHeader() {
        return isShowHeader;
    }

    /**
     * @param showHeader 是否显示头部
     */
    public void setShowHeader(boolean showHeader) {
        isShowHeader = showHeader;
    }

    /**
     * @return 尾部布局
     */
    public int getFooterLayout() {
        return footerLayout;
    }

    /**
     * @param footerLayout 尾部布局
     */
    public void setFooterLayout(int footerLayout) {
        this.footerLayout = footerLayout;
    }

    /**
     * @return 是否显示加载条
     */
    public boolean isFadeTips() {
        return fadeTips;
    }

    @NonNull
    @Override
    public BaseRecycleViewHolder onCreateRecycleViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == BaseRecycleViewAdapter.FOOTER_TYPE)
            return new BaseRecycleFooterViewHolder(layoutInflater.inflate(footerLayout == 0 ? R.layout.item_recyle_footview : footerLayout, parent, false));
        else if (viewType == BaseRecycleViewAdapter.HEADER_TYPE)
            return onCreateHeaderViewHolder(layoutInflater, parent, viewType);
        return onCreateNormalViewHolder(layoutInflater, parent, viewType);
    }

    @Override
    public void onBindRecycleViewHolder(@NonNull BaseRecycleViewHolder holder, int position) {
        super.onBindRecycleViewHolder(holder, position);
        int viewType = getItemViewType(position);
        if (viewType == BaseRecycleViewAdapter.FOOTER_TYPE) {
            if (holder instanceof BaseRecycleFooterViewHolder) {
                final BaseRecycleFooterViewHolder baseRecycleFooterViewHolder = (BaseRecycleFooterViewHolder) holder;
                // 之所以要设置可见，是因为我在没有更多数据时会隐藏了这个footView
                baseRecycleFooterViewHolder.getTips().setVisibility(View.VISIBLE);
                // 只有获取数据为空时，hasMore为false，所以当我们拉到底部时基本都会首先显示“正在加载更多...”
                if (hasMore) {
                    // 不隐藏footView提示
                    fadeTips = false;
                    if (this.baseBeanList.size() > 0) {
                        baseRecycleFooterViewHolder.getProgressBar().setVisibility(View.VISIBLE);
                        // 如果查询数据发现增加之后，就显示正在加载更多
                        baseRecycleFooterViewHolder.getTips().setText("正在加载更多");
                    }
                } else {
                    if (this.baseBeanList.size() > 0) {
                        baseRecycleFooterViewHolder.getProgressBar().setVisibility(View.GONE);
                        // 如果查询数据发现并没有增加时，就显示没有更多数据了
                        baseRecycleFooterViewHolder.getTips().setText("没有更多数据了");

                        // 然后通过延时加载模拟网络请求的时间，在500ms后执行
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 隐藏提示条
                                baseRecycleFooterViewHolder.getProgressBar().setVisibility(View.GONE);
                                // 将fadeTips设置true
                                fadeTips = true;
                                // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                                hasMore = false;
                            }
                        }, 500);
                    } else {
                        baseRecycleFooterViewHolder.getTips().setText("没有数据");
                        baseRecycleFooterViewHolder.getProgressBar().setVisibility(View.GONE);
                        // 将fadeTips设置true
                        fadeTips = false;
                        // hasMore设为true是为了让再次拉到底时，会先显示正在加载更多
                        hasMore = false;
                    }
                }
            }
        } else if (getItemViewType(position) == BaseRecycleViewAdapter.HEADER_TYPE) {
            bindHeaderViewHolder(holder, position);
        } else {
            bindNormalViewHolder(holder, position - (isShowHeader ? 1 : 0));
        }
    }

    @Override
    protected int getRecycleItemCount() {
        return this.baseBeanList.size() + (isShowFooter() ? 1 : 0) + (isShowHeader() ? 1 : 0);
    }

    @Override
    public int getItemViewType(int position) {
        int type = getItemViewTypeByChild(position);
        if (type > -1) {
            return type;
        }
        if (isShowHeader && position == 0)  // 如果显示头部，那第一位则是头部
            return BaseRecycleViewAdapter.HEADER_TYPE;
        if (isShowFooter && position == this.baseBeanList.size() + (isShowFooter() ? 1 : 0) + (isShowHeader() ? 1 : 0) - 1)  //  如果显示尾部，那最后一位则是尾部
            return BaseRecycleViewAdapter.FOOTER_TYPE;
        return BaseRecycleViewAdapter.NORMAL_TYPE;
    }

    /**
     * 绑定普通viewHolder
     *
     * @param holder   holder 对象
     * @param position 下标
     */
    protected abstract void bindNormalViewHolder(RecyclerView.ViewHolder holder, int position);

    /**
     * 创建普通viewholder
     *
     * @param layoutInflater 布局加载器
     * @param parent         父类
     * @param viewType       view 类型
     * @return 创建viewHolder之后的对象
     */
    protected abstract BaseRecycleViewHolder onCreateNormalViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType);

    /**
     * 创建头部viewholder
     *
     * @param layoutInflater 布局加载器
     * @param parent         父类
     * @param viewType       view 类型
     * @return 创建viewHolder之后的对象
     */
    protected abstract BaseRecycleViewHolder onCreateHeaderViewHolder(LayoutInflater layoutInflater, ViewGroup parent, int viewType);

    /**
     * 绑定头部viewHolder
     *
     * @param holder   holder 对象
     * @param position 下标
     */
    protected abstract void bindHeaderViewHolder(RecyclerView.ViewHolder holder, int position);


    protected abstract int getItemViewTypeByChild(int position);

}
