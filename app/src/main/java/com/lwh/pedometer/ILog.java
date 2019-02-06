package com.lwh.pedometer;

/**
 * 日志接口
 */
public interface ILog {
	boolean DEBUG_MODE = true;// 是否为调试模式，应用上线时改为false

	void info(String tag, String msg);

	void error(String tag, String msg);

	void debug(String tag, String msg);

	void warn(String tag, String msg);

	void verbose(String tag, String msg);
}
