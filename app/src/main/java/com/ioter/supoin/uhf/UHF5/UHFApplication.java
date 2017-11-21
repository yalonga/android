package com.ioter.supoin.uhf.UHF5;

import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothSocket;

import com.ioter.supoin.uhf.UHF5helper.ReaderHelper;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class UHFApplication extends Application
{
	
	private Socket mTcpSocket = null;
	private BluetoothSocket mBtSocket = null;
	
	private List<Activity> activities = new ArrayList<Activity>();

	@Override
	public void onCreate() {
		super.onCreate();

		try {
			ReaderHelper.setContext(getApplicationContext());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
	}
	
	public void addActivity(Activity activity) {
		activities.add(activity);
	}

	@Override
	public void onTerminate() {
		super.onTerminate();
		for (Activity activity : activities) {
			try {
				activity.finish();
			} catch (Exception e) {
				;
			}
		}
		
		try {
			if (mTcpSocket != null) mTcpSocket.close();
			if (mBtSocket != null) mBtSocket.close();
		} catch (IOException e) {
		}
		
		mTcpSocket = null;
		mBtSocket = null;

		System.exit(0);
	};
	
	public void setTcpSocket(Socket socket) {
		this.mTcpSocket = socket;
	}
	
	public void setBtSocket(BluetoothSocket socket) {
		this.mBtSocket = socket;
	}
}
