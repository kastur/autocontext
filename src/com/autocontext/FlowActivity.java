package com.autocontext;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;


public class FlowActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context applicationContext = getApplicationContext();

        Intent serviceIntent = new Intent(applicationContext, PactService.class);
        startService(serviceIntent);
		bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

        setContentView(R.layout.main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.flow_actions, menu);
        return true;
    }

    public void onConnect() {
        Render();
    }
    
    public void Render() {
        Activity activity = this;

        Flow flow = mFlowManager.getNewFlow();
        setContentView(flow.getEditable(activity).getEditView());
    }

    private OnClickListener clickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.trigger_next_calendar) {
                mFlowManager.triggerNextCalendarContext();
            }
        }
    };

    private ServiceConnection serviceConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName name, IBinder binder) {
            mFlowManager = ((PactService.LocalBinder)binder).getFlowManager();
            onConnect();
        }

        public void onServiceDisconnected(ComponentName name) { }
    };

    private FlowManager mFlowManager;
}