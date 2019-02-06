package com.lwh.pedometer.ui.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.lwh.pedometer.ITimeMillis;
import com.lwh.pedometer.R;
import com.lwh.pedometer.R.anim;
import com.lwh.pedometer.base.BaseActivity;

import android.content.Intent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * 启动页面
 * 
 * @author lwh
 */
public class SplashActivity extends BaseActivity implements ITimeMillis{

	private RelativeLayout splash_root;
	private ImageView iv_splash_logo;

	public static final int DELAY = 1 * SECOND;

	/**
	 * 开启视图动画
	 */
	private void startAnimation() {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
		iv_splash_logo.startAnimation(animation);
		animation.setAnimationListener(new AnimationListener() {

			public void onAnimationStart(Animation animation) {
				Timer timer = new Timer();
				timer.schedule(new TimerTask() {

					@Override
					public void run() {
						startMainActivity();
					}

				}, DELAY);
			}

			public void onAnimationRepeat(Animation animation) {
			}

			public void onAnimationEnd(Animation animation) {
			}
		});
	}

	/**
	 * 启动主界面
	 */
	public void startMainActivity() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void doBusiness() {
		startAnimation();
	}
}
