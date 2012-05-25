package com.autocontext;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;


public class BaseFlowActivity extends Activity {

    final int MESSAGE_ON_SERVICE_CONNECTED = 1;
    protected FlowManager mFlowManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.loading);

        Context applicationContext = getApplicationContext();
        Intent serviceIntent = new Intent(applicationContext, PactService.class);
        startService(serviceIntent);
		bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public void handleOnServiceConnected(Message msg) {
        mFlowManager = (FlowManager)msg.obj;
    }

    protected Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_ON_SERVICE_CONNECTED) {
                handleOnServiceConnected(msg);
            } else {
                super.handleMessage(msg);
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            FlowManager flowManager = ((PactService.LocalBinder)binder).getFlowManager();
            Message msg = Message.obtain(mHandler);
            msg.what=  MESSAGE_ON_SERVICE_CONNECTED;
            msg.obj =  flowManager;
            msg.sendToTarget();
        }

        public void onServiceDisconnected(ComponentName name) { }
    };
}