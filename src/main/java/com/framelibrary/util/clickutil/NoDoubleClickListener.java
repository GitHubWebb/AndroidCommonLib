package com.framelibrary.util.clickutil;

import android.view.View;
import android.widget.AdapterView;

import java.util.Calendar;

/**
 * 防止重复点击事件&工具
 *
 * @author wangwx
 */
public abstract class NoDoubleClickListener implements View.OnClickListener, AdapterView.OnItemClickListener {
    public static final int CLICK_DELAY_TIME = 300;  //连击事件间隔
    private long lastClickTime = 0; //记录最后一次时间

    @Override
    public void onClick(View v) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > CLICK_DELAY_TIME) {  //判断时间差
            lastClickTime = currentTime;  //记录最后一次点击时间
            onNoDoubleClick(v);
        }
    }

    //抽象一个无连击事件方法，用于实现内容
    public abstract void onNoDoubleClick(View v);

    //抽象一个无连击事件方法，用于实现内容
    public abstract void onNoDoubleClick(AdapterView<?> parent, View view, int position, long id);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime > CLICK_DELAY_TIME) {  //判断时间差
            lastClickTime = currentTime;  //记录最后一次点击时间
            onNoDoubleClick(parent, view, position, id);
        }
    }

}