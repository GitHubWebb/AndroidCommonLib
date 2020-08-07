package com.framelibrary.widget.wheelview.viewholder;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.framelibrary.util.bean.BaseBean;
import com.framelibrary.widget.recycleview.BaseRecycleViewHolder;
import com.framelibrary.widget.recycleview.RecyclerItemClickListener;

/**
 * 空内容ViewHolder
 */
public class EmptyContentViewHolder extends BaseRecycleViewHolder implements View.OnClickListener {

    public EmptyContentViewHolder(View itemView) {
        super(itemView);
        findViewById(itemView);
    }

    private void findViewById(View itemView) {

    }

    @Override
    public void setUpView(Object model, int position, RecyclerView.Adapter adapter) {

    }

    @Override
    public void updateData(BaseBean baseBean, int position, RecyclerItemClickListener recyclerItemClickListener) {

    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public void updateData(BaseBean baseBean, int position, RecyclerItemClickListener recyclerItemClickListener, int baseBeanSize) {
        if (recyclerItemClickListener != null)
            this.recyclerItemClickListener = recyclerItemClickListener;
        this.viewHolderPosition = position;
        this.baseBean = baseBean;


    }

    private void loadMore(boolean isLoadMore) {
//        ToastUtils.showShortToast();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

}
