package com.lwh.pedometer;

import android.content.Context;

public class Toast extends android.widget.Toast implements IToast,ITimeMillis{

	private android.widget.Toast mToast;
	private Context mContext;
	private int mDuration = SECOND;//默认toast时间为1秒
	
	public int getDuration() {
		return mDuration;
	}

	public void setDuration(int duration) {
		this.mDuration = duration;
	}

	public Toast(Context context) {
		super(context);
		mContext = context;
	}
	
	public void toast(String text){
		toast(text, mDuration);
	}
	
	public void toast(String text,int duration){
		if (mToast == null) {
			mToast = Toast.makeText(mContext,text,duration);
		}else{
			mToast.setText(text);
		}
		mToast.show();
	}

}
