package com.lwh.pedometer.service;

import com.lwh.pedometer.ILog;
import com.lwh.pedometer.IToast;
import com.lwh.pedometer.LogCat;
import com.lwh.pedometer.Toast;

import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;

public class BaseService extends Service implements ILog,IToast{

	private LogCat mLogCat;
	private Toast mToast;
	protected final String TAG = this.getClass().getSimpleName(); 
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		mLogCat = new LogCat();
		mToast = new Toast(this);
		info(TAG,"onCreate() is called.");
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		info(TAG, "onStartCommand() is called."); 
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		info(TAG, "onDestroy() is called."); 
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		info(TAG, "onUnbind() is called."); 
		return super.onUnbind(intent);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		info(TAG, "onConfigurationChanged() is called");
	}
	
	@Override
	public void onRebind(Intent intent) {
		super.onRebind(intent);
		info(TAG, "onRebind() is called");
	}
	
	@Override
	public void toast(String text) {
		mToast.toast(text);
	}

	@Override
	public void toast(String text, int duration) {
		mToast.toast(text, duration);
	}

	@Override
	public void info(String tag, String msg) {
		mLogCat.info(tag, msg);
	}

	@Override
	public void error(String tag, String msg) {
		mLogCat.error(tag, msg);
	}

	@Override
	public void debug(String tag, String msg) {
		mLogCat.debug(tag, msg);
	}

	@Override
	public void warn(String tag, String msg) {
		mLogCat.warn(tag, msg);
	}

	@Override
	public void verbose(String tag, String msg) {
		mLogCat.verbose(tag, msg);
	}

}
