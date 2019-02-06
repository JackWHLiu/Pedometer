package com.lwh.pedometer.view;

import java.util.Locale;

import com.alwh.framework.core.v4compat.ViewPager;
import com.alwh.framework.core.v4compat.ViewPager.OnPageChangeListener;
import com.lwh.pedometer.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 频道滚动条
 */
public class ChannelScrollBar extends HorizontalScrollView {

	Paint mRectPaint;
	Paint mDividerPaint;
	ViewPager mPager;
	PageListener mPagerListener;
	public OnPageChangeListener mDelegatePageListener;
	private LinearLayout mTabContainer;
	Locale mLocale;
	private LinearLayout.LayoutParams mDefaultTabLayoutParams;
	private LinearLayout.LayoutParams mExpandedTabLayoutParams;

	private int mIndicatorColor = 0xFF6471CD;
	private int mUnderlineColor = 0x00389CFF;
	private int mDividerColor = 0x00389CFF;
	private int mIndicatorHeight = 8;
	private int mUnderlineHeight = 2;
	private int mDividerPadding = 12;
	private int mTabHorizontalPadding = 20;
	private int mTabColor;
	private boolean mCanExpand = false;
	private int mScrollOffset;
	private boolean mTextAllCaps = true;
	
	private int mTabTextSize = 12;
	private int mTabTextColor = Color.WHITE;
	private int mTabCount;
	private int mCurrPosition;
	private float mCurrPositionOffset = 0.0f;
	private int mSelectedPosition;
	private int mDividerWidth = 1;
	private int mLastScrollX;
	private int mSelectedTabTextColor = 0xFF666666;
	private Typeface mTabTypeface = null;
	private int mTabTypefaceStyle = Typeface.NORMAL;
	private static final int[] ATTRS = new int[] { android.R.attr.textSize, android.R.attr.textColor };

	public ChannelScrollBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context,attrs);
	}

	public interface IconTabProvider {
		public int getPageIconResId(int position);
	}

	private void init(Context context, AttributeSet attrs) {
		setFillViewport(true);
		setWillNotDraw(false);
		mPagerListener = new PageListener();
		mDefaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		mExpandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);
		initTabContainer();
		initAttrs(context, attrs);
		initPaint();
		if (mLocale == null) {
			mLocale = getResources().getConfiguration().locale;
		}
	}

	private void initAttrs(Context context, AttributeSet attrs) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		mScrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mScrollOffset, dm);
		mIndicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mIndicatorHeight, dm);
		mUnderlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mUnderlineHeight, dm);
		mDividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerPadding, dm);
		mTabHorizontalPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mTabHorizontalPadding, dm);
		mDividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mDividerWidth, dm);
		mTabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, mTabTextSize, dm);
		TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);
		mTabTextSize = a.getDimensionPixelSize(0, mTabTextSize);
		mTabTextColor = a.getColor(1, mTabTextColor);
		a.recycle();
		a = context.obtainStyledAttributes(attrs, R.styleable.ChannelScrollBar);
		mIndicatorColor = a.getColor(R.styleable.ChannelScrollBar_indicatorColor, mIndicatorColor);
		mUnderlineColor = a.getColor(R.styleable.ChannelScrollBar_underlineColor, mUnderlineColor);
		mDividerColor = a.getColor(R.styleable.ChannelScrollBar_dividerColor, mDividerColor);
		mIndicatorHeight = a.getDimensionPixelSize(R.styleable.ChannelScrollBar_indicatorHeight, mIndicatorHeight);
		mUnderlineHeight = a.getDimensionPixelSize(R.styleable.ChannelScrollBar_underlineHeight, mUnderlineHeight);
		mDividerPadding = a.getDimensionPixelSize(R.styleable.ChannelScrollBar_dividerPadding, mDividerPadding);
		mTabHorizontalPadding = a.getDimensionPixelSize(R.styleable.ChannelScrollBar_tabHorizontalPadding, mTabHorizontalPadding);
		mTabColor = a.getResourceId(R.styleable.ChannelScrollBar_tabColor, mTabColor);
		mCanExpand = a.getBoolean(R.styleable.ChannelScrollBar_canExpand, mCanExpand);
		mScrollOffset = a.getDimensionPixelSize(R.styleable.ChannelScrollBar_scrollOffset, mScrollOffset);
		mTextAllCaps = a.getBoolean(R.styleable.ChannelScrollBar_textAllCaps, mTextAllCaps);
		a.recycle();
	}

	private void initTabContainer() {
		mTabContainer = new LinearLayout(getContext());
		mTabContainer.setOrientation(LinearLayout.HORIZONTAL);
		mTabContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		addView(mTabContainer);
	}

	private void initPaint() {
		mRectPaint = new Paint();
		mRectPaint.setAntiAlias(true);// 抗锯齿
		mRectPaint.setStyle(Style.FILL);
		mDividerPaint = new Paint();
		mDividerPaint.setAntiAlias(true);
		mDividerPaint.setStrokeWidth(mDividerWidth);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (isInEditMode() || mTabCount == 0) {
			return;
		}
		int height = getHeight();// 拿到导航条的高度
		// draw underline
		mRectPaint.setColor(mUnderlineColor);
		canvas.drawRect(0, height - mUnderlineHeight, mTabContainer.getWidth(), height, mRectPaint);
		// draw indicator line
		mRectPaint.setColor(mIndicatorColor);

		// default: line below current tab
		View currTab = mTabContainer.getChildAt(mCurrPosition);
		float lineLeft = currTab.getLeft();
		float lineRight = currTab.getRight();

		// if there is an offset, start interpolating left and right coordinates between current and next tab
		if (mCurrPositionOffset > 0f && mCurrPosition < mTabCount - 1) {
			View nextTab = mTabContainer.getChildAt(mCurrPosition + 1);
			final float nextTabLeft = nextTab.getLeft();
			final float nextTabRight = nextTab.getRight();
			lineLeft = (mCurrPositionOffset * nextTabLeft + (1f - mCurrPositionOffset) * lineLeft);
			lineRight = (mCurrPositionOffset * nextTabRight + (1f - mCurrPositionOffset) * lineRight);
		}
		canvas.drawRect(lineLeft, height - mIndicatorHeight, lineRight, height, mRectPaint);

		// draw divider
		mDividerPaint.setColor(mDividerColor);
		for (int i = 0; i < mTabCount - 1; i++) {
			View tab = mTabContainer.getChildAt(i);
			canvas.drawLine(tab.getRight(), mDividerPadding, tab.getRight(), height - mDividerPadding, mDividerPaint);
		}
	}
	
	public void notifyDataSetChanged() {
		mTabContainer.removeAllViews();
		mTabCount = mPager.getAdapter().getCount();
		for (int i = 0; i < mTabCount; i++) {
			if (mPager.getAdapter() instanceof IconTabProvider) {
				addIconTab(i, ((IconTabProvider) mPager.getAdapter()).getPageIconResId(i));
			} else {
				addTextTab(i, mPager.getAdapter().getPageTitle(i).toString());
			}

		}
		updateTabStyle();
		getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@SuppressWarnings("deprecation")
			public void onGlobalLayout() {
				getViewTreeObserver().removeGlobalOnLayoutListener(this);
				mCurrPosition = mPager.getCurrentItem();
				scrollToChild(mCurrPosition, 0);
			}
		});
	}
	
	private void addTextTab(final int position, String title) {
		TextView tab = new TextView(getContext());
		tab.setText(title);
		tab.setGravity(Gravity.CENTER);
		tab.setSingleLine();
		addTab(position, tab);
	}

	private void addIconTab(final int position, int resId) {
		ImageButton tab = new ImageButton(getContext());
		tab.setImageResource(resId);
		addTab(position, tab);
	}

	private void addTab(final int position, View tab) {
		tab.setFocusable(true);
		tab.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mPager.setCurrentItem(position);
			}
		});
		tab.setPadding(mTabHorizontalPadding, 0, mTabHorizontalPadding, 0);
		mTabContainer.addView(tab, position, mCanExpand ? mExpandedTabLayoutParams : mDefaultTabLayoutParams);
	}
	
	private class PageListener implements OnPageChangeListener {

		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			mCurrPosition = position;
			mCurrPositionOffset = positionOffset;
			scrollToChild(position, (int) (positionOffset * mTabContainer.getChildAt(position).getWidth()));
			invalidate();
			if (mDelegatePageListener != null) {
				mDelegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				scrollToChild(mPager.getCurrentItem(), 0);
			}

			if (mDelegatePageListener != null) {
				mDelegatePageListener.onPageScrollStateChanged(state);
			}
		}

		public void onPageSelected(int position) {
			mSelectedPosition = position;
			updateTabStyle();
			if (mDelegatePageListener != null) {
				mDelegatePageListener.onPageSelected(position);
			}
		}
	}
	
	private void scrollToChild(int position, int offset) {
		if (mTabCount == 0) {
			return;
		}
		int newScrollX = mTabContainer.getChildAt(position).getLeft() + offset;
		if (position > 0 || offset > 0) {
			newScrollX -= mScrollOffset;
		}
		if (newScrollX != mLastScrollX) {
			mLastScrollX = newScrollX;
			scrollTo(newScrollX, 0);
		}
	}
	
	private void updateTabStyle() {
		for (int i = 0; i < mTabCount; i++) {
			View v = mTabContainer.getChildAt(i);
			v.setBackgroundColor(mTabColor);
			if (v instanceof TextView) {
				TextView tab = (TextView) v;
				tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTabTextSize);
				tab.setTypeface(mTabTypeface, mTabTypefaceStyle);
				tab.setTextColor(mTabTextColor);
				// setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
				// pre-ICS-build
				if (mTextAllCaps) {
					if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
						tab.setAllCaps(true);
					} else {
						tab.setText(tab.getText().toString().toUpperCase(mLocale));
					}
				}
				if (i == mSelectedPosition) {
					tab.setTextColor(mSelectedTabTextColor);
				}
			}
		}
	}

	public void setViewPager(ViewPager pager) {
		this.mPager = pager;
		if (pager.getAdapter() == null) {
			throw new IllegalStateException("ViewPager does not have adapter instance.");
		}
		pager.setOnPageChangeListener(mPagerListener);
		notifyDataSetChanged();
	}

	public void setOnPageChangeListener(OnPageChangeListener l) {
		this.mDelegatePageListener = l;
	}

	public void setIndicatorColor(int indicatorColor) {
		this.mIndicatorColor = indicatorColor;
		invalidate();
	}

	public void setIndicatorColorResource(int resId) {
		this.mIndicatorColor = getResources().getColor(resId);
		invalidate();
	}

	public int getIndicatorColor() {
		return this.mIndicatorColor;
	}

	public void setIndicatorHeight(int indicatorHeight) {
		this.mIndicatorHeight = indicatorHeight;
		invalidate();
	}

	public int getIndicatorHeight() {
		return mIndicatorHeight;
	}

	public void setUnderlineColor(int underlineColor) {
		this.mUnderlineColor = underlineColor;
		invalidate();
	}

	public void setUnderlineColorResource(int resId) {
		this.mUnderlineColor = getResources().getColor(resId);
		invalidate();
	}

	public int getUnderlineColor() {
		return mUnderlineColor;
	}

	public void setDividerColor(int dividerColor) {
		this.mDividerColor = dividerColor;
		invalidate();
	}

	public void setDividerColorResource(int resId) {
		this.mDividerColor = getResources().getColor(resId);
		invalidate();
	}

	public int getDividerColor() {
		return mDividerColor;
	}

	public void setUnderlineHeight(int underlineHeight) {
		this.mUnderlineHeight = underlineHeight;
		invalidate();
	}

	public int getUnderlineHeight() {
		return mUnderlineHeight;
	}

	public void setDividerPadding(int dividerPadding) {
		this.mDividerPadding = dividerPadding;
		invalidate();
	}

	public int getDividerPadding() {
		return mDividerPadding;
	}

	public void setScrollOffset(int scrollOffset) {
		this.mScrollOffset = scrollOffset;
		invalidate();
	}

	public int getScrollOffset() {
		return mScrollOffset;
	}

	public void setCanExpand(boolean canExpand) {
		this.mCanExpand = canExpand;
		notifyDataSetChanged();
	}

	public boolean getCanExpand() {
		return mCanExpand;
	}

	public boolean isTextAllCaps() {
		return mTextAllCaps;
	}

	public void setAllCaps(boolean textAllCaps) {
		this.mTextAllCaps = textAllCaps;
	}
	
	public void setTextSize(int textSize) {
		this.mTabTextSize = textSize;
		updateTabStyle();
	}
	
	public int getTextSize() {
		return mTabTextSize;
	}

	public void setTextColor(int textColor) {
		this.mTabTextColor = textColor;
		updateTabStyle();
	}
	
	public void setTextColorResource(int resId) {
		this.mTabTextColor = getResources().getColor(resId);
		updateTabStyle();
	}

	public int getTextColor() {
		return mTabTextColor;
	}
	
	public void setSelectedTextColor(int textColor) {
		this.mSelectedTabTextColor = textColor;
		updateTabStyle();
	}
	
	public void setSelectedTextColorResource(int resId) {
		this.mSelectedTabTextColor = getResources().getColor(resId);
		updateTabStyle();
	}

	public int getSelectedTextColor() {
		return mSelectedTabTextColor;
	}

	public void setTypeface(Typeface typeface, int style) {
		this.mTabTypeface = typeface;
		this.mTabTypefaceStyle = style;
		updateTabStyle();
	}

	public void setTabBackground(int resId) {
		this.mTabColor = resId;
		updateTabStyle();
	}

	public int getTabBackground() {
		return mTabColor;
	}

	public void setTabHorizontalPadding(int padding) {
		this.mTabHorizontalPadding = padding;
		updateTabStyle();
	}

	public int TabHorizontalPadding() {
		return mTabHorizontalPadding;
	}
	
	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState savedState = (SavedState) state;
		super.onRestoreInstanceState(savedState.getSuperState());
		mCurrPosition = savedState.currPosition;
		requestLayout();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		SavedState savedState = new SavedState(superState);
		savedState.currPosition = mCurrPosition;
		return savedState;
	}

	static class SavedState extends BaseSavedState {
		int currPosition;

		public SavedState(Parcelable superState) {
			super(superState);
		}

		private SavedState(Parcel in) {
			super(in);
			currPosition = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			super.writeToParcel(dest, flags);
			dest.writeInt(currPosition);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}
}
