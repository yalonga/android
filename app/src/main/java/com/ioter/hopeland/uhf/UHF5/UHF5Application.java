package com.ioter.hopeland.uhf.UHF5;

import android.app.Activity;
import android.app.Application;
import android.provider.Settings;

import com.ioter.hopeland.uhf.UHF5helper.ReaderHelper;

import java.util.Stack;

public class UHF5Application extends Application
{

	public static final String SCN_CUST_DB_OUTPUT_MODE = "SCANNER_OUTPUT_MODE";

	private static Stack<Activity> activityStack;
	private static UHF5Application singleton;

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		singleton = this;
		try {
			ReaderHelper.setContext(getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 扫描设置默认为API
		boolean enableApi = (Settings.Secure.getInt(getContentResolver(),
				SCN_CUST_DB_OUTPUT_MODE, 0) > 0);
		if (!enableApi) {
			Settings.System.putInt(getContentResolver(),
					SCN_CUST_DB_OUTPUT_MODE, 1);
		}
	}
	
	// Returns the application instance
	public static UHF5Application getInstance() {
		return singleton;
	}

	public void addActivity(Activity activity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(activity);
	}

	public Activity currentActivity() {
		Activity activity = activityStack.lastElement();
		return activity;
	}


	public void finishActivity() {
		Activity activity = activityStack.lastElement();
		finishActivity(activity);
	}


	public void finishActivity(Activity activity) {
		if (activity != null) {
			activityStack.remove(activity);
			activity.finish();
			activity = null;
		}
	}


	public void finishActivity(Class<?> cls) {
		for (Activity activity : activityStack) {
			if (activity.getClass().equals(cls)) {
				finishActivity(activity);
			}
		}
	}


	public void finishAllActivity() {
		for (int i = 0, size = activityStack.size(); i < size; i++) {
			if (null != activityStack.get(i)) {
				activityStack.get(i).finish();
			}
		}
		activityStack.clear();
	}


	public void AppExit() {
		try {
			finishAllActivity();
		} catch (Exception e) {
		}
	}
}
