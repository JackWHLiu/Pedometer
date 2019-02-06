package com.lwh.pedometer.service;

import java.util.Calendar;

import com.lwh.pedometer.IConstant;
import com.lwh.pedometer.ITimeMillis;
import com.lwh.pedometer.R;
import com.lwh.pedometer.pojo.Step;
import com.lwh.pedometer.sensor.StepDetector;
import com.lwh.pedometer.ui.activity.MainActivity;
import com.lwh.pedometer.util.DateConverter;
import com.lwh.pedometer.util.DbUtils;
import com.lwh.pedometer.view.CircleProgressBar;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.os.RemoteException;

public class StepService extends BaseService implements IConstant, ITimeMillis, SensorEventListener {
	private final String TAG = this.getClass().getSimpleName();
	// 默认为30秒进行一次存储
	private static int sDuration = 30 * SECOND;
	/**
	 * 当前的日期
	 */
	private static String CURRENT_DATE = "";
	private SensorManager mSensorManager;
	private StepDetector mStepDetector;
	private NotificationManager mNotiManager;
	private Notification.Builder mBuilder;
	private Messenger mMessager = new Messenger(new MessagerHandler());
	private BroadcastReceiver mBatInfoReceiver;
	private WakeLock mWakeLock;
	private TimeCount mTime;
	static int sCount = 0;
	private String DB_NAME = "pedometer";

	private static class MessagerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_FROM_CLIENT:
				try {
					Messenger messenger = msg.replyTo;
					Message replyMsg = Message.obtain(null, MSG_FROM_SERVER);
					Bundle bundle = new Bundle();
					bundle.putInt("step", StepDetector.CURRENT_SETP);
					replyMsg.setData(bundle);
					messenger.send(replyMsg);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				break;
			default:
				super.handleMessage(msg);
			}
		}
	}

	@Override
	public void onCreate() {
		super.onCreate();
		initBroadcastReceiver();
		new Thread(new Runnable() {
			public void run() {
				startStepDetector();
			}
		}).start();
		startTimeCount();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		initTodayData();
		updateNotification("今日步数：" + StepDetector.CURRENT_SETP + " 步");
		return START_STICKY;
	}

	/**
	 * 得到今天的日期
	 */
	private String getTodayDate() {
		return DateConverter.long2str(System.currentTimeMillis(), DateConverter.FORMAT_YEAR_MONTH_DAY);
	}

	/**
	 * 初始化今天的数据
	 */
	private void initTodayData() {
		CURRENT_DATE = getTodayDate();
		DbUtils.createDb(this,DB_NAME);
		// 获取当天的数据，用于展示
		Step stepData = DbUtils.queryByWhereOnly(Step.class,Step.KEY_DATE, new String[] { CURRENT_DATE });
		if (stepData==null) {
			StepDetector.CURRENT_SETP = 0;
		}else{
			StepDetector.CURRENT_SETP = Integer.parseInt(stepData.getStep());
		}
	}

	private void initBroadcastReceiver() {
		final IntentFilter filter = new IntentFilter();
		// 屏幕灭屏广播
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		// 日期修改广播
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		// 关机广播
		filter.addAction(Intent.ACTION_SHUTDOWN);
		// 屏幕亮屏广播
		filter.addAction(Intent.ACTION_SCREEN_ON);
		// 屏幕解锁广播
		filter.addAction(Intent.ACTION_USER_PRESENT);
		// 当长按电源键弹出“关机”对话或者锁屏时系统会发出这个广播
		// example：有时候会用到系统对话框，权限可能很高，会覆盖在锁屏界面或者“关机”对话框之上，
		// 所以监听这个广播，当收到时就隐藏自己的对话，如点击pad右下角部分弹出的对话框
		filter.addAction(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

		mBatInfoReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(final Context context, final Intent intent) {
				String action = intent.getAction();
				if (Intent.ACTION_SCREEN_ON.equals(action)) {
					verbose(TAG, "screen on");
				} else if (Intent.ACTION_SCREEN_OFF.equals(action)) {
					verbose(TAG, "screen off");
					sDuration = MINUTE;
				} else if (Intent.ACTION_USER_PRESENT.equals(action)) {
					verbose(TAG, "screen unlock");
					save();
					sDuration = 3 * SECOND;
				} else if (Intent.ACTION_CLOSE_SYSTEM_DIALOGS.equals(intent.getAction())) {
					verbose(TAG, "receive Intent.ACTION_CLOSE_SYSTEM_DIALOGS");
					// 保存一次数据
					save();
				} else if (Intent.ACTION_SHUTDOWN.equals(intent.getAction())) {
					verbose(TAG, "receive ACTION_SHUTDOWN");
					save();
				} else if (Intent.ACTION_TIME_CHANGED.equals(intent.getAction())) {
					verbose(TAG, "receive ACTION_TIME_CHANGED");
					initTodayData();
					clearStepData();
				}
			}
		};
		registerReceiver(mBatInfoReceiver,filter);
	}

	private void clearStepData() {
		sCount = 0;
		StepService.CURRENT_DATE = "0";
	}

	private void startTimeCount() {
		mTime = new TimeCount(sDuration, 1000);
		mTime.start();
	}

	/**
	 * 更新通知
	 */
	@SuppressLint("NewApi")
	private void updateNotification(String content) {
		mBuilder = new Notification.Builder(this);
		PendingIntent contentIntent = PendingIntent.getActivity(this,0, new Intent(this, MainActivity.class), 0);
		mBuilder.setContentIntent(contentIntent);
		mBuilder.setSmallIcon(R.drawable.logo);
		mBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo));
		mBuilder.setTicker("蛙趣计步");
		mBuilder.setContentTitle("蛙趣计步");
		// 设置不可清除
		mBuilder.setOngoing(true);
		mBuilder.setContentText(content);
		Notification notification = mBuilder.getNotification();
		startForeground(0, notification);
		mNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		mNotiManager.notify(R.string.app_name, notification);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mMessager.getBinder();
	}

	private void startStepDetector() {
		if (mSensorManager != null && mStepDetector != null) {
			mSensorManager.unregisterListener(mStepDetector);
			mSensorManager = null;
			mStepDetector = null;
		}
		mSensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		getLock(this);
		// android4.4以后可以使用计步传感器
		// int VERSION_CODES = android.os.Build.VERSION.SDK_INT;
		// if (VERSION_CODES >= 19) {
		// addCountStepListener();
		// } else {
		// addBasePedoListener();
		// }

		addBasePedometerListener();
		addCountStepListener();
	}

	private void addCountStepListener() {
		Sensor detectorSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		Sensor countSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
		if (detectorSensor != null) {
			mSensorManager.registerListener(StepService.this, detectorSensor, SensorManager.SENSOR_DELAY_UI);
		} else if (countSensor != null) {
			mSensorManager.registerListener(StepService.this, countSensor, SensorManager.SENSOR_DELAY_UI);
			// addBasePedoListener();
		} else {
			verbose(TAG, "Count sensor not available!");
		}
	}

	private void addBasePedometerListener() {
		mStepDetector = new StepDetector(this);
		// 获得传感器的类型，这里获得的类型是加速度传感器
		// 此方法用来注册，只有注册过才会生效，参数：SensorEventListener的实例，Sensor的实例，更新速率
		Sensor sensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		// sensorManager.unregisterListener(stepDetector);
		mSensorManager.registerListener(mStepDetector, sensor, SensorManager.SENSOR_DELAY_UI);
		mStepDetector.setOnSensorChangeListener(new StepDetector.OnSensorChangeListener() {

			public void onChange() {
				updateNotification("今日步数：" + StepDetector.CURRENT_SETP + " 步");
			}
		});
	}

	public void onSensorChanged(SensorEvent event) {
		sCount++;
		updateNotification("今日步数：" + StepDetector.CURRENT_SETP + " 步");
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	class TimeCount extends CountDownTimer {
		public TimeCount(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			// 如果计时器正常结束，则开始计步
			mTime.cancel();
			save();
			startTimeCount();
		}

		@Override
		public void onTick(long millisUntilFinished) {
		}
	}

	private void save() {
		int tempStep = StepDetector.CURRENT_SETP;
		Step stepData = DbUtils.queryByWhereOnly(Step.class,Step.KEY_DATE, new String[] { CURRENT_DATE });
		if (stepData==null) {
			stepData = new Step();
			stepData.setDate(CURRENT_DATE);
			stepData.setStep(tempStep + "");
			DbUtils.insert(stepData);
		}else{
			stepData.setStep(tempStep + "");
			DbUtils.update(stepData);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 取消前台进程
		stopForeground(true);
		DbUtils.closeDb();
		unregisterReceiver(mBatInfoReceiver);
		Intent intent = new Intent(this, StepService.class);
		startService(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	// private void unlock(){
	// setLockPatternEnabled(android.provider.Settings.Secure.LOCK_PATTERN_ENABLED,false);
	// }
	//
	// private void setLockPatternEnabled(String systemSettingKey, boolean
	// enabled) {
	// //推荐使用
	// android.provider.Settings.Secure.putInt(getContentResolver(),
	// systemSettingKey,enabled ? 1 : 0);
	// }

	synchronized private PowerManager.WakeLock getLock(Context context) {
		if (mWakeLock != null) {
			if (mWakeLock.isHeld())
				mWakeLock.release();
			mWakeLock = null;
		}
		if (mWakeLock == null) {
			PowerManager mgr = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			mWakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, StepService.class.getName());
			mWakeLock.setReferenceCounted(true);
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(System.currentTimeMillis());
			int hour = c.get(Calendar.HOUR_OF_DAY);
			if (hour >= 23 || hour <= 6) {
				mWakeLock.acquire(5 * SECOND);
			} else {
				mWakeLock.acquire(30 * SECOND);
			}
		}
		return (mWakeLock);
	}
}
