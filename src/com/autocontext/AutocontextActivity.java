package com.autocontext;

import java.util.LinkedList;
import java.util.List;

import com.autocontext.Autocontext.Flow;
import com.autocontext.Autocontext.IFlow;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

public class AutocontextActivity extends Activity {
	LinearLayout layout;

    Internal.AutocontextServiceConnection serviceConn;
    GUI.IdentifierFlow identifierFlow;
    GUI.CalendarEventFilterFlow calendarFilterFlow;
    GUI.NotifyFlow notifyFlow;
    GUI.LaunchPackageFlow packageChooserFlow;
    GUI.SubmitView submitView;

    Flow flow;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();

        serviceConn = new Internal.AutocontextServiceConnection(context);
        identifierFlow = new GUI.IdentifierFlow(context);
        calendarFilterFlow = new GUI.CalendarEventFilterFlow(context);
        notifyFlow = new GUI.NotifyFlow(context);
        packageChooserFlow = new GUI.LaunchPackageFlow(context, this);
        
        flow = new Flow();
        flow.add(identifierFlow);
        flow.add(calendarFilterFlow);
        flow.add(notifyFlow);
        flow.add(packageChooserFlow);

        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(identifierFlow.getView());
        layout.addView(calendarFilterFlow.getView());
        layout.addView(notifyFlow.getView());
        layout.addView(packageChooserFlow.getView());

        submitView = new GUI.SubmitView(context, flow, serviceConn);
        layout.addView(submitView.getView());
        setContentView(layout);
    }
        
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == Globals.PACKAGE_CHOOSER_ID) {
    		if (resultCode == RESULT_OK) {
                packageChooserFlow.onActivityResult(requestCode, resultCode, data);
    		}
    	}
    }
}