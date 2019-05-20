package com.framelibrary.widget.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.framelibrary.AppManager;
import com.framelibrary.BaseLibActivity;
import com.framelibrary.MusicLockScreenActivity;
import com.framelibrary.R;

/**
 * 音频锁屏监听广播
 *
 * @author wangwx
 */
public class AudioLockScreenReceiver extends BroadcastReceiver {

    public AudioLockScreenReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        /*if (action.equals(Intent.ACTION_SCREEN_OFF)) {
            Intent lockscreen = new Intent(context, MusicLockScreenActivity.class);
            lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(lockscreen);
        }*/
    }
}
