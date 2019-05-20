package com.framelibrary.widget.recycleview;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.framelibrary.event.InfoChangeEvent;
import com.framelibrary.util.bean.BaseBean;
import com.framelibrary.widget.audio.BaseMusicService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.ButterKnife;

/**
 *
 */

public abstract class BaseRecycleViewHolder<T> extends RecyclerView.ViewHolder {
    protected String TAG = getClass().getSimpleName();
    public RecyclerItemClickListener recyclerItemClickListener;
    public int viewHolderPosition;
    public BaseBean baseBean;
    protected Activity context;

    public BaseRecycleViewHolder(View itemView) {
        super(itemView);
        context = (Activity) itemView.getContext();
        EventBus.getDefault().register(this);
        ButterKnife.bind(this, itemView);
    }

    public abstract void setUpView(T model, int position, RecyclerView.Adapter adapter);

    public abstract void updateData(BaseBean baseBean, int position, RecyclerItemClickListener recyclerItemClickListener);

    protected void columnClick(View v) {
        columnClick(v, viewHolderPosition);
    }

    private void columnClick(View v, int position) {
        columnClick(v, position, baseBean);

    }

    protected void columnClick(View v, int position, BaseBean baseBean) {
        if (recyclerItemClickListener != null)
            recyclerItemClickListener.onItemClick(v, position, baseBean);

    }


    @Subscribe
    public void onEventMainThread(InfoChangeEvent infoChangeEvent) {

    }

}
