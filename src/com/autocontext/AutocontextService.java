package com.autocontext;

import com.autocontext.Autocontext.FlowManager;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;


public class AutocontextService extends Service {
 
	FlowManager flowManager;
	
	public class LocalBinder extends Binder {
		public FlowManager getFlowManager() {
            return AutocontextService.this.flowManager;
        }
	}
	
	@Override
	public IBinder onBind(Intent bindIntent) {
		return new LocalBinder();
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		flowManager = new FlowManager();
	}
	
	
}
