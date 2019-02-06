package com.lwh.pedometer.dialog;

import java.util.ArrayList;
import java.util.List;

import com.lwh.pedometer.base.BaseDialog;
import com.lwh.pedometer.ui.activity.MainActivity;
import com.lwh.pedometer.util.ACache;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;
import com.wx.wheelview.widget.WheelView.OnWheelItemSelectedListener;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

public class WheelViewDialog extends BaseDialog {
	
	private String step;
	Context mContext;

	public WheelViewDialog(Context context) {
		super(context);
		this.mContext = context;
		setHasTitle(false);
		setPositiveLabel("确认");
		setNegativeLabel("取消");
		WheelView<String> goal_wheelview = new WheelView<String>(context);
		goal_wheelview.setSkin(WheelView.Skin.Holo); // common皮肤
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < 50; i++) {
			list.add(String.valueOf((i+1)*1000));
		}
		goal_wheelview.setWheelData(list);  // 数据集合
		goal_wheelview.setWheelAdapter(new ArrayWheelAdapter(context));
		ACache aCache = ACache.get(context);
		if (aCache.getAsString("goal_step")!=null) {
			goal_wheelview.setSelection(getSelection(Integer.valueOf(aCache.getAsString("goal_step"))));
		}else{
			goal_wheelview.setSelection(0);
		}
		goal_wheelview.setWheelSize(3);
		goal_wheelview.setOnWheelItemSelectedListener(new OnWheelItemSelectedListener<String>() {


			@Override
			public void onItemSelected(int position, String obj) {
				step = obj.toString();
				String text = "每日目标定为 "+step+" 步";
				SpannableString spanStr = new SpannableString(text);
				spanStr.setSpan(new ForegroundColorSpan(Color.RED),6, text.length()-1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
		});
		setView(goal_wheelview);
	}
	
	public int getSelection(int goalStep){
		return goalStep/1000-1;
	}

	@Override
	protected boolean OnClickPositiveButton() {
		ACache aCache = ACache.get(getContext());
		aCache.put("goal_step",step);//把用户的偏好设置保存到本地
		((MainActivity) mContext).mPedometerFragment.init();
		dismiss();
		return false;
	}

	@Override
	protected void OnClickNegativeButton() {
		dismiss();
	}

	@Override
	protected void onDismiss() {

	}

}
