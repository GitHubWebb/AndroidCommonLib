package com.framelibrary.util;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * @author fanming
 *
 */
public class UploadFileUtils {

	private static final String UPLOADKEY = "upload";
	private static final String UPLOADURL = "/m/mexpress/log/upload?";
	
	/**
	 * 上传文件
	 * @param fileName  文件名(绝对路径+文件名)
	 * @param urlPath   上传文件的服务器url地址
	 * @param uploadFileResultListener  上传文件的结果监听
	 */
	public static void uploadFile(String fileName,String urlPath,String params,final UploadFileResultListener uploadFileResultListener){
		AsyncHttpClient asyncHttpClient = new AsyncHttpClient();
		RequestParams requestParams = new RequestParams();
		try {
			requestParams.put(UPLOADKEY, new File(fileName));
			asyncHttpClient.post(urlPath + UPLOADURL + params, requestParams, new AsyncHttpResponseHandler(){

				@Override
				public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
					uploadFileResultListener.onUploadFailure(error);
				}
				@Override
				public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
					String respone = new String(responseBody);
					uploadFileResultListener.onUploadSuccess(respone);
				}});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			uploadFileResultListener.onUploadFailure(e);
		}
	}
	/**
	 * 上传文件结果监听
	 * @author fanming
	 *
	 */
	public interface UploadFileResultListener{
		/**
		 * 上传文件失败抛的异常
		 * @param error
		 */
		void onUploadFailure(Throwable error);
		/**
		 * 上传文件成功服务器返回的结果
		 * @param result
		 */
		void onUploadSuccess(String result);
		
	}
	
	
}
