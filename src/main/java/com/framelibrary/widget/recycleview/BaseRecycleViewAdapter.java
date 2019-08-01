package com.framelibrary.widget.recycleview;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import com.framelibrary.util.bean.BaseBean;

import java.util.List;

/**
 * RecycleView基础adapter
 */

public abstract class BaseRecycleViewAdapter extends RecyclerView.Adapter<BaseRecycleViewHolder> implements View.OnClickListener, View.OnLongClickListener {

    protected static final int HEADER_TYPE = 0;
    protected static final int NORMAL_TYPE = 1;
    protected static final int FOOTER_TYPE = 2;
    protected static final int EMPTY_TYPE = 99; //空数据类型
    protected RecyclerItemClickListener recyclerItemClickListener;
    protected List<BaseBean> baseBeanList;

    @NonNull
    @Override
    public BaseRecycleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return onCreateRecycleViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull BaseRecycleViewHolder holder, int position) {
        onBindRecycleViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return getRecycleItemCount();
    }

    protected abstract int getRecycleItemCount();

    protected abstract BaseRecycleViewHolder onCreateRecycleViewHolder(@NonNull ViewGroup parent, int viewType);

    protected void onBindRecycleViewHolder(@NonNull BaseRecycleViewHolder holder, int position) {
        if (getItemViewType(position) == NORMAL_TYPE) {
            holder.itemView.setTag(position);
            holder.itemView.setOnClickListener(this);
            holder.itemView.setOnLongClickListener(this);
        }
    }

    public void setRecyclerItemClickListener(RecyclerItemClickListener recyclerItemClickListener) {
        this.recyclerItemClickListener = recyclerItemClickListener;
    }

    @Override
    public void onClick(View v) {
        onClickResponse(v, true);
    }

    @Override
    public boolean onLongClick(View v) {
        onClickResponse(v, false);
        return false;
    }

    private void onClickResponse(View v, boolean isClick) {
        if (recyclerItemClickListener != null && this.baseBeanList != null && !this.baseBeanList.isEmpty()) {
            Object tag = v.getTag();
            if (tag != null && tag instanceof Integer) {
                int position = (int) tag;
                if (this.baseBeanList.size() > position && position >= 0) {
                    BaseBean baseBean = this.baseBeanList.get(position);
                    if (isClick)
                        recyclerItemClickListener.onItemClick(v, position, baseBean);
                    else
                        recyclerItemClickListener.onItemLongClick(v, position, baseBean);
                }
            }
        }
    }

    protected BaseBean getItemBean(int position) {
        if (this.baseBeanList == null || this.baseBeanList.isEmpty() || position < 0 || position >= this.baseBeanList.size()) {
            return null;
        }
        return this.baseBeanList.get(position);
    }


}
