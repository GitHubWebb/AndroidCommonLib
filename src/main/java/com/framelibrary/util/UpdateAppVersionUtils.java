package com.framelibrary.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.NumberFormat;

/**
 * @author fanming
 * APK下载更新工具类
 */
public class UpdateAppVersionUtils {

	/**
	 * 当前版本名
	 */
	private final String currentVersionName;
	/**
	 * 服务器版本名
	 */
	private final String serverVersionName;

	/**
	 * APP下载地址
	 */
	private final String appDownloadUrl;
	/**
	 * APP文件名
	 */
	private String appFileName = "";
	/**
	 * 默认app下载文件名
	 */
	private final static String DEFAULT_APP_FILE_NAME = "nowDownload.apk";
	/**
	 * APP下载文件名后缀
	 */
	private final static String APP_FILE_SUFFIX = ".apk";
	/**
	 * 是否停止下载
	 */
	private boolean isStop = false;

	private final Activity mActivity;

	public boolean checkVersionUpdate(){
		if(isNeedUpdate()){
			isStop = false;
			downloadApk();
			return true;
		}else {
			isStop = true;
			return false;
		}
	}

	/**
	 * 停止下载
	 */
	public void stopDownload() {
		isStop = true;
		if(downloadApkThread != null){
			downloadApkThread.interrupt();
		}
	}

	/**
	 * @param activity  
	 * @param currentVersionName  当前版本名
	 * @param serverVersionName   服务器版本名
	 */
	public UpdateAppVersionUtils(Activity activity,String currentVersionName,String serverVersionName,String appDownloadUrl,String appFileName) {
		this.mActivity = activity;
		this.currentVersionName = currentVersionName;
		this.serverVersionName = serverVersionName;
		this.appDownloadUrl = appDownloadUrl;
		if(StringUtils.isBlank(this.appFileName)){
			this.appFileName = DEFAULT_APP_FILE_NAME;
		}else {
			this.appFileName = appFileName + APP_FILE_SUFFIX;
		}
		
	}

	/**
	 * @return  是否需要更新
	 */
	private boolean isNeedUpdate(){
		if(StringUtils.isBlank(this.serverVersionName) || StringUtils.isBlank(this.currentVersionName) || StringUtils.isBlank(this.appDownloadUrl)){
			return false;
		}
		return compareAppVersion(this.currentVersionName, this.serverVersionName);
	}

	DownloadApkThread downloadApkThread;
	/**
	 * 下载APK
	 */
	private synchronized void downloadApk() {
		downloadApkThread = new DownloadApkThread();
		downloadApkThread.start();
	}
	private File file = null;
	private class DownloadApkThread extends Thread{
		@Override
		public void run() {
			super.run();
			URL url;
			int lenght = 0;
			FileOutputStream fileOutputStream = null;
			BufferedInputStream bufferedInputStream = null;
			HttpURLConnection httpURLConnection = null;
			InputStream inputStream = null;
			try {
				url = new URL(appDownloadUrl);
				httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setConnectTimeout(5000);
				lenght = httpURLConnection.getContentLength();
				mActivity.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						onDownloaProgressdListener.onDownloadProgress("0%");
					}
				});
				inputStream = httpURLConnection.getInputStream();
				System.out.println("total====" + httpURLConnection.getContentLength());
				Log.d(getClass().getName(), "conn over!");
				if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
					file = new File(Environment.getExternalStorageDirectory(), appFileName);
					if (file.exists()) {
						file.delete();
					}
					System.out.println("file.exists()===="+file.exists());
					fileOutputStream = new FileOutputStream(file,true);
					bufferedInputStream = new BufferedInputStream(inputStream,1024);
					byte[] buffer = new byte[1024];
					int len;
					int total = 0;
					while ((len = bufferedInputStream.read(buffer)) != -1) {
						fileOutputStream.write(buffer, 0, len);
						total += len;
						convertPercent(lenght, total);
						if (isStop) {
							mActivity.runOnUiThread(new Runnable() {

								@Override
								public void run() {
									destoryThread();
									onDownloaProgressdListener.onDownloadReset();
								}
							});
							break;
						}
					}
					if(!isStop && file != null){
						mActivity.runOnUiThread(new Runnable() {

							@Override
							public void run() {
								destoryThread();
								onDownloaProgressdListener.onDownloadFinish();
								installApk();
							}
						});
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				mActivity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						destoryThread();
						onDownloaProgressdListener.onDownloadError();
					}
				});
			}finally {
				System.out.println("finally");
				try {
					fileOutputStream.close();
					bufferedInputStream.close();
					inputStream.close();
					httpURLConnection.disconnect();
				} catch (Exception e) {
					e.printStackTrace();
				}finally {
					fileOutputStream = null;
					bufferedInputStream = null;
					inputStream = null;
					httpURLConnection = null;
				}
			}
		}
	};

	
	/**
	 * 
	 */
	private void destoryThread() {
		downloadApkThread.interrupt();
		downloadApkThread = null;
	}
	
	
	/**
	 * 百分比转换，并且回调下载进度
	 * @param totalLength  总长度
	 * @param length  下载的长度
	 */
	private void convertPercent(int totalLength, int length) {
		double tempTotalLength =  totalLength;
		double tempLength = length;
		double progress = tempLength / tempTotalLength;
		NumberFormat numberFormat = NumberFormat.getPercentInstance();
		final String progressStr = numberFormat.format(progress);
		System.out.println("totalLength===="+progress);
		mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				onDownloaProgressdListener.onDownloadProgress(progressStr);
			}
		});
	}

	/**
	 * APK安装
	 */
	private void installApk() {
		if (file == null) {
			Toast.makeText(mActivity, "更新失败，文件丢失，请联系管理员！", Toast.LENGTH_SHORT).show();
			return;
		}
		Intent intent = new Intent();
		// 执行动作
		intent.setAction(Intent.ACTION_VIEW);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 执行的数据类型 此处Android应为android，否则造成安装不了
		intent.setDataAndType(Uri.parse("file://" + file.toString()), "application/vnd.android.package-archive");
		mActivity.startActivity(intent);
	}

	/**
	 * 比较当前版本号与服务器版本号
	 * 
	 * @param currentVersion
	 *            当前版本号
	 * @param serverVersion
	 *            服务器版本号
	 * @return true 可更新，false 不可更新
	 */
	private boolean compareAppVersion(String currentVersion, String serverVersion) {
		String tempCurrentVersion = currentVersion.replace(".", "");
		String tempServerVersion = serverVersion.replace(".", "");
		int tempCurrent = Integer.parseInt(tempCurrentVersion);
		int tempServer = Integer.parseInt(tempServerVersion);
		if(tempServer > tempCurrent){
			return true;
		}
		return false;
	}


	private OnDownloadProgressListener onDownloaProgressdListener;
	/**
	 * 设置下载进度更新
	 * @param onDownloaProgressdListener
	 */
	public void setOnDownloadProgressListener(OnDownloadProgressListener onDownloaProgressdListener) {
		this.onDownloaProgressdListener = onDownloaProgressdListener;
	}

	/**
	 * 下载apk的进度
	 * @author fanming
	 */
	public interface OnDownloadProgressListener {

		/**
		 * 下载进度
		 * @param progress   下载进度
		 */
		void onDownloadProgress(String progress);
		/**
		 * apk下载完成
		 */
		void onDownloadFinish();
		/**
		 * 下载出错
		 */
		void onDownloadError();
		/**
		 * 停止下载
		 */
		void onDownloadReset();

	}
}
