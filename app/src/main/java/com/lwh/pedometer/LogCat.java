package com.lwh.pedometer;

import android.util.Log;

public class LogCat implements ILog{

	@Override
	public void info(String tag, String msg) {
		if (DEBUG_MODE) {
			Log.i(tag, msg);
		}
	}

	@Override
	public void error(String tag, String msg) {
		if (DEBUG_MODE) {
			Log.e(tag, msg);
		}
	}

	@Override
	public void debug(String tag, String msg) {
		if (DEBUG_MODE) {
			Log.d(tag, msg);
		}
	}

	@Override
	public void warn(String tag, String msg) {
		if (DEBUG_MODE) {
			Log.w(tag, msg);
		}
	}

	@Override
	public void verbose(String tag, String msg) {
		if (DEBUG_MODE) {
			Log.v(tag, msg);
		}
	}
}
