package com.lwh.pedometer.util;

import java.util.List;

import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.model.ConflictAlgorithm;

import android.content.Context;

/**
 * 数据库相关工具
 * 
 * @author lwh
 *
 */
public class DbUtils {

	private static LiteOrm mLiteOrm;

	public static void createDb(Context context, String dbName) {
		dbName += ".db";
		if (mLiteOrm == null) {
			mLiteOrm = LiteOrm.newCascadeInstance(context, dbName);
			mLiteOrm.setDebugged(true);
		}
	}

	public static LiteOrm getLiteOrm() {
		return mLiteOrm;
	}

	/**
	 * 插入一条记录
	 * 
	 * @param t
	 */
	public static <T> void insert(T t) {
		mLiteOrm.save(t);
	}

	/**
	 * 插入所有记录
	 * 
	 * @param list
	 */
	public static <T> void insertAll(List<T> list) {
		mLiteOrm.save(list);
	}

	/**
	 * 查询所有
	 * 
	 * @param cla
	 * @return
	 */
	public static <T> List<T> queryAll(Class<T> clazz) {
		return mLiteOrm.query(clazz);
	}

	public static <T> List<T> queryAll(Class<T> clazz, String column, boolean isDesc) {
		if (isDesc) {
			return mLiteOrm.<T> query(new QueryBuilder<T>(clazz).appendOrderDescBy(column));
		} else {
			return mLiteOrm.query(clazz);
		}
	}

	/**
	 * 查询 某字段 等于value的值
	 */
	public static <T> List<T> queryByWhere(Class<T> clazz, String field, String[] value) {
		return mLiteOrm.<T> query(new QueryBuilder<T>(clazz).where(field + "=?", value));
	}

	/**
	 * 查询唯一的一条记录，如果记录有多条，不要调用此方法
	 * 
	 * @param clazz
	 * @param field
	 * @param value
	 * @return
	 */
	public static <T> T queryByWhereOnly(Class<T> clazz, String field, String[] value) {
		if (mLiteOrm.<T> query(new QueryBuilder<T>(clazz).where(field + "=?", value)).isEmpty())
			return null;
		else
			return mLiteOrm.<T> query(new QueryBuilder<T>(clazz).where(field + "=?", value)).get(0);
	}

	/**
	 * 查询 某字段 等于 Value的值 可以指定从1-20，就是分页
	 * 
	 * @param clazz
	 * @param field
	 * @param value
	 * @param start
	 * @param length
	 * @return
	 */
	public static <T> List<T> queryByWhereLength(Class<T> clazz, String field, String[] value, int start, int length) {
		return mLiteOrm.<T> query(new QueryBuilder<T>(clazz).where(field + "=?", value).limit(start, length));
	}

	/**
	 * 删除所有
	 * 
	 * @param clazz
	 */
	public static <T> void deleteAll(Class<T> clazz) {
		mLiteOrm.deleteAll(clazz);
	}

	/**
	 * 仅在以存在时更新
	 * 
	 * @param t
	 */
	public static <T> void update(T t) {
		mLiteOrm.update(t, ConflictAlgorithm.Replace);
	}

	public static <T> void updateALL(List<T> list) {
		mLiteOrm.update(list);
	}

	public static void closeDb() {
		mLiteOrm.close();
	}
}
