package com.framelibrary.exception;

import android.util.Log;

import java.lang.Thread.UncaughtExceptionHandler;

public class ExceptionHanlder implements UncaughtExceptionHandler {

	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		Log.e("ExceptionHanlder", "发生了异常在线程：" + thread.toString()+ " 详细信息: " + ex.toString());
	}

}
