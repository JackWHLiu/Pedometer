package com.lwh.pedometer.dialog;

import com.lwh.pedometer.base.BaseDialog;
import com.lwh.pedometer.ui.activity.MainActivity;
import com.lwh.pedometer.util.ImageUtils;
import com.lwh.pedometer.view.ColorPickerView;
import com.lwh.pedometer.view.ColorPickerView.OnColorChangedListener;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.view.ViewGroup.LayoutParams;

public class ColorPickerDialog extends BaseDialog implements OnColorChangedListener{
	int mSelectColor;
	public ColorPickerDialog(Context context) {
		super(context);
		setTitle("选择一个喜欢的颜色");
		ColorPickerView cpv = new ColorPickerView(getContext());
		cpv.setOnColorChangedListenner(this);
		cpv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,ImageUtils.dp2px(240, context)));
		setView(cpv);
		setPositiveLabel("选择");
		setNegativeLabel("取消");
	}

	@Override
	protected boolean OnClickPositiveButton() {
		Editor editor = getContext().getSharedPreferences("pedometer", Context.MODE_PRIVATE).edit();
		editor.putInt("theme_color",mSelectColor);
		editor.commit();
		((MainActivity) mContext).mPedometerFragment.init();
		((MainActivity)mContext).csb_main.setIndicatorColor(mSelectColor);
		dismiss();
		return false;
	}
	
	@Override
	public void onColorChanged(int color, int originalColor, float saturation) {
		mSelectColor = color;
	}

	@Override
	protected void OnClickNegativeButton() {

	}

	@Override
	protected void onDismiss() {

	}

}
