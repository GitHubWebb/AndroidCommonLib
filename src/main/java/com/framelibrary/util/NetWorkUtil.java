package com.framelibrary.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * @author:wangwx
 * @Date:2018/4/26 20:36
 * @Description 网络状态判断
 */

public class NetWorkUtil {
    public static boolean isOpenNoNetWork = false;  // 是否打开无网络加载页面

    private Context context = null;
    public NetWorkUtil(Context context) {
        this.context = context;
    }
    /**
     * 判断当前网络是否可用
     *
     * @return
     */
    public  boolean isNetWorkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null) {
            NetworkInfo info = manager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }
}
