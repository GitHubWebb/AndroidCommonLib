package com.framelibrary.widget.audio;

import com.framelibrary.AppManager;
import com.framelibrary.BaseLibActivity;
import com.framelibrary.MusicLockScreenActivity;
import com.framelibrary.R;
import com.framelibrary.util.StringUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * 音频播放广播
 *
 * @author wangwx
 */
public class AudioPlayerReceiver extends BroadcastReceiver {

    public AudioPlayerReceiver() {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppManager appManager = AppManager.getAppManager();
        BaseLibActivity currentActivity = (BaseLibActivity) appManager.currentActivity();
        String actionExtra = intent.getStringExtra("action");
        if (StringUtils.isBlank(actionExtra))
            return;

        String channelID = intent.getStringExtra("channelID");
        String action = intent.getAction();
        /*if (action.equals(Intent.ACTION_USER_PRESENT)) {
            Intent lockscreen = new Intent(currentActivity, MusicLockScreenActivity.class);
            lockscreen.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            currentActivity.startActivity(lockscreen);
        }*/

//        Log.d("actionExtra", actionExtra);
        if (currentActivity != null &&
                currentActivity.notificationManager != null &&
                currentActivity.mBindService.mediaPlayer != null) {
            if (actionExtra.equals("close")) {
                currentActivity.notificationManager.cancelAll();
                currentActivity.notificationManager = null;
                currentActivity.notification = null;
                currentActivity.mBindService.stop();
                currentActivity.mBindService.stopSelf();
            } else if (actionExtra.equals("pre")) {
                currentActivity.mBindService.preMusic();
            } else if (actionExtra.equals("next")) {
                currentActivity.mBindService.nextMusic();
            } else {
                actionExtra = currentActivity.mBindService.isPlaying() ? "pause" : "playing";
                if (actionExtra.equals("playing")) {
                    currentActivity.mBindService.resume();
                } else if (actionExtra.equals("pause")) {
                    currentActivity.mBindService.pause();
                }
            }
        }
    }
}
