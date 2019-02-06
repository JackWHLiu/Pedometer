package com.lwh.pedometer.base;

import com.lwh.pedometer.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Dialog基类
 * 
 * @author lwh
 */
public abstract class BaseDialog extends Dialog {

	protected Context mContext;
	/**
	 * 确认按钮的监听事件
	 */
	protected OnClickListener mOnConfirmListener;
	/**
	 * 取消按钮的监听事件
	 */
	protected OnClickListener mOnCancelListener;
	protected OnDismissListener mOnDismissListener;
	protected View mView;
	protected Button mPositiveButton;
	protected Button mNegativeButton;
	private boolean mIsFullScreen;
	private boolean mHasTitle = true;
	/**
	 * 对话框的宽度
	 */
	private int mWidth;
	/**
	 * 对话框的高度
	 */
	private int mHeight;
	private int mX;
	private int mY;
	private int mTitleIcon = 0;
	/**
	 * 对话框的标题
	 */
	private String mTitle;
	/**
	 * 对话框的信息
	 */
	private String mMessage;
	/**
	 * 积极按钮的文字
	 */
	private String mPositiveLabel;
	/**
	 * 消极按钮的文字
	 */
	private String mNegativeLabel;

	private boolean mIsCancel;// 默认是否可点击back按键/点击外部区域取消对话框

	public boolean isCancel() {
		return mIsCancel;
	}

	public void setCancel(boolean isCancel) {
		this.mIsCancel = isCancel;
	}

	public BaseDialog(Context context) {
		super(context, R.style.alert);
		this.mContext = context;
	}

	/**
	 * dp转px
	 */
	public int dp2px(float value) {
		final float scale = getContext().getResources().getDisplayMetrics().densityDpi;
		return (int) (value * (scale / 160) + 0.5f);
	}

	/**
     * sp转px.
     */
    public int sp2px(float value) {
    	 float scale = mContext.getResources().getDisplayMetrics().scaledDensity;  
         return (int) (value * scale + 0.5f);  
    }
	
	/**
	 * 创建事件
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		init();
	}

	private void init() {
		Context context = getContext();
		LinearLayout dialogLayout = new LinearLayout(context);
		dialogLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		dialogLayout.setOrientation(LinearLayout.VERTICAL);
		dialogLayout.setMinimumWidth(dp2px(200));
		dialogLayout.setBackgroundColor(Color.WHITE);
		LinearLayout topLayout = new LinearLayout(context);
		topLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, dp2px(45)));
		topLayout.setGravity(Gravity.CENTER_VERTICAL);
		TextView titleTextView = new TextView(context);
		titleTextView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		titleTextView.setText(getTitle());
		titleTextView.setTextColor(0xFF5BAB5F);
		titleTextView.setTextSize(18);
		titleTextView.setEllipsize(TruncateAt.END);
		titleTextView.setPadding(dp2px(10), 0, dp2px(10), 0);
		topLayout.addView(titleTextView);
		dialogLayout.addView(topLayout);
		View view = new View(context);
		view.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,dp2px(0.2f)));
		view.setBackgroundColor(0xFFCACACA);
		// 设置标题和消息
		if (mHasTitle) {
			topLayout.setVisibility(View.VISIBLE);
			view.setVisibility(View.VISIBLE);
		} else {
			topLayout.setVisibility(View.GONE);
			view.setVisibility(View.GONE);
		}
		RelativeLayout layoutContainer = new RelativeLayout(context);
		layoutContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT));
		TextView messageTextView = new TextView(context);
		messageTextView.setText(getMessage());
		messageTextView.setTextColor(Color.GRAY);
		if (mView != null) {
			layoutContainer.addView(mView);
		} else {
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			params.topMargin = dp2px(40);
			params.bottomMargin = dp2px(40);
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			layoutContainer.addView(messageTextView,params);
		}
		LinearLayout bottomLayout = new LinearLayout(context);
		bottomLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,dp2px(40)));
		bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
		// 设置按钮事件监听
		mPositiveButton = new Button(context);
		mPositiveButton.setLayoutParams(new LayoutParams(0,LayoutParams.MATCH_PARENT,1));
		mPositiveButton.setBackgroundResource(R.drawable.selector_dialog_btn);
		mNegativeButton = new Button(context);
		mNegativeButton.setLayoutParams(new LayoutParams(0,LayoutParams.MATCH_PARENT,1));
		mNegativeButton.setBackgroundResource(R.drawable.selector_dialog_btn);
		if (mPositiveLabel != null && mPositiveLabel.length() > 0) {
			mPositiveButton.setText(mPositiveLabel);
			mPositiveButton.setTextColor(0xFF5BAB5F);
			mPositiveButton.setOnClickListener(getOnPositiveButtonClickListener());
		} else {
			mPositiveButton.setVisibility(View.GONE);
		}
		if (mNegativeLabel != null && mNegativeLabel.length() > 0) {
			mNegativeButton.setText(mNegativeLabel);
			mNegativeButton.setOnClickListener(getOnNegativeButtonClickListener());
		} else {
			mNegativeButton.setVisibility(View.GONE);
		}
		View view2 = new View(context);
		view2.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,dp2px(0.5f)));
		view2.setBackgroundColor(0xFFCACACA);
		dialogLayout.addView(view);
		View view3 = new View(context);
		view3.setLayoutParams(new LayoutParams(dp2px(0.5f),LayoutParams.MATCH_PARENT));
		view3.setBackgroundColor(0xFFCACACA);
		dialogLayout.addView(layoutContainer);
		dialogLayout.addView(view2);
		bottomLayout.addView(mNegativeButton);
		bottomLayout.addView(view3);
		bottomLayout.addView(mPositiveButton);
		dialogLayout.addView(bottomLayout);
		// 设置对话框的位置和大小
		WindowManager.LayoutParams params = getWindow().getAttributes();
		if (getWidth() > 0)
			params.width = getWidth();
		if (getHeight() > 0)
			params.height = getHeight();
		if (getX() > 0)
			params.width = getX();
		if (getY() > 0)
			params.height = getY();
		if (mIsFullScreen) {
			params.width = WindowManager.LayoutParams.MATCH_PARENT;
			params.height = WindowManager.LayoutParams.MATCH_PARENT;
		}
		if (mIsCancel) {
			setCanceledOnTouchOutside(true);
			setCancelable(true);
		} else {
			setCanceledOnTouchOutside(false);
			setCancelable(false);
		}
		getWindow().setAttributes(params);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
		setOnDismissListener(getOnDismissListener());
		setContentView(dialogLayout);
	}

	protected OnDismissListener getOnDismissListener() {
		return new OnDismissListener() {
			public void onDismiss(DialogInterface arg0) {
				BaseDialog.this.onDismiss();
				BaseDialog.this.setOnDismissListener(null);
				mView = null;
				mContext = null;
				mPositiveButton = null;
				mNegativeButton = null;
				if (mOnDismissListener != null) {
					mOnDismissListener.onDismiss(null);
				}
			}
		};
	}

	protected View.OnClickListener getOnPositiveButtonClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				if (OnClickPositiveButton())
					BaseDialog.this.dismiss();
			}
		};
	}

	protected View.OnClickListener getOnNegativeButtonClickListener() {
		return new View.OnClickListener() {
			public void onClick(View v) {
				OnClickNegativeButton();
				BaseDialog.this.dismiss();
			}
		};
	}

	protected OnFocusChangeListener getOnFocusChangeListener() {
		return new OnFocusChangeListener() {
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus && v instanceof EditText) {
					((EditText) v).setSelection(0, ((EditText) v).getText().length());
				}
			}
		};
	}

	public void setOnConfirmListener(OnClickListener listener) {
		mOnConfirmListener = listener;
	}

	public void setOnDismissListener(OnDismissListener listener) {
		mOnDismissListener = listener;
	}

	public void setOnCancelListener(OnClickListener listener) {
		mOnCancelListener = listener;
	}

//	protected abstract void onBuild();

	protected abstract boolean OnClickPositiveButton();

	protected abstract void OnClickNegativeButton();

	protected abstract void onDismiss();

	public String getTitle() {
		return mTitle;
	}

	public void setTitle(String title) {
		this.mTitle = title;
	}

	public int getTitleIcon() {
		return mTitleIcon;
	}

	public void setmTitleIcon(int titleIcon) {
		this.mTitleIcon = titleIcon;
	}

	public int getIconTitle() {
		return mTitleIcon;
	}

	protected String getMessage() {
		return mMessage;
	}

	protected void setMessage(String message) {
		this.mMessage = message;
	}

	protected View getView() {
		return mView;
	}

	protected void setView(View view) {
		this.mView = view;
	}

	public boolean getIsFullScreen() {
		return mIsFullScreen;
	}

	public void setIsFullScreen(boolean isFullScreen) {
		this.mIsFullScreen = isFullScreen;
	}

	public boolean isHasTitle() {
		return mHasTitle;
	}

	public void setHasTitle(boolean hasTitle) {
		this.mHasTitle = hasTitle;
	}

	protected int getWidth() {
		return mWidth;
	}

	protected void setWidth(int width) {
		this.mWidth = width;
	}

	protected int getHeight() {
		return mHeight;
	}

	protected void setHeight(int height) {
		this.mHeight = height;
	}

	public int getX() {
		return mX;
	}

	public void setX(int x) {
		this.mX = x;
	}

	public int getY() {
		return mY;
	}

	public void setY(int y) {
		this.mY = y;
	}

	public String getPositiveLabel() {
		return mPositiveLabel;
	}

	public void setPositiveLabel(String positiveLabel) {
		this.mPositiveLabel = positiveLabel;
	}

	public String getNegativeLabel() {
		return mNegativeLabel;
	}

	public void setNegativeLabel(String negativeLabel) {
		this.mNegativeLabel = negativeLabel;
	}
}