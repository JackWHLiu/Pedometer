package com.lwh.pedometer.ui.activity;

import java.util.ArrayList;
import java.util.List;

import com.alwh.framework.app.Activity;
import com.alwh.framework.core.annotation.OnClick;
import com.lwh.pedometer.R;
import com.lwh.pedometer.base.BaseActivity;
import com.lwh.pedometer.util.ACache;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;
import com.wx.wheelview.widget.WheelView.OnWheelItemSelectedListener;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class GoalActivity extends BaseActivity {

	WheelView<String> goal_wheelview;
	TextView tv_goal_info;
	Button btn_goal_confirm;
	String step;
	
	@Override
	protected void doBusiness() {
		goal_wheelview.setSkin(WheelView.Skin.Holo); // common皮肤
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 50; i++) {
			list.add(String.valueOf((i+1)*1000));
		}
		goal_wheelview.setWheelAdapter(new ArrayWheelAdapter(this));
		ACache aCache = ACache.get(this);
		if (aCache.getAsString("goal_step")!=null) {
			goal_wheelview.setSelection(getSelection(Integer.valueOf(aCache.getAsString("goal_step"))));
		}else{
			goal_wheelview.setSelection(0);
		}
		goal_wheelview.setWheelSize(9);
		goal_wheelview.setWheelData(list);  // 数据集合
		goal_wheelview.setOnWheelItemSelectedListener(new OnWheelItemSelectedListener<String>() {

			@Override
			public void onItemSelected(int position, String obj) {
				step = obj.toString();
				String text = "每日目标定为 "+step+" 步";
				SpannableString spanStr = new SpannableString(text);
				spanStr.setSpan(new ForegroundColorSpan(Color.RED),6, text.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
						tv_goal_info.setText(spanStr);
			}
		});
	}
	
	@OnClick(R.id.goal_titlebar_back)
	public void back(View v){
		finish();
	}
	
	public int getSelection(int goalStep){
		return goalStep/1000-1;
	}
	
	@OnClick(R.id.btn_goal_confirm)
	public void confirm(View v){
		ACache aCache = ACache.get(this);
		aCache.put("goal_step",step);//把用户的偏好设置保存到本地
		setResult(Activity.RESULT_OK);//返回结果给Activity刷新界面
		finish();
	}

}
