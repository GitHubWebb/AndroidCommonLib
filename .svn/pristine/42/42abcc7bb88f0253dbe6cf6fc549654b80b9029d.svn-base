package com.framelibrary.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import com.framelibrary.widget.FloatingView;

public class MyWindowManager {

    private static final String TAG = MyWindowManager.class.getSimpleName();

    /**
     * 小悬浮窗View的实例
     */
    private static FloatingView smallWindow;

    /**
     * 小悬浮窗View的参数
     */
    private static LayoutParams smallWindowParams;

    /**
     * 用于控制在屏幕上添加或移除悬浮窗
     */
    private static WindowManager mWindowManager;

    /**
     * 创建一个小悬浮窗。初始位置为屏幕的右部中间位置。
     *
     * @param context 必须为应用程序的Context.
     */
    @SuppressLint("RtlHardcoded")
    @SuppressWarnings("deprecation")
    public static void createSmallWindow(Context context) {
        LogUtils.D(TAG, "createSmallWindow");
        WindowManager windowManager = getWindowManager(context);
        int screenWidth = windowManager.getDefaultDisplay().getWidth();
        int screenHeight = windowManager.getDefaultDisplay().getHeight();
        if (smallWindow == null) {
            LogUtils.D(TAG, "createSmallWindow start");
            smallWindow = new FloatingView(context);
            if (smallWindowParams == null) {
                smallWindowParams = new LayoutParams();
                //设置type,系统提示型窗口，在应用程序之上
                int sdkInt = Build.VERSION.SDK_INT;
                //Unable to add window android.view.ViewRootImpl$W@5e2d85a -- permission denied for this window type
                if (sdkInt >= 23)
                    smallWindowParams.type = LayoutParams.TYPE_APPLICATION_OVERLAY;
                else
                    smallWindowParams.type = LayoutParams.TYPE_PHONE;


                smallWindowParams.format = PixelFormat.RGBA_8888;
                smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
                        | LayoutParams.FLAG_NOT_FOCUSABLE;
                smallWindowParams.gravity = Gravity.LEFT | Gravity.TOP;
                smallWindowParams.width = FloatingView.viewWidth;
                smallWindowParams.height = FloatingView.viewHeight;
                smallWindowParams.x = screenWidth;
                smallWindowParams.y = screenHeight / 2;
            }
            smallWindow.setParams(smallWindowParams);
            windowManager.addView(smallWindow, smallWindowParams);
        }

    }

    /**
     * 将小悬浮窗从屏幕上移除。
     *
     * @param context 必须为应用程序的Context.
     */
    public static void removeSmallWindow(Context context) {
        LogUtils.D(TAG, "removeSmallWindow");
        if (smallWindow != null) {
            LogUtils.D(TAG, "removeSmallWindow START");
            WindowManager windowManager = getWindowManager(context);
            windowManager.removeView(smallWindow);
            smallWindow = null;
        }
    }


    /**
     * 是否有悬浮窗(包括小悬浮窗和大悬浮窗)显示在屏幕上。
     *
     * @return 有悬浮窗显示在桌面上返回true，没有的话返回false。
     */
    public static boolean isWindowShowing() {
        return smallWindow != null;
    }

    /**
     * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。
     *
     * @param context 必须为应用程序的Context.
     * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。
     */
    private static WindowManager getWindowManager(Context context) {
        if (mWindowManager == null) {
            mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        }
        return mWindowManager;
    }
}
