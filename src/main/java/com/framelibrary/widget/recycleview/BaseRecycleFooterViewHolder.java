package com.framelibrary.widget.recycleview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.framelibrary.R;
import com.framelibrary.util.bean.BaseBean;


/**
 * 上拉加载RecyclerView 脚部加载viewholder
 */

public class BaseRecycleFooterViewHolder extends BaseRecycleViewHolder {

    private TextView tips;
    private ProgressBar progressBar;

    public BaseRecycleFooterViewHolder(View itemView) {
        super(itemView);
        tips = itemView.findViewById(R.id.tv);
        progressBar = itemView.findViewById(R.id.pb);
    }

    @Override
    public void setUpView(Object model, int position, RecyclerView.Adapter adapter) {

    }

    @Override
    public void updateData(BaseBean baseBean, int position, RecyclerItemClickListener recyclerItemClickListener) {

    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public TextView getTips() {
        return tips;
    }
}
