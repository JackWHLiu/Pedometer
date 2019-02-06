package com.lwh.pedometer.base;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import com.igexin.sdk.PushManager;
import com.lwh.pedometer.IConstant;
import com.lwh.pedometer.ITimeMillis;
import com.lwh.pedometer.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.media.MediaPlayer;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobConfig;
import cn.sharesdk.framework.ShareSDK;

public class BaseApplication extends Application implements IConstant,ITimeMillis{

	private volatile static BaseApplication mApp;//整个应用的Application
	private Stack<WeakReference<BaseActivity>> mTasks = new Stack<WeakReference<BaseActivity>>();

	public synchronized static BaseApplication getInstance() {
		return mApp;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		MobclickAgent.setSessionContinueMillis(10*1000);
		mApp = this;
		init();
	}

	private void init() {
		initSDK();
		initService();
		mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
	}

	private void initSDK() {
		initBmobSDK();
		initGetuiSDK();
		initShareSDK();
	}

	private void initGetuiSDK() {
		PushManager.getInstance().initialize(mApp);
	}

	private void initShareSDK() {
		ShareSDK.initSDK(this);
	}

	private void initBmobSDK() {
		BmobConfig.Builder builder = new BmobConfig.Builder(mApp);
		builder.setApplicationId(BMOB_APP_ID);// Bmob的应用id
		builder.setConnectTimeout(3 * SECOND);// 3秒连接超时
		BmobConfig conf = builder.build();
		Bmob.initialize(conf);
	}

	private void initService() {
//		mApp.startService(new Intent(mApp,PedometerService.class));
//		mApp.startService(new Intent(mApp,RemoteService.class));
	}
	NotificationManager mNotificationManager;

	public NotificationManager getNotificationManager() {
		if (mNotificationManager == null)
			mNotificationManager = (NotificationManager) getSystemService(android.content.Context.NOTIFICATION_SERVICE);
		return mNotificationManager;
	}

	MediaPlayer mMediaPlayer;

	public void pushTask(WeakReference<BaseActivity> task) {
		mTasks.push(task);
	}

	public void removeTask(WeakReference<BaseActivity> task) {
		mTasks.remove(task);
	}

	public void removeTask(int taskIndex) {
		if (mTasks.size() > taskIndex)
			mTasks.remove(taskIndex);
	}

	public void removeToTop() {
		int end = mTasks.size();
		int start = 1;
		for (int i = end - 1; i >= start; i--) {
			if (!mTasks.get(i).get().isFinishing()) {
				mTasks.get(i).get().finish();
			}
		}
	}

	public void removeAll() {
		for (WeakReference<BaseActivity> task : mTasks) {
			if (!task.get().isFinishing()) {
				task.get().finish();
			}
		}
	}
}
