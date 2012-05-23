package com.autocontext;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.autocontext.observers.CalenderEventContextSensor;
import com.autocontext.observers.ImmediateContextSensor;


public class PactService extends Service {
 
	FlowManager flowManager;
	
	public class LocalBinder extends Binder {
		public FlowManager getFlowManager() {
            return PactService.this.flowManager;
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
		flowManager.registerContextObserver(new ImmediateContextSensor());
		flowManager.registerContextObserver(new CalenderEventContextSensor());
		flowManager.init(getApplicationContext());
        flowManager.getEmptyContextAction();
	}
	
	
}
