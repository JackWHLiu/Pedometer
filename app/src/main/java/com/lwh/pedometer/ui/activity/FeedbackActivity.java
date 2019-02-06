package com.lwh.pedometer.ui.activity;

import com.alwh.framework.core.annotation.OnClick;
import com.lwh.pedometer.R;
import com.lwh.pedometer.base.BaseActivity;
import com.lwh.pedometer.pojo.Feedback;
import com.lwh.pedometer.util.TextUtils;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class FeedbackActivity extends BaseActivity {

	private TextView tv_feedback_titlebar_commit;
	private EditText et_feedback_message;
	
	@Override
	protected void doBusiness() {

	}
	
	@OnClick(R.id.feedback_titlebar_back)
	public void back(View v){
		finish();
	}

	@OnClick(R.id.tv_feedback_titlebar_commit)
	public void commit(View v){
		String message = et_feedback_message.getText().toString().trim();
		if (!TextUtils.checkEmpty(message)) {
			tv_feedback_titlebar_commit.setEnabled(false);
			Feedback feedback = new Feedback();
			feedback.setMessage(message);
			feedback.save(new FeedbackCommitListener());
		}else{
			toast("请输入内容再提交哦^_^!");
		}
	}
	
	class FeedbackCommitListener extends SaveListener<String>{

		@Override
		public void done(String s, BmobException e) {
			if (e==null) {
				toast("反馈成功");
				finish();
			}else{
				toast("反馈失败");
				tv_feedback_titlebar_commit.setEnabled(true);
			}
		}
	}
}
