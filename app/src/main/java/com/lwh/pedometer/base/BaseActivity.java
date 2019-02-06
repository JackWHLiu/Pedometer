package com.lwh.pedometer.base;

import java.lang.ref.WeakReference;

import com.alwh.framework.app.Activity;
import com.lwh.pedometer.ILog;
import com.lwh.pedometer.IToast;
import com.lwh.pedometer.LogCat;
import com.lwh.pedometer.Toast;
import com.umeng.analytics.MobclickAgent;

import android.content.Intent;
import android.os.Bundle;

/**
 * Activity基类
 * @author lwh
 */
public abstract class BaseActivity extends Activity implements ILog, IToast {
	private Toast mToast;
	protected static BaseApplication mApp;
	protected WeakReference<BaseActivity> mTask;
	protected final String TAG = this.getClass().getSimpleName();
	private LogCat mLogCat;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLogCat = new LogCat();
		mToast = new Toast(this);
		info(TAG, "onCreate() is called.");
		mApp = BaseApplication.getInstance();
		mTask = new WeakReference<BaseActivity>(this);
		mApp.pushTask(mTask);
		doBusiness();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 业务逻辑
	 */
	protected abstract void doBusiness();

	@Override
	protected void onRestart() {
		super.onRestart();
		info(TAG, "onRestart() is called.");
	}

	@Override
	protected void onStart() {
		super.onStart();
		info(TAG, "onStart() is called.");
	}

	@Override
	protected void onResume() {
		MobclickAgent.onResume(this);
		super.onResume();
		info(TAG, "onResume() is called.");
	}

	@Override
	protected void onPause() {
		MobclickAgent.onPause(this);
		super.onPause();
		info(TAG, "onPause() is called.");
	}

	@Override
	protected void onStop() {
		super.onStop();
		info(TAG, "onStop() is called.");
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		info(TAG, "onDestroy() is called.");
		mApp.removeTask(mTask);
	}

	public void toast(String text) {
		mToast.toast(text);
	}

	public void toast(String text, int duration) {
		mToast.toast(text, duration);
	}

	public void info(String tag, String msg) {
		mLogCat.info(tag, msg);
	}

	public void error(String tag, String msg) {
		mLogCat.error(tag, msg);
	}

	public void debug(String tag, String msg) {
		mLogCat.debug(tag, msg);
	}

	public void warn(String tag, String msg) {
		mLogCat.warn(tag, msg);
	}

	public void verbose(String tag, String msg) {
		mLogCat.verbose(tag, msg);
	}
}