package com.lwh.pedometer.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * 适配器基类，继承此类可以适配任何类型的Bean
 * @author lwh
 */
public abstract class BaseAdapter<Bean> extends android.widget.BaseAdapter {

	private List<Bean> mBeans = null;
	private LayoutInflater mInflater = null;
	private static final String METHOD_INFLATE = "inflate";
	private View mConvertView;
	private ViewHolder<?> mViewHolder;

	public BaseAdapter(Context context) {
		mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public BaseAdapter(Context context, List<Bean> beans) {
		this(context);
		bindDataSet(beans);
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ViewId {
		int[]value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface ItemLayout {
		int value();
	}

	/**
	 * Binds data set for this adapter.You can't invoke the method when the
	 * adapter has data set.It is called in the Constructor commonly.
	 * 为本是适配器绑定数据集。当适配器有数据的时候，你不能调用这个方法。它通常在构造方法中被调用。
	 */
	public void bindDataSet(List<Bean> beans) {
		if (mBeans == null) {
			mBeans = beans;
			notifyDataSetChanged();
		} else
			throw new IllegalStateException("Data set is already binded.");
	}

	@Override
	public int getCount() {
		if (mBeans != null) {
			return mBeans.size();
		} else {
			return Integer.MIN_VALUE;
		}
	}

	@Override
	public Object getItem(int position) {
		if (position >= 0 && position < mBeans.size()) {
			return mBeans.get(position);
		} else {
			return null;
		}
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 添加一个条目
	 */
	public void addItem(Bean data) {
		mBeans.add(data);
		notifyDataSetChanged();
	}

	/**
	 * 在指定位置添加一个条目
	 */
	public void addItem(int position, Bean bean) {
		mBeans.add(position, bean);
		notifyDataSetChanged();
	}

	/**
	 * 在适配器末尾添加一堆条目
	 */
	public void addItems(List<Bean> beans) {
		mBeans.addAll(beans);
		notifyDataSetChanged();
	}

	/**
	 * 从适配器的指定位置开始，添加一堆条目
	 */
	public void addItems(int start, List<Bean> beans) {
		mBeans.addAll(start, beans);
		notifyDataSetChanged();
	}

	/**
	 * 替换指定位置的条目
	 */
	public void replaceItem(int position, Bean bean) {
		mBeans.set(position, bean);
		notifyDataSetChanged();
	}

	/**
	 * 从指定位置开始替换一堆条目
	 */
	public void replaceItems(int start, List<Bean> beans) {
		for (Bean bean : beans) {
			mBeans.set(start, bean);
			start++;
		}
	}

	/**
	 * Replaces all data in BaseAdapter whether the data is empty or not.
	 * 替换适配器中所有条目，无论数据是否为空
	 */
	public void replace(List<Bean> beans) {
		mBeans = beans;
		notifyDataSetInvalidated();
	}
	
	public void removeItem(Bean bean){
		mBeans.remove(bean);
		notifyDataSetChanged();
	}

	/**
	 * 从指定位置移除一个条目
	 */
	public void removeItem(int position) {
		mBeans.remove(position);
		notifyDataSetChanged();
	}

	/**
	 * 移除连续下标的一堆条目
	 * @param start 开始的下标
	 * @param end 结束的下标
	 */
	public void removeItems(int start, int end) {
		for (int i = start; i <= end; i++) {
			mBeans.remove(i);
		}
		notifyDataSetChanged();
	}

	/**
	 * 清空所有条目
	 */
	public void clear() {
		mBeans.clear();
		notifyDataSetChanged();
	}

	/**
	 * 加载布局
	 * @hide
	 */
	private View inflateView()
			throws NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Class<?> adapterClazz = getClass();
		ItemLayout adapterLayout = adapterClazz.getAnnotation(ItemLayout.class);
		int layoutId = adapterLayout.value();
		Class<?> inflaterClazz = LayoutInflater.class;
		Method inflateMethod = inflaterClazz.getMethod(METHOD_INFLATE, int.class, ViewGroup.class);
		return (View) inflateMethod.invoke(mInflater, layoutId, null);
	}

	/**
	 * 获取布局文件中条目的id
	 * @hide
	 */
	private int[] getViewIds() {
		Class<?> adapterClazz = getClass();
		ViewId viewId = adapterClazz.getAnnotation(ViewId.class);
		if (viewId != null) {
			return viewId.value();
		} else {
			return null;
		}
	}

	/**
	 * 给条目的子控件绑定数据
	 * @param position 位置
	 * @param views 存放了条目中的控件，通过get(int id)拿
	 */
	protected abstract <T extends View> void onBindView(int position, SparseArray<T> views);

	/**
	 * 获取所有的Bean
	 */
	public List<Bean> getBeans() {
		return mBeans;
	}

	@Override
	public final View getView(int position, View convertView, ViewGroup parent) {
		mConvertView = convertView;
		if (mConvertView == null) {
			mViewHolder = new ViewHolder();
			try {
				mConvertView = inflateView();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			}
			int[] viewIds = getViewIds();
			for (int viewId : viewIds) {
				mViewHolder.get(viewId);
			}
			mConvertView.setTag(mViewHolder);
		} else {
			mViewHolder = (ViewHolder) mConvertView.getTag();
		}
		onBindView(position, mViewHolder.mViews);
		return mConvertView;
	}

	public class ViewHolder<T extends View> {

		private SparseArray<T> mViews;

		private ViewHolder() {
			mViews = new SparseArray<T>();
		}

		@SuppressWarnings("unchecked")
		private T get(int viewId) {
			View view = mViews.get(viewId);
			if (view == null) {
				view = mConvertView.findViewById(viewId);
				mViews.put(viewId, (T) view);
			}
			return (T) view;
		}
	}
}
