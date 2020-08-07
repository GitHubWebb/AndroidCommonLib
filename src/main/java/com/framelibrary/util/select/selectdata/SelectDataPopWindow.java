package com.framelibrary.util.select.selectdata;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.framelibrary.R;
import com.framelibrary.widget.wheelview.adapter.wheelview.adapter.AbstractWheelTextAdapterChild;
import com.framelibrary.widget.wheelview.adapter.wheelview.listener.OnWheelChangedListener;
import com.framelibrary.widget.wheelview.adapter.wheelview.listener.OnWheelScrollListener;
import com.framelibrary.widget.wheelview.adapter.wheelview.view.WheelView;
import com.framelibrary.util.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Author:  Chen.yuan
 * Email:   hubeiqiyuan2010@163.com
 * Date:    2016/7/28 17:37
 * Description:日期选择window
 */
public class SelectDataPopWindow extends PopupWindow implements View.OnClickListener {

    private static final String TAG = "SelectDataPopWindow";
    private WheelView wvOne;
    private WheelView wvTwo;
    private CalendarTextAdapter calendarTextAdapterOne;
    private CalendarTextAdapter calendarTextAdapterTwo;

    private final int maxTextSize = 17;
    private final int minTextSize = 13;
    private OnClickButtonListener onClickButtonListener;
    private OnChangedListener onChangedListener;
    private List<String> dataOne;
    private List<String> dataTwo;
    private Context context;
    private TextView textView;

    public SelectDataPopWindow(final Context context, List<String> dataOne, List<String> dataTwo) {
        super(context);
        this.context = context;
        View view = View.inflate(context, R.layout.dialog_select_data, null);
        //设置SelectPicPopupWindow的View
        this.setContentView(view);
        //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
//		this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        view.findViewById(R.id.tv_cancel).setOnClickListener(this);
        view.findViewById(R.id.tv_finish).setOnClickListener(this);

        this.dataOne = dataOne;
        this.dataTwo = dataTwo;


        final Window window = ((Activity) context).getWindow();
        setWindowBackground(true, window);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                setWindowBackground(false, window);
            }
        });

        initWheelView(view, dataTwo != null && !dataTwo.isEmpty());

    }

    private void setWindowBackground(boolean showPop, Window window) {
        WindowManager.LayoutParams params = window.getAttributes();
        params.alpha = showPop ? 0.5f : 1f;
        window.setAttributes(params);
    }


    private void initWheelView(View view, boolean isShowTwo) {
        wvOne = view.findViewById(R.id.wv_one);
        wvTwo = view.findViewById(R.id.wv_two);

        wvTwo.setVisibility(isShowTwo ? View.VISIBLE : View.GONE);

        calendarTextAdapterOne = new CalendarTextAdapter(context, dataOne, 0);
        wvOne.setVisibleItems(5);
        wvOne.setViewAdapter(calendarTextAdapterOne);

        calendarTextAdapterTwo = new CalendarTextAdapter(context, dataTwo, 0);
        wvTwo.setVisibleItems(5);
        wvTwo.setViewAdapter(calendarTextAdapterTwo);

        wvOne.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int currentItem = wheel.getCurrentItem();
                String text = calendarTextAdapterOne.getItemText(currentItem).toString();
                LogUtils.D(TAG, "onChangedOne currentItem=" + currentItem + ",text=" + text);
                if (onChangedListener != null) {
                    onChangedListener.onDataOneChanged(currentItem);
                }
            }
        });
        wvOne.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) calendarTextAdapterOne.getItemText(wheel.getCurrentItem());
                setTextViewSize(currentText, calendarTextAdapterOne);
            }
        });

        wvTwo.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                int currentItem = wheel.getCurrentItem();
                String text = calendarTextAdapterTwo.getItemText(currentItem).toString();
                LogUtils.D(TAG, "onChangedTwo currentItem=" + currentItem + ",text=" + text);
                if (onChangedListener != null)
                    onChangedListener.onDataTwoChanged(currentItem);
            }
        });
        wvTwo.addScrollingListener(new OnWheelScrollListener() {

            @Override
            public void onScrollingStarted(WheelView wheel) {
            }

            @Override
            public void onScrollingFinished(WheelView wheel) {
                String currentText = (String) calendarTextAdapterTwo.getItemText(wheel.getCurrentItem());
                setTextViewSize(currentText, calendarTextAdapterTwo);
            }
        });

    }

    public void initWheelViewTwo(List<String> dataTwo) {
        calendarTextAdapterTwo = new CalendarTextAdapter(context, dataTwo, 0);
        wvTwo.setVisibleItems(5);
        wvTwo.setViewAdapter(calendarTextAdapterTwo);
        wvTwo.setCurrentItem(0);
    }

    /**
     * 设置字体大小
     *
     * @param currentItemText 当前item字
     * @param adapter         适配器
     */
    private void setTextViewSize(String currentItemText, CalendarTextAdapter adapter) {
        ArrayList<View> arrayList = adapter.getTextViews();
        int size = arrayList.size();
        String currentText;
        for (int i = 0; i < size; i++) {
            TextView textView = (TextView) arrayList.get(i);
            currentText = textView.getText().toString();
            if (currentItemText.equals(currentText)) {
                textView.setTextSize(maxTextSize);
            } else {
                textView.setTextSize(minTextSize);
            }
        }
    }

    private class CalendarTextAdapter extends AbstractWheelTextAdapterChild {

        private List<String> list;

        private CalendarTextAdapter(Context context, List<String> list, int currentItem) {
            super(context, R.layout.item_data_select, NO_RESOURCE, currentItem, maxTextSize, minTextSize);
            this.list = list;
            setItemTextResource(R.id.tempValue);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            return super.getItem(index, cachedView, parent);
        }

        @Override
        public int getItemsCount() {
            return list == null ? 0 : list.size();
        }

        @Override
        protected CharSequence getItemText(int index) {
            return list.get(index) + "";
        }
    }

    public void setOnClickButtonListener(OnClickButtonListener onClickButtonListener) {
        this.onClickButtonListener = onClickButtonListener;
    }

    public void setOnClickButtonListener(OnClickButtonListener onClickButtonListener, TextView textView) {
        this.onClickButtonListener = onClickButtonListener;
        this.textView = textView;
    }

    public void setOnChangedListener(OnChangedListener onChangedListener) {
        this.onChangedListener = onChangedListener;
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.tv_cancel) {
            dismiss();

        } else if (i == R.id.tv_finish) {
            int itemOne = wvOne.getCurrentItem();
            int itemTwo = wvTwo.getCurrentItem();
            String textOne = dataOne == null || dataOne.isEmpty() ? "" : dataOne.get(itemOne);
            String textTwo = dataTwo == null || dataTwo.isEmpty() ? "" : dataTwo.get(itemTwo);
            LogUtils.D(TAG, "onClick(),textOne=" + textOne + ",textTwo=" + textTwo);
            if (onClickButtonListener != null) {
                LogUtils.D(TAG, "onClick(), onClickButtonListener != null");
                textView.setText(textOne);
                onClickButtonListener.onClick(textOne, textTwo);
            }

            dismiss();

        }
    }

    public interface OnClickButtonListener {
        void onClick(String selectDataOne, String selectDataTwo);
    }

    public interface OnChangedListener {
        void onDataOneChanged(int currentItem);

        void onDataTwoChanged(int currentItem);
    }

}