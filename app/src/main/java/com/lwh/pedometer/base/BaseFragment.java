package com.lwh.pedometer.base;

import com.alwh.framework.app.Fragment;
import com.lwh.pedometer.ILog;
import com.lwh.pedometer.IToast;
import com.lwh.pedometer.LogCat;
import com.lwh.pedometer.Toast;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Fragment的基类
 */
public abstract class BaseFragment extends Fragment implements ILog,IToast{

	protected final String TAG = this.getClass().getSimpleName();
	private Toast mToast;
	protected static BaseApplication mApp;
	private LogCat mLogCat;
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		mLogCat = new LogCat();
		info(TAG, "onAttach() is called.");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mApp = BaseApplication.getInstance();
		mToast = new Toast(mApp);
		info(TAG, "onCreate() is called.");
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		info(TAG, "onActivityCreated() is called.");
		doBusiness();
	}

	/** 业务逻辑 */
	protected abstract void doBusiness();

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		info(TAG, "onSaveInstanceState() is called.");
	}

	@Override
	public void onStart() {
		info(TAG, "onStart() is called.");
		super.onStart();
	}

	@Override
	public void onResume() {
		info(TAG, "onResume() is called.");
		super.onResume();
	}

	@Override
	public void onPause() {
		info(TAG, "onPause() is called.");
		super.onPause();
	}

	@Override
	public void onStop() {
		info(TAG, "onStop() is called.");
		super.onStop();
	}

	@Override
	public void onDestroy() {
		info(TAG, "onDestroy() is called.");
		super.onDestroy();
	}

	@Override
	public void onDetach() {
		info(TAG, "onDetach() is called.");
		super.onDetach();
	}
	
	public void toast(String text){
		mToast.toast(text);
	}
	
	public void toast(String text,int duration){
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
	public void startPage(Class<? extends BaseActivity> clazz,boolean needResult){
		Intent intent = new Intent();
		intent.setClass(getActivity(), clazz);
		if (needResult) {
			startActivityForResult(intent, 0);
		}else{
			startActivity(intent);
		}
	}
}
