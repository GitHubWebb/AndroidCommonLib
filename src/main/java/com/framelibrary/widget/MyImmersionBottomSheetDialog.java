package com.framelibrary.widget;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StyleRes;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

/**
 * 支持沉浸式的BottomSheetDialog
 */

public class MyImmersionBottomSheetDialog extends BottomSheetDialog {

    public MyImmersionBottomSheetDialog(@NonNull Context context) {
        super(context);
    }

    public MyImmersionBottomSheetDialog(@NonNull Context context, @StyleRes int theme) {
        super(context, theme);
    }

    public MyImmersionBottomSheetDialog(@NonNull Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int screenHeight = getScreenHeight(getContext());
        int dialogHeight = screenHeight;
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, dialogHeight == 0 ? ViewGroup.LayoutParams.MATCH_PARENT : dialogHeight);
    }

    public static int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        int height = wm.getDefaultDisplay().getHeight();
        return height;
    }

    @Override
    public void setOnCancelListener(@Nullable OnCancelListener listener) {
        super.setOnCancelListener(listener);
    }

    // 增添屏蔽滚动事件
    public BottomSheetBehavior setSlide(boolean isSlide) {
        View view1 = getDelegate().findViewById(android.support.design.R.id.design_bottom_sheet);
        final BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(view1);
        bottomSheetBehavior.setHideable(isSlide);//fasle此处设置表示禁止BottomSheetBehavior的执行
        return bottomSheetBehavior;
    }


    //设置是否直接进入展开状态
    public void setSTATE_EXPANDED(boolean isSTATE_EXPANDED) {
        if (!isSTATE_EXPANDED)
            return;

        //避免隐藏菜单时崩溃
        BottomSheetBehavior behavior = setSlide(isSTATE_EXPANDED);//fasle此处设置表示禁止BottomSheetBehavior的执行

        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);//展开状态

        //表示在隐藏时，跳过折叠状态，直接进入隐藏状态。
        behavior.setSkipCollapsed(true);

    }
}