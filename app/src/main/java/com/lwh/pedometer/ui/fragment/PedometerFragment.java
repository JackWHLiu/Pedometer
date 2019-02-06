package com.lwh.pedometer.ui.fragment;

import java.text.DecimalFormat;

import com.lwh.pedometer.IConstant;
import com.lwh.pedometer.base.BaseFragment;
import com.lwh.pedometer.service.StepService;
import com.lwh.pedometer.util.ACache;
import com.lwh.pedometer.view.CircleProgressBar;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.widget.TextView;

public class PedometerFragment extends BaseFragment implements IConstant,Handler.Callback {

	private CircleProgressBar cpb_main_pedometer;
	String mMaxStepNum;
	int mThemeColor;
	ACache mCache;
	TextView tv_main_pedometer_percent_label;
	TextView tv_main_pedometer_calorie_label;
	TextView tv_main_pedometer_percent;
	TextView tv_main_pedometer_calorie;

	public ACache getCache() {
		if (mCache == null) {
			mCache = ACache.get(getActivity());
		}
		return mCache;
	}

	@Override
	protected void doBusiness() {
		init();
	}

	// 循环取当前时刻的步数中间的间隔时间
	private long TIME_INTERVAL = 500;
	private Messenger messenger;
	private Messenger mGetReplyMessenger = new Messenger(new Handler(this));
	private Handler delayHandler;

	ServiceConnection conn = new ServiceConnection() {
		public void onServiceConnected(ComponentName name, IBinder service) {
			try {
				messenger = new Messenger(service);
				Message msg = Message.obtain(null, MSG_FROM_CLIENT);
				msg.replyTo = mGetReplyMessenger;
				messenger.send(msg);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}

		public void onServiceDisconnected(ComponentName name) {

		}
	};

	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case MSG_FROM_SERVER:
			// 更新界面上的步数
			cpb_main_pedometer.setMaxStepNum(Integer.valueOf(mMaxStepNum));
			int step = msg.getData().getInt("step");
			cpb_main_pedometer.update(step, 500);
			DecimalFormat df = new DecimalFormat("0.0");
			tv_main_pedometer_percent.setText(String.valueOf(df.format(step / Double.valueOf(mMaxStepNum) * 100)) + "%");
			tv_main_pedometer_calorie.setText(String.valueOf(df.format(step * 0.03)) + "大卡");
			delayHandler.sendEmptyMessageDelayed(REQUEST_SERVER, TIME_INTERVAL);
			break;
		case REQUEST_SERVER:
			try {
				Message msg1 = Message.obtain(null, MSG_FROM_CLIENT);
				msg1.replyTo = mGetReplyMessenger;
				messenger.send(msg1);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
			break;
		}
		return false;
	}

	public void init() {
		getCache();
		if (mCache.getAsString("goal_step") != null) {
			mMaxStepNum = mCache.getAsString("goal_step");
		} else {
			mMaxStepNum = String.valueOf(DEFAULT_GOAL_STEP);
		}
		SharedPreferences sp = getActivity().getSharedPreferences("pedometer", Context.MODE_PRIVATE);
		if (sp.getInt("theme_color",0)!=0) {
			int themeColor = sp.getInt("theme_color",0);
			cpb_main_pedometer.setColor(themeColor);
			tv_main_pedometer_percent_label.setTextColor(themeColor);
			tv_main_pedometer_calorie_label.setTextColor(themeColor);
		}
		delayHandler = new Handler(this);
	}

	public void onStart() {
		super.onStart();
		setupService();
	}

	private void setupService() {
		Intent intent = new Intent(getActivity(), StepService.class);
		getActivity().bindService(intent, conn, Context.BIND_AUTO_CREATE);
		getActivity().startService(intent);
	}

	public void onDestroy() {
		getActivity().unbindService(conn);
		super.onDestroy();
	}

}
