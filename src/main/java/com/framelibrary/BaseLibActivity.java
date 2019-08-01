package com.framelibrary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.android.arouter.launcher.ARouter;
import com.framelibrary.config.BaseApplication;
import com.framelibrary.event.InfoChangeEvent;
import com.framelibrary.util.DateUtils;
import com.framelibrary.util.LogUtils;
import com.framelibrary.util.StatusBarUtil;
import com.framelibrary.util.StringUtils;
import com.framelibrary.widget.audio.AudioCustomNotificationUtil;
import com.framelibrary.widget.audio.BaseMusicService;
import com.framelibrary.util.select.selectphoto.FileUtils;
import com.github.anzewei.parallaxbacklayout.ParallaxBack;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.umeng.analytics.MobclickAgent;
import com.framelibrary.util.select.selectdata.SelectDataPopWindow;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 基类
 */

@ParallaxBack(edge = ParallaxBack.Edge.LEFT, layout = ParallaxBack.Layout.COVER)
public abstract class BaseLibActivity extends FragmentActivity {
    static {
//        关于矢量图片资源SVG向后兼容：CompatVectorFromResourcesEnabled标志的使用
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected AppManager appManager;
    public String TAG = "BaseLibActivity";
    public TextView tvTitle;
    public static boolean isActive; //全局变量
    protected BaseLibActivity libActivity;
    public ImageView ivLeft;
    public JSONObject shareJO = new JSONObject();
    public Vibrator vibrator;
    protected int page = 1;  //分页 开始页码为1
    protected View decorView;
    public static BaseMusicService mBindService;
    public BaseMusicService.LocalBinder mBinder;
    public AudioCustomNotificationUtil notificationUtil;
    public static Notification notification;
    public static NotificationManager notificationManager;

    //城市配置初始化
    public CityPickerView mCityPickerView;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        appManager = BaseApplication.appManager;
        EventBus.getDefault().register(this);
        Activity currentActivity = appManager.getActivity(this.getClass());
        if (currentActivity != null) {
            appManager.finishActivity(currentActivity);
        } else {
            TAG = getClass().getSimpleName();
            appManager.addActivity(this);
        }

        libActivity = this;
        //自定义颜色,第二个参数直接写颜色就可以
//        Eyes.setStatusBarLightMode(this, Color.WHITE);
//        Eyes.setStatusBarLightMode(this, Color.TRANSPARENT);
        //获取顶层视图
        decorView = getWindow().getDecorView();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);

        //当FitsSystemWindows设置 true 时，会在屏幕最上方预留出状态栏高度的 padding
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);
        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, 0x55000000);
        }

        ButterKnife.bind(this);
        ARouter.getInstance().inject(this);
    }

    //获取出生年
    public ArrayList<String> getBirthDayYearList() {
        ArrayList<String> arrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int startYear = mYear - 15;
        for (int i = startYear; i > startYear - 40; i--) {
            arrayList.add(i + "年");
        }
        return arrayList;
    }

    //显示数据下拉框
    public void showSelectData(List<String> arrayListOne, List<String> arrayListTwo, TextView textView) {
        showSelectData(arrayListOne, arrayListTwo, textView, -1);
    }

    /**
     * 判断是否存在虚拟按键
     *
     * @return
     */
    public boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        Resources rs = getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {

        }
        return hasNavigationBar;
    }

    /**
     * 显示选择数据的PopWindow
     *
     * @param arrayListOne 数据源1
     * @param arrayListTwo 数据源2
     * @param textView     需要显示的控件
     */
    protected void showSelectData(List<String> arrayListOne, List<String> arrayListTwo, final TextView textView, final int requestCode) {
        if (arrayListOne == null || arrayListOne.isEmpty())
            return;
        SelectDataPopWindow selectDataPopWindow = new SelectDataPopWindow(this, arrayListOne, arrayListTwo);
        selectDataPopWindow.showAtLocation(textView, Gravity.BOTTOM, 0, 0);
        selectDataPopWindow.setOnClickButtonListener(new SelectDataPopWindow.OnClickButtonListener() {
            @Override
            public void onClick(String selectDataOne, String selectDataTwo) {
                String result = selectDataOne;

                LogUtils.D(TAG, "showSelectData(),result=" + result);
                if (!StringUtils.isBlank(selectDataTwo)) {
                    result += selectDataTwo;
                }
                textView.setText(result);

                onSelectData(result, requestCode);
            }
        }, textView);
    }

    protected void onSelectData(String result, int requestCode) {
    }


    /**
     * 选择城市
     *
     * @param tv 需要显示的控件
     */
    public void chooseArea(TextView tv) {
        //判断输入法的隐藏状态
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(tv.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
        selectAddress(tv);//调用CityPicker选取区域
    }

    private void selectAddress(final TextView tv) {
        //添加默认的配置，不需要自己定义，当然也可以自定义相关熟悉，详细属性请看demo
        CityConfig cityConfig = new CityConfig.Builder()
                .title("地址选择")
//                .titleBackgroundColor("#F0f0f0")
                .confirTextColor("#F29300")
                .cancelTextColor("#696969")
                .province("北京市")
                .city("北京市")
                .district("朝阳区")
//                .textColor(Color.parseColor("#000000"))
                .visibleItemsCount(3) //滚轮显示的item个数
                .provinceCyclic(false) //省滚轮是否循环滚动
                .cityCyclic(false)   //市滚轮是否循环滚动
                .districtCyclic(false) //区滚轮是否循环滚动
                /*显示省市区三级联动的显示状态
                 * PRO:只显示省份的一级选择器
                 * PRO_CITY:显示省份和城市二级联动的选择器
                 * PRO_CITY_DIS:显示省份和城市和县区三级联动的选择器
                 */
                .setCityWheelType(CityConfig.WheelType.PRO_CITY_DIS)
                .setShowGAT(true)  //是否显示港澳台
                .build();
        if (mCityPickerView == null) {
            mCityPickerView = new CityPickerView();
            /**
             * 预先加载仿iOS滚轮实现的全部数据
             */
            mCityPickerView.init(libActivity);
        }
        mCityPickerView.setConfig(cityConfig);
        //监听方法，获取选择结果
        mCityPickerView.setOnCityItemClickListener(new OnCityItemClickListener() {

            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                StringBuilder sb = new StringBuilder();
                sb.append("选择的结果：\n");
                if (province != null) {
                    sb.append(province.getName() + " " + province.getId() + "\n");
                }

                if (city != null) {
                    sb.append(city.getName() + " " + city.getId() + ("\n"));
                }

                if (district != null) {
                    sb.append(district.getName() + " " + district.getId() + ("\n"));
                }

//                mResultTv.setText("" + sb.toString());
                //为TextView赋值
                tv.setText(province.getName() + "-" + city.getName() + "-" + district.getName());

            }

            @Override
            public void onCancel() {
                ToastUtils.showLongToast(libActivity, "已取消");
            }
        });
        mCityPickerView.showCityPicker();
    }

    protected void initView() {
    }

    protected void initData() {
    }

    /*
    * 想设置震动大小可以通过改变pattern来设定，如果开启时间太短，震动效果可能感觉不到
   * */
    protected void openVibrator() {
        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 300};   // 停止 开启 停止 开启
        vibrator.vibrate(pattern, -1);           //重复两次上面的pattern 如果只想震动一次，index设为-1
    }


    //移动光标到尾部
    protected void moveCursorEnd(EditText editText) {
        editText.setSelection(editText.getText().length());
    }

    //移动光标到尾部
    protected void moveCursorEnd(EditText... editTextArray) {
        for (int i = 0; i < editTextArray.length; i++) {
            editTextArray[i].setSelection(editTextArray[i].getText().length());
        }
    }


    @NonNull
    protected String getStringText(TextView et) {
        if (et != null)
            return et.getText().toString().trim();
        return "";
    }

    // 压缩图片
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public File compressFileInThread(final File tempPic) {
        final String pic_path = tempPic.getPath();
        LogUtils.I(TAG, "compressFileInThread ,pic_path=" + pic_path);
//        String outPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getPath() + File.separator + "compress/";
        String outPath = getExternalCacheDirs()[0] + File.separator + "compress/";

        File folder = new File(outPath);

        tempPic.setWritable(true);
        tempPic.setReadable(true);
        if (!folder.exists()) {
            boolean isMkdirs = folder.mkdirs();

            try {
                tempPic.getParentFile().createNewFile();
                tempPic.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            LogUtils.D(TAG, "compressFileInThread isMkdirs=" + isMkdirs);
            LogUtils.D(TAG, "compressFileInThread tempPic=" + tempPic);
        }

        LogUtils.D(TAG, "compressFileInThread() , tempPic.getName()" + (tempPic.getName()).trim());

        String temPicPrefix = tempPic.getName().substring(tempPic.getName().lastIndexOf("."), tempPic.getName().length());
        LogUtils.D(TAG, "compressFileInThread() , temPicPrefix=" + temPicPrefix);

        final String compressImage = FileUtils.compressImage(pic_path, outPath + File.separator + StringUtils.getUUID32() + temPicPrefix, 30);
        File file = new File(compressImage);
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            int fileSize = fileInputStream.available();
            LogUtils.D(TAG, "compressFileInThread fileSize=" + fileSize);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return file;
    }

    /**
     * APP是否处于前台唤醒状态
     *
     * @return
     */
    public boolean isAppOnForeground() {
        ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                .getRunningAppProcesses();
        if (appProcesses == null)
            return false;

        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            // The name of the process that this object is associated with.
            if (appProcess.processName.equals(packageName)
                    && appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                return true;
            }
        }

        return false;
    }

    //有按下事件情况拦截
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (DateUtils.isFastDoubleClick()) {
                return true;
            }
            LogUtils.D(TAG, "dispatchTouchEvent,date=" + DateUtils.format(System.currentTimeMillis()));
        }
        return super.dispatchTouchEvent(ev);
    }

    @Subscribe
    public void onEventMainThread(InfoChangeEvent infoChangeEvent) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isActive) {
            //app 从后台唤醒，进入前台
            isActive = true;

            if (libActivity != null)
//                libActivity.addUserInfo();
                Log.i("ACTIVITY", "程序从后台唤醒");
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStop() {
        if (!isAppOnForeground()) {
            //app 进入后台
            isActive = false;//记录当前已经进入后台
            Log.i("ACTIVITY", "程序进入后台");
        }
        super.onStop();
    }

    @Override
    public void finish() {
        super.finish();
        if (appManager != null) {
            appManager.finishActivity(this);
            libActivity = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

}
