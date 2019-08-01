package com.framelibrary.widget;
import android.annotation.SuppressLint;
import android.content.Context;

import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	private boolean mDisableSroll = true;

	public MyViewPager(Context context) {
		super(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setDisableScroll(boolean bDisable) {
		mDisableSroll = bDisable;
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		if (mDisableSroll) {
			return false;
		}
		return super.onInterceptTouchEvent(ev);
	}

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mDisableSroll) {
			return false;
		}
		return super.onTouchEvent(ev);
	}

}
