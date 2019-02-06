package com.lwh.pedometer.ui.activity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.alwh.framework.app.Activity;
import com.alwh.framework.app.Fragment;
import com.alwh.framework.core.v4compat.FragmentManager;
import com.alwh.framework.core.v4compat.FragmentPagerAdapter;
import com.alwh.framework.core.v4compat.ViewPager;
import com.lwh.pedometer.IConstant;
import com.lwh.pedometer.base.BaseActivity;
import com.lwh.pedometer.base.BaseFragment;
import com.lwh.pedometer.ui.fragment.PedometerFragment;
import com.lwh.pedometer.ui.fragment.SettingFragment;
import com.lwh.pedometer.view.ChannelScrollBar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.ViewConfiguration;

public class MainActivity extends BaseActivity implements IConstant {

	public ChannelScrollBar csb_main;
	ViewPager vp_main;
	private DisplayMetrics mDm;
	List<BaseFragment> mFragments;
	public PedometerFragment mPedometerFragment;
	public SettingFragment mSettingFragment;

	@Override
	protected void doBusiness() {
		setOverflowShowingAlways();
		mFragments = new ArrayList<BaseFragment>();
		mPedometerFragment = new PedometerFragment();
		mSettingFragment = new SettingFragment();
		mFragments.add(mPedometerFragment);
		mFragments.add(mSettingFragment);
		vp_main.setAdapter(new MainPagerAdapter(getSupportFragmentManager()));
		csb_main.setViewPager(vp_main);
		initScrollBar();
	}

	@Override
	public void onBackPressed() {
		moveTaskToBack(true);
		super.onBackPressed();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode==Activity.RESULT_OK) {
			mPedometerFragment.init();
		}
	}
	
	private void initScrollBar() {
		SharedPreferences sp = getSharedPreferences("pedometer", Context.MODE_PRIVATE);
		mDm = getResources().getDisplayMetrics();
		csb_main.setCanExpand(true);
		csb_main.setDividerColor(Color.TRANSPARENT);
		csb_main.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, mDm));
		csb_main.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, mDm));
		csb_main.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18, mDm));
		csb_main.setSelectedTextColor(Color.parseColor("#ffffff"));
		csb_main.setTabBackground(0x00000000);
		if (sp.getInt("theme_color",0)!=0) {
			int themeColor = sp.getInt("theme_color",0);
			csb_main.setIndicatorColor(themeColor);
		}
	}

	

	class MainPagerAdapter extends FragmentPagerAdapter {

		public MainPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		private final String[] titles = { "计步", "设置" };

		@Override
		public CharSequence getPageTitle(int position) {
			return titles[position];
		}

		@Override
		public Fragment getItem(int position) {
			return mFragments.get(position);
		}

		@Override
		public int getCount() {
			return mFragments.size();
		}
	}

	private void setOverflowShowingAlways() {
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
			menuKeyField.setAccessible(true);
			menuKeyField.setBoolean(config, false);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
