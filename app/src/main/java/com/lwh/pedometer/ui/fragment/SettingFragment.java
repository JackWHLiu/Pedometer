package com.lwh.pedometer.ui.fragment;

import com.alwh.framework.core.annotation.OnClick;
import com.lwh.pedometer.R;
import com.lwh.pedometer.base.BaseFragment;
import com.lwh.pedometer.dialog.ColorPickerDialog;
import com.lwh.pedometer.dialog.WheelViewDialog;
import com.lwh.pedometer.ui.activity.AboutActivity;
import com.lwh.pedometer.ui.activity.FeedbackActivity;
import com.lwh.pedometer.ui.activity.RecordActivity;

import android.view.View;
import cn.sharesdk.onekeyshare.OnekeyShare;

public class SettingFragment extends BaseFragment {

	@Override
	protected void doBusiness() {

	}

	@OnClick({ R.id.setting_item_history, R.id.setting_item_goal, R.id.setting_item_share, R.id.setting_item_color,
			R.id.setting_item_feedback, R.id.setting_item_about })
	public void excute(View v) {
		switch (v.getId()) {
		case R.id.setting_item_history:
			startPage(RecordActivity.class,false);
			break;
		case R.id.setting_item_goal:
			new WheelViewDialog(getActivity()).show();
			break;
		case R.id.setting_item_share:
			share();
			break;
		case R.id.setting_item_color:
			new ColorPickerDialog(getActivity()).show();
			break;
		case R.id.setting_item_feedback:
			startPage(FeedbackActivity.class, false);
			break;
		case R.id.setting_item_about:
			startPage(AboutActivity.class, false);
			break;
		}
	}
	
	private void share() {
		 OnekeyShare oks = new OnekeyShare();
		 //关闭sso授权
		 oks.disableSSOWhenAuthorize(); 
		// 分享时Notification的图标和文字  2.5.9以后的版本不调用此方法
		 //oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
		 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		 oks.setTitle("计步器分享");
		 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
		 oks.setTitleUrl("http://www.anzhi.com/soft_2708027.html");
		 // text是分享文本，所有平台都需要这个字段
		 oks.setText("一款轻量级的计步器APP，最懂你的健身神器！");
		 //分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
		 oks.setImageUrl("http://www.mob.com/static/app/icon/1472736843.png");
		 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		 //oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		 // url仅在微信（包括好友和朋友圈）中使用
		 oks.setUrl("http://www.anzhi.com/soft_2708027.html");
		 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
		 oks.setComment("请简洁评论一下吧！");
		 // site是分享此内容的网站名称，仅在QQ空间使用
		 oks.setSite(getString(R.string.app_name));
		 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
		 oks.setSiteUrl("http://www.anzhi.com/soft_2708027.html");
		// 启动分享GUI
		 oks.show(getActivity());		
	}

}
