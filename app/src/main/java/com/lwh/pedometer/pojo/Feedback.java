package com.lwh.pedometer.pojo;

/**
 * 用户反馈信息
 */
public class Feedback extends BaseBmobObject {

	private static final long serialVersionUID = -1552145277779785549L;
	String message;//消息内容
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
