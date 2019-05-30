package com.framelibrary.widget.audio;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.framelibrary.AppManager;
import com.framelibrary.BaseLibActivity;
import com.framelibrary.R;
import com.framelibrary.util.LogUtils;
import com.framelibrary.util.StringUtils;
import com.lljjcoder.style.citylist.Toast.ToastUtils;

import java.util.Random;

/**
 * 音频监测焦点
 *
 * @author wangwx
 */
public abstract class BaseMusicService extends IntentService {
    protected String TAG = getClass().getSimpleName();
    private AudioManager mAm;
    private boolean isPlaymusic;
    private String url;
    public MediaPlayer mediaPlayer;
    private AudioCustomNotificationUtil notificationUtil;
    protected BaseLibActivity currentActivity;
    private AudioLockScreenReceiver lockScreenReceiver = new AudioLockScreenReceiver();
    public String audioUrl; // 音频播放地址

    public static boolean isNoPay; // 是否允许播放(是否购买)  0 未购买 1 已购买
    public static boolean isNoTryPay; // 是否允许试看  0 不允许试看 1 允许试看
    public static boolean isShowTryAlert = false; //是否显示过试看结束 标记
    public static int try_num; // 试听时长
    public OnMediaPlayerFinishInterface mediaPlayerFinishInterface;

    public BaseMusicService() {
        super("music");
    }

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public BaseMusicService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mAm = (AudioManager) getSystemService(AUDIO_SERVICE);
        initActivity();
    }

    private String channelID = "10";

    public void initActivity() {
        AppManager appManager = AppManager.getAppManager();
        currentActivity = (BaseLibActivity) appManager.currentActivity();
        // 注册广播
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(AudioCustomNotificationUtil.audioLockScreenReceiverAction);
        iFilter.addAction(Intent.ACTION_SCREEN_OFF);
        iFilter.addAction(Intent.ACTION_SCREEN_ON);
        registerReceiver(lockScreenReceiver, iFilter);
        notificationUtil = notificationUtil.getInstance(currentActivity, channelID);
    }

    /**
     * 服务启动的时候调用
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("=========onStartCommand======");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {

    }

    AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                // Pause playback
                pause();
            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                // Resume playback
                resume();
            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                // mAm.unregisterMediaButtonEventReceiver(RemoteControlReceiver);
//                mAm.abandonAudioFocus(afChangeListener);
                // Stop playback
                pause();
            }
        }
    };

    private boolean requestFocus() {
        // Request audio focus for playback
        int result = mAm.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
    }

    public void resume() {
        if (mediaPlayer != null && requestFocus()) {
            try {
                if (requestFocus()) {
                    mediaPlayer.setOnCompletionListener(completionListener);
                    mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
                    if (!isPlaying()) {
                        mediaPlayer.start();
                        if (currentActivity.notification == null || currentActivity.notificationManager == null) {
                            //创造音频播放Notification
                            notificationUtil.showNotificationAudioCustomView();

                        } else {
                            /*currentActivity.notification.contentView.setProgressBar(R.id.progress_audio,n
                                    mediaPlayer.getDuration(), mediaPlayer.getCurrentPosition(), false);*/
                            currentActivity.notificationUtil.showNotificationAudioCustomView();
                        }
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    public void pause() {
        try {
            if (mediaPlayer != null && isPlaying()) {
                if (!mediaPlayer.isLooping()) {
                    mAm.abandonAudioFocus(afChangeListener);
                }
                mediaPlayer.pause();
//                currentActivity.notification.contentView.setImageViewResource(R.id.widget_play, R.mipmap.audio_widget_pause);
                if (currentActivity.notificationUtil != null)
                    currentActivity.notificationUtil.showNotificationAudioCustomView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    MediaPlayer.OnCompletionListener completionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer player) {
            if (!player.isLooping()) {
                mAm.abandonAudioFocus(afChangeListener);
            }
            if (mediaPlayerFinishInterface != null)
                mediaPlayerFinishInterface.finish(player);
        }
    };

    MediaPlayer.OnErrorListener mediaPlayerOnErrorListener = new MediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(MediaPlayer mp, int what, int extra) {
            Log.d(TAG, "OnError - Error code: " + what + " Extra code: " + extra);
            switch (what) {
                case -1004:
                    Log.d(TAG, "MEDIA_ERROR_IO");
                    break;
                case -1007:
                    Log.d(TAG, "MEDIA_ERROR_MALFORMED");
                    break;
                case 200:
                    Log.d(TAG, "MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK");
                    break;
                case 100:
                    Log.d(TAG, "MEDIA_ERROR_SERVER_DIED");
                    break;
                case -110:
                    Log.d(TAG, "MEDIA_ERROR_TIMED_OUT");
                    break;
                case 1:
                    Log.d(TAG, "MEDIA_ERROR_UNKNOWN");
                    break;
                case -1010:
                    Log.d(TAG, "MEDIA_ERROR_UNSUPPORTED");
                    break;
            }
            switch (extra) {
                case 800:
                    Log.d(TAG, "MEDIA_INFO_BAD_INTERLEAVING");
                    break;
                case 702:
                    Log.d(TAG, "MEDIA_INFO_BUFFERING_END");
                    break;
                case 701:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case 802:
                    Log.d(TAG, "MEDIA_INFO_METADATA_UPDATE");
                    break;
                case 801:
                    Log.d(TAG, "MEDIA_INFO_NOT_SEEKABLE");
                    break;
                case 1:
                    Log.d(TAG, "MEDIA_INFO_UNKNOWN");
                    break;
                case 3:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_RENDERING_START");
                    break;
                case 700:
                    Log.d(TAG, "MEDIA_INFO_VIDEO_TRACK_LAGGING");
                    break;
            }
            if (mediaPlayer != null)
                mediaPlayer.reset();
            return true;
        }
    };

    public void play(Context context, String url, @Nullable OnMediaPlayerFinishInterface mediaPlayerFinishInterface) {
        if (mediaPlayerFinishInterface != null)
            this.mediaPlayerFinishInterface = mediaPlayerFinishInterface;
        if (mediaPlayer != null && isPlaying())
            pause();

        if (context != null && !StringUtils.isBlank(url))
            if (requestFocus()) {
                try {
                    this.audioUrl = url;
                    mediaPlayer = MediaPlayer.create(context, Uri.parse(audioUrl));
                    if (mediaPlayer != null) {
                        mediaPlayer.setOnCompletionListener(completionListener);
                        mediaPlayer.setOnErrorListener(mediaPlayerOnErrorListener);
                        if (!isPlaying()) {
                            mediaPlayer.start();
                        }
                    }
                } catch (Exception e) {
                    LogUtils.E(TAG, "play, e=" + e.getMessage());
                }
            }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null)
            mediaPlayer.release();
        unregisterReceiver(lockScreenReceiver);
        AudioCustomNotificationUtil.release();

    }

    public void stop() {
        if (mediaPlayer != null) {
            if (isPlaying()) {
                mediaPlayer.stop();
            }
            mediaPlayer.reset();

            mediaPlayer = null;
            /*if (BaseLibActivity.notificationManager != null)
                BaseLibActivity.notificationManager.cancelAll();*/
        }
    }

    //上一曲
    public abstract void preMusic();

    //下一曲
    public abstract void nextMusic();

    // Binder given to clients
    public final IBinder mBinder = new LocalBinder();
    // Random number generator
    private final Random mGenerator = new Random();

    //调节播放进度 避免调节播放位置不准确
    @TargetApi(Build.VERSION_CODES.O)
    public void seekTo(long gotoTimer) {
        if (mediaPlayer == null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            mediaPlayer.seekTo(gotoTimer, MediaPlayer.SEEK_CLOSEST);
        else
            mediaPlayer.seekTo((int) gotoTimer);
    }

    //获取当前播放进度
    public long getCurrentTime() {
        if (mediaPlayer == null)
            return 0L;
        return mediaPlayer.getCurrentPosition();
    }

    //获取当前播放总进度
    public long getTotalTime() {
        if (mediaPlayer == null)
            return 0L;
        return mediaPlayer.getDuration();
    }

    //播放状态
    public boolean isPlaying() {
        if (mediaPlayer != null && mediaPlayer.isPlaying())
            return true;

        return false;
    }

    /**
     * 根据音频地址判断当前是否播放&是否播放的是当前地址
     *
     * @param audio_url
     * @return 0 需要调用mediaplayer.play 方法进行播放
     * 1 需要调用resume() 继续播放
     * 2 需要调用pause() 暂停播放
     */
    public int isPlaying(String audio_url) {
        if (mediaPlayer == null || !audio_url.equals(this.audioUrl))
            return 0;

        if (isPlaying())
            return 2;
        else
            return 1;
    }

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public BaseMusicService getService() {
            // Return this instance of LocalService so clients can call public methods
            return BaseMusicService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String type = intent.getStringExtra("action");
        Log.d("intent", type);
        if (type.equals("next")) {
            //nextMusic();
        } else if (type.equals("pre")) {
            //frontMusic();
        } else if (type.equals("pause")) {
            Intent i = new Intent(this, AudioPlayerReceiver.class);
            Log.d("isplaying", isPlaying() + "");
            if (isPlaying())
                i.putExtra("action", "playing");
            else
                i.putExtra("action", "pause");
            sendBroadcast(i);
        }

    }
}
