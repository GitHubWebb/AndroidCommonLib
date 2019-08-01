package com.framelibrary;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.github.anzewei.parallaxbacklayout.ParallaxBack;
import com.github.anzewei.parallaxbacklayout.ParallaxHelper;

/**
 * 音乐锁屏
 *
 * @author wangwx
 */
@ParallaxBack(edge = ParallaxBack.Edge.LEFT, layout = ParallaxBack.Layout.COVER)
public class MusicLockScreenActivity extends BaseLibActivity {
    private static final String TAG = "MusicLockScreenActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED //锁屏状态下显示
//              | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD     //解锁
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON      //保持屏幕长亮
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);    //打开屏幕
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib_main);

        /*this.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);*/
        //关闭滑动返回功能
//        ParallaxHelper.getInstance().disableParallaxBack(this);
    }

    @Override
    public void onBackPressed() {
        // do nothing
    }

}
