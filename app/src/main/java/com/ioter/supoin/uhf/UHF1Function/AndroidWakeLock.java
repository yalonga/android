package com.ioter.supoin.uhf.UHF1Function;

import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class AndroidWakeLock {
	WakeLock wakelock;
	PowerManager pmr;
	public AndroidWakeLock(PowerManager pm) {
		 
			// PowerManager pm = (PowerManager)
			// getSystemService(Context.POWER_SERVICE);
			 pmr=pm;
	}

	@SuppressWarnings("deprecation")
	public void WakeLock() {
		if (wakelock == null)
		{
			wakelock = pmr.newWakeLock(PowerManager.FULL_WAKE_LOCK, this
					.getClass().getCanonicalName());
		}
		wakelock.acquire();
	}

	public void ReleaseWakeLock() {
		if (wakelock != null && wakelock.isHeld()) {
			wakelock.release();
			wakelock = null;
		}
	}
}
