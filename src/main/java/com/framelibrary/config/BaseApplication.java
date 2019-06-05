package com.framelibrary.config;

import android.content.SharedPreferences;
import android.support.multidex.MultiDexApplication;
import android.util.DisplayMetrics;

import com.alibaba.android.arouter.launcher.ARouter;
import com.bumptech.glide.Priority;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.framelibrary.AppManager;
import com.framelibrary.BuildConfig;
import com.framelibrary.R;
import com.framelibrary.util.Constant;
import com.framelibrary.util.DateUtils;
import com.framelibrary.widget.glide.GlideRoundTransform;
import com.github.anzewei.parallaxbacklayout.ParallaxHelper;
import com.liulishuo.filedownloader.FileDownloader;
import com.simple.spiderman.SpiderMan;

import java.lang.reflect.Field;
import java.lang.reflect.Method;


/**
 * application
 */

public class BaseApplication extends MultiDexApplication {

    protected static BaseApplication instance;
    private DisplayMetrics displayMetrics;
    private SharedPreferences sharedPreferences;
    public static AppManager appManager;
    protected RequestOptions options;
    protected RequestOptions roundTransFromOptions;
    protected RequestOptions userAvatarOptions;


    @Override
    public void onCreate() {
        super.onCreate();

        //是否开启日志
        Constant.LOG_PRINT = BuildConfig.DEBUG;

        //放在其他库初始化前
        if (Constant.LOG_PRINT)
            SpiderMan.init(this);

        instance = this;

        if (Constant.LOG_PRINT) {           // 这两行必须写在init之前，否则这些配置在init过程中将无效
            ARouter.openLog();     // 打印日志
            ARouter.openDebug();   // 开启调试模式(如果在InstantRun模式下运行，必须开启调试模式！线上版本需要关闭,否则有安全风险)
        }
        ARouter.init(instance); // 尽可能早，推荐在Application中初始化

        disableAPIDialog();
        initData();

    }

    /**
     * 反射 禁止弹窗 Detected problems with API 弹窗 屏蔽解决方案
     */
    private void disableAPIDialog() {

        try {
            Class clazz = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = clazz.getDeclaredMethod("currentActivityThread");
            currentActivityThread.setAccessible(true);
            Object activityThread = currentActivityThread.invoke(null);
            Field mHiddenApiWarningShown = clazz.getDeclaredField("mHiddenApiWarningShown");
            mHiddenApiWarningShown.setAccessible(true);
            mHiddenApiWarningShown.setBoolean(activityThread, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 初始化工具类
     */
    private void initData() {

        //初始化ParallaxBackLayout,目的仿苹果右划关闭页面
        registerActivityLifecycleCallbacks(ParallaxHelper.getInstance());

        //不允许多次重复点击
        DateUtils.setClickLimit(true);
        displayMetrics = getResources().getDisplayMetrics();
        //初始化Fresco
        Fresco.initialize(this, ImagePipelineConfigFactory.getImagePipelineConfig(this));

//        Fresco.initialize(this);

        initGlideRequestOptions();
        initRoundTransFromOptions();
        initUserAvatarOptions();

        appManager = AppManager.getAppManager();

        FileDownloader.setup(this);//注意作者已经不建议使用init方法

    }

    public static BaseApplication getInstance() {
        return instance;
    }

    public DisplayMetrics getDisplayMetrics() {
        return displayMetrics;
    }

    public SharedPreferences getSharedPreferences() {
        if (sharedPreferences == null) {
            sharedPreferences = getSharedPreferences(BuildConfig.APPLICATION_ID+"unlock_date", MODE_MULTI_PROCESS);
        }
        return sharedPreferences;
    }


    private void initGlideRequestOptions() {
        options = new RequestOptions().centerCrop().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).priority(Priority.HIGH);
    }

    private void initRoundTransFromOptions() {
        initRoundTransFromOptions(4);
    }

    /**
     * 设置圆角度数
     * @param roundDP 圆角度数  已DP为单位
     */
    public RequestOptions initRoundTransFromOptions(int roundDP) {
        this.roundTransFromOptions = new RequestOptions().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).transform(new GlideRoundTransform(roundDP)).priority(Priority.HIGH);

        return roundTransFromOptions;
    }

    private void initUserAvatarOptions() {
        this.userAvatarOptions = new RequestOptions().placeholder(R.mipmap.ic_launcher).error(R.mipmap.ic_launcher).priority(Priority.HIGH);
    }

    public RequestOptions getOptions() {
        return options;
    }

    public RequestOptions getRoundTransFromOptions() {
        return roundTransFromOptions;
    }

    public RequestOptions getUserAvatarOptions() {
        return userAvatarOptions;
    }


}
