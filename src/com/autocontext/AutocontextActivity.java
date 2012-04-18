package com.autocontext;

import java.util.LinkedList;
import java.util.List;

import com.autocontext.Autocontext.FlowMap;
import com.autocontext.Autocontext.FlowType;
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
import android.text.Layout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;

public class AutocontextActivity extends Activity {
	LinearLayout layout;
	

    Internal.AutocontextServiceConnection serviceConn;
    GUI.IdentifierFlow identifierFlow;
    GUI.SubmitView submitView;

    FlowMap flow;
    TableLayout flowLayout;
    LinkedList<TableRow> elemLayouts = new LinkedList<TableRow>();
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = getApplicationContext();
        final Activity activity = this;

        serviceConn = new Internal.AutocontextServiceConnection(context);
        identifierFlow = new GUI.IdentifierFlow(context);
        
        flow = new FlowMap();
        flow.put(FlowType.IDENTIFIER, identifierFlow);
        
        flow.put(FlowType.CONTEXT_CALENDAR_EVENT_FILTER, new GUI.CalendarEventFilterFlow(context));
        flow.put(FlowType.ACTION_BRIGHTNESS_VALUE, new GUI.BrightnessFlow(context));

        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        flowLayout = new TableLayout(context);
        flowLayout.setOrientation(LinearLayout.VERTICAL);
        layout.addView(flowLayout);
        
        drawFlow(context);
        
        LinearLayout spinnerLayout = new LinearLayout(context);
        spinnerLayout.setOrientation(LinearLayout.HORIZONTAL);
        final Spinner spinner = new Spinner(this);
        String[] items = {"Provide Notification", "Launch App", "Set Display Brightness"};
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, items);
        spinner.setAdapter(spinnerArrayAdapter);
        spinnerLayout.addView(spinner);
        
        Button addFlowButton = new Button(context);
        addFlowButton.setText("Add");
        addFlowButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String selectedItem = spinner.getSelectedItem().toString();
				if (selectedItem.equals("Notification")) {
					flow.put(FlowType.ACTION_NOTIFY, new GUI.NotifyFlow(context));
				} else if (selectedItem.equals("Launch app")) {
					flow.put(FlowType.ACTION_LAUNCH_PACKAGE, new GUI.LaunchPackageFlow(context, activity));
				} else if (selectedItem.equals("Set Display Brightness")) {
					flow.put(FlowType.ACTION_BRIGHTNESS_VALUE, new GUI.BrightnessFlow(context));
				}
				drawFlow(context);
			}
		});
        spinnerLayout.addView(addFlowButton);
        
        layout.addView(spinnerLayout);

        submitView = new GUI.SubmitView(context, flow, serviceConn);
        layout.addView(submitView.getView());
        setContentView(layout);
    }
    
    private void drawFlow(final Context context) {
    	// Clear the TableLayout and each of the rows.
    	for (TableRow elemLayout : elemLayouts) {
    		elemLayout.removeAllViews();
    	}
    	flowLayout.removeAllViews();
    	
    	// Add one row per flow.
    	for (final IFlow flowView : flow.values()) {
    		TableRow elemLayout = new TableRow(context);
    		
    		elemLayout.addView(flowView.getView());
    		elemLayouts.add(elemLayout);
    		
    		Button removeFlowButton = new Button(context);
    		removeFlowButton.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					flow.remove(flowView);
					drawFlow(context);
				}
			});
    		removeFlowButton.setText("X");
    		removeFlowButton.setGravity(Gravity.RIGHT);
    		elemLayout.addView(removeFlowButton);
    		flowLayout.addView(elemLayout);
    	}
    }
}