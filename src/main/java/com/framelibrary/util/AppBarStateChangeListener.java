package com.framelibrary.util;

import android.support.design.widget.AppBarLayout;

/**
 * CollapsingToolbarLayout的展开与折叠
 *
 * @author wangwx
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE,
        NORMAL
    }

    private State mCurrentState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (i == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED,i);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(i) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED,i);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE,i);
            }else{
                onStateChanged(appBarLayout, State.NORMAL,i);
            }
            mCurrentState = State.IDLE;
        }
    }

    // verticalOffset 偏移量
    public abstract void onStateChanged(AppBarLayout appBarLayout, State state , int verticalOffset);
}