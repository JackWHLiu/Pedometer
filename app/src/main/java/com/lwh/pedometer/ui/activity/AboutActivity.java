package com.lwh.pedometer.ui.activity;

import com.alwh.framework.core.annotation.OnClick;
import com.lwh.pedometer.R;
import com.lwh.pedometer.base.BaseActivity;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 关于界面
 * @author lwh
 */
public class AboutActivity extends BaseActivity {

	TextView tv_about_app_version;
	ImageView iv_about_app_logo;
	TextView tv_about_app_introduce;

	@OnClick(R.id.about_titlebar_back)
	public void back(View v) {
		finish();
	}

	@Override
	protected void doBusiness() {
		tv_about_app_version.setText(getVersion());
		tv_about_app_introduce.setText(
				"        这是一款用于计步的APP软件，可以在锁屏状态下自动记录每天所走的步数。\n        现在这个智能时代，各种手机应用层出不穷，因此关于健康的手机计步器软件也有了。手机计步器软件，它可以帮助你分析记录你所走过的路程，从而帮助计算出你所消耗的热量，制定出更加符合你的运动计划。\n        喜欢运动的伙伴们，赶紧加入我们吧！");
		iv_about_app_logo.setImageResource(R.drawable.logo_trans);
	}

	private CharSequence getVersion() {
		PackageManager pm = getPackageManager();
		try {
			PackageInfo pkgInfo = pm.getPackageInfo(getPackageName(),PackageManager.GET_UNINSTALLED_PACKAGES);
			return pkgInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}

}
