package com.lwh.pedometer.pojo;

import cn.bmob.v3.BmobObject;

/**
 * 基本的Bmob对象
 */
public class BaseBmobObject extends BmobObject {

	private static final long serialVersionUID = -484971129310665155L;
	private String userId;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
}
