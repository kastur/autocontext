package com.autocontext;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import com.autocontext.observers.CalenderEventContextSensor;


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
		flowManager.registerContextObserver(new CalenderEventContextSensor());
		flowManager.Initialize(getApplicationContext());
	}

    @Override
    public void onDestroy() {
        super.onDestroy();
        flowManager.saveFlows();
    }
}
