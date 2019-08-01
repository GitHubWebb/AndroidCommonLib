package com.framelibrary.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.os.SystemClock;

public class Cache<T> {
	
	private static final int MAX_EFFECTIVE_TIME_MILLIS = 1000 * 60;
	private long dateChangeTimeMillis;
	private final List<T> datas = new ArrayList<T>();

	public void addAll(List<T> datas){
		this.datas.addAll(datas);
		onDataChange();
	}

	private void onDataChange() {
		dateChangeTimeMillis = now();
	}

	private long now() {
		return SystemClock.elapsedRealtime();
	}
	
	public void setData(List<T> datas){
		this.datas.clear();
		addAll(datas);
	}
	
	public Iterator<T> getData(){
		if(now() - dateChangeTimeMillis > MAX_EFFECTIVE_TIME_MILLIS){
			return null;
		}
		return this.datas.iterator();
	}

	public void clearData(){
		this.datas.clear();
		onDataChange();
	}
	
	public void add(T data){
		this.datas.add(data);
		onDataChange();
	}
	
	public boolean remove(T data){
		onDataChange();
		return this.datas.remove(data);
	}
}
