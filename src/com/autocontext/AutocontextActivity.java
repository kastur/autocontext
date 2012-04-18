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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;

public class AutocontextActivity extends Activity {
	LinearLayout layout;
	LinearLayout flowLayout;

    Internal.AutocontextServiceConnection serviceConn;
    GUI.IdentifierFlow identifierFlow;
    GUI.SubmitView submitView;

    Flow flow;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        final Activity activity = this;

        serviceConn = new Internal.AutocontextServiceConnection(context);
        identifierFlow = new GUI.IdentifierFlow(context);
        
        flow = new Flow();
        flow.add(identifierFlow);
        
        flow.add(new GUI.CalendarEventFilterFlow(context));

        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        flowLayout = new LinearLayout(context);
        flowLayout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(flowLayout);
        
        drawFlow();
        
        LinearLayout spinnerLayout = new LinearLayout(context);
        spinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
        final Spinner spinner = new Spinner(this);
        String[] items = {"Notification", "Launch app"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(spinnerArrayAdapter);
        spinnerLayout.addView(spinner);
        
        Button addFlowButton = new Button(context);
        addFlowButton.setText("Add");
        addFlowButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String selectedItem = spinner.getSelectedItem().toString();
				if (selectedItem.equals("Notification")) {
					flow.add(new GUI.NotifyFlow(context));
					drawFlow();
				} else if (selectedItem.equals("Launch app")) {
					flow.add(new GUI.LaunchPackageFlow(context, activity));
					drawFlow();
				}
			}
		});
        spinnerLayout.addView(addFlowButton);
        
        layout.addView(spinnerLayout);

        submitView = new GUI.SubmitView(context, flow, serviceConn);
        layout.addView(submitView.getView());
        setContentView(layout);
    }
    
    private void drawFlow() {
    	flowLayout.removeAllViews();
    	for (IFlow flowView : flow) {
    		flowLayout.addView(flowView.getView());
    	}
    }
}