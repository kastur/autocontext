package com.autocontext;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

class Internal {
	public static class AutocontextServiceConnection {
		Context mContext;
		AutocontextService mService;

		public AutocontextServiceConnection(Context context) {
			mContext = context;
			mService = null;
			context.startService(new Intent(mContext, AutocontextService.class));
	        Intent intent = new Intent(mContext, AutocontextService.class);
	        ServiceConnection serviceConnection = new ServiceConnection() {
				public void onServiceConnected(ComponentName name, IBinder binder) {
					mService = ((AutocontextService.LocalBinder)binder).getService();
				}

				public void onServiceDisconnected(ComponentName name) {
				}
			};

			context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        }

        public AutocontextService getService() {
        	return mService;
        }
	}
}
