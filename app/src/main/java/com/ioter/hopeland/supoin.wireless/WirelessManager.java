package com.ioter.hopeland.supoin.wireless;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class WirelessManager {
	private static final String TAG = "WirelessManager";
	private static final String BIND_ACTION = "com.supoin.wireless.WirelessService";
	private IWirelessService mWirelessService;
	private static Context mContext;
	private static WirelessManager mWiManager;
	private WirelessServiceConnectListen mListener;

	public static WirelessManager instans(Context context) {
		mContext = context;
		if (mWiManager != null) {
			return mWiManager;
		} else {
			mWiManager = new WirelessManager();
		}
		return mWiManager;
	}

	public int open() {

		if (mWirelessService != null) {
			try {
				return mWirelessService.open();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -4;
	}

	public int close() {

		if (mWirelessService != null) {
			try {
				return mWirelessService.close();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -4;

	}

	public int connectTo433() {

		if (mWirelessService != null) {
			try {
				return mWirelessService.connectTo433();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -4;
	}

	public int writeTo433(String code) {

		if (mWirelessService != null) {
			try {
				return mWirelessService.writeTo433(code);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return -4;
	}

	public void WirelessServiceConnect(WirelessServiceConnectListen listener) {
		mListener = listener;
		Intent it = new Intent();
		it.setAction(BIND_ACTION);
		it.setPackage("com.supoin.wireless");
		mContext.bindService(it, mserviceConnection, mContext.BIND_AUTO_CREATE);
		
	}

	ServiceConnection mserviceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			Log.d(TAG, "onServiceDisconnected");
			mWirelessService = null;
			mListener.disConnect();
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			Log.d(TAG, "onServiceConnected");
			mWirelessService = IWirelessService.Stub.asInterface(service);
			mListener.connected();
		}
	};
	
	public interface WirelessServiceConnectListen{
		
		public void connected();
		public void disConnect();
	}
}
