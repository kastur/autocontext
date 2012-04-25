package com.autocontext;

import com.autocontext.Autocontext.ActionFlow;
import com.autocontext.Autocontext.ContextActionPair;
import com.autocontext.FlowManager;
import com.autocontext.Autocontext.IAction;
import com.autocontext.Autocontext.IContext;
import com.autocontext.actions.ToastAction;
import com.autocontext.contexts.CalendarEventContext;
import com.autocontext.contexts.ImmediateContext;
import com.autocontext.observers.CalenderEventContextObserver.CalendarEvent;

import android.R.color;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;


public class AutocontextActivity extends Activity {
	LinearLayout layout;
	FlowManager mFlowManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context applicationContext = getApplicationContext();
        
        startService(new Intent(applicationContext, AutocontextService.class));
        Intent intent = new Intent(applicationContext, AutocontextService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				mFlowManager = ((AutocontextService.LocalBinder)binder).getFlowManager();
				onConnect();
			}

			public void onServiceDisconnected(ComponentName name) {
			}
		};

		bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);

        
        layout = new LinearLayout(applicationContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        
        ScrollView rootView = new ScrollView(applicationContext);
        rootView.addView(layout);
        
        setContentView(rootView);
    }
    
    public void onConnect() {
    	Context appContext = getApplicationContext();
    	//ImmediateContext immediateContext = new ImmediateContext(appContext);
    	//mFlowManager.registerContext(immediateContext);
    	
    	
    	CalendarEventContext calendarContext = new CalendarEventContext(appContext);
    	mFlowManager.registerContext(calendarContext);
    	
    	ActionFlow actionFlow = new ActionFlow();
    	actionFlow.add(new ToastAction(appContext));
    	mFlowManager.registerActionFlow(actionFlow);
    	
    	mFlowManager.registerContextAction(calendarContext, actionFlow);
    	draw();
    }
    
    public void draw() {
    	final Context applicationContext = getApplicationContext();
    	
        layout.addView(getHeadingOne(applicationContext, "Configured Contexts"));
        layout.addView(getContextsView(applicationContext));
        layout.addView(getHorizSeparator(applicationContext));
        
        layout.addView(getHeadingOne(applicationContext, "Configured Tasks"));
        layout.addView(getActionFlowsView(applicationContext));
        layout.addView(getHorizSeparator(applicationContext));
        
        layout.addView(getHeadingOne(applicationContext, "Configured Scripts"));
        layout.addView(getContextActionView(applicationContext));
        layout.addView(getHorizSeparator(applicationContext));
        
        Button triggerImmediateButton = new Button(applicationContext);
        triggerImmediateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFlowManager.triggerImmediateContexts();
			}
		});
        triggerImmediateButton.setText("Trigger immediate contexts.");
        layout.addView(triggerImmediateButton);
    }
    
    private View getContextsView(Context activityContext) {
    	LinearLayout layout = new LinearLayout(activityContext);
    	for (IContext context : mFlowManager.getContexts()) {
    		layout.addView(context.getEditView());
    	}
    	return layout;
    }
    
    private View getActionFlowsView(Context activityContext) {
    	LinearLayout layout = new LinearLayout(activityContext);
    	layout.setOrientation(LinearLayout.VERTICAL);
    	
    	for (ActionFlow actionFlow : mFlowManager.getActionFlows()) {
    		for (IAction action : actionFlow) {
    			layout.addView(action.getEditView());
    		}
    	}
    	return layout;
    }
    
    private View getContextActionView(Context activityContext) {
    	LinearLayout layout = new LinearLayout(activityContext);
    	layout.setOrientation(LinearLayout.VERTICAL);
    	
    	for (ContextActionPair pair : mFlowManager.getContextActions()) {
    		LinearLayout pairLayout = new LinearLayout(activityContext);
    		layout.addView(pairLayout);
    		
    		pairLayout.addView(pair.getContext().getDispView());
    		pairLayout.addView(getVerticalSeparator(activityContext));
    		
    		
    		LinearLayout pairActions = new LinearLayout(activityContext);
    		pairLayout.addView(pairActions);
        	
    		for (IAction action : pair.getActions()) {
    			pairActions.addView(action.getDispView());
    			pairActions.addView(getHorizSeparator(activityContext));
    		}
    	}
    	return layout;
    }
    
    private View getHorizSeparator(Context activityContext) {
    	LinearLayout layout = new LinearLayout(activityContext);
    	layout.setBackgroundColor(Color.parseColor("#ff0000"));
    	layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, 1));
    	return layout;
    }
    
    private View getVerticalSeparator(Context activityContext) {
    	LinearLayout layout = new LinearLayout(activityContext);
    	layout.setBackgroundColor(Color.parseColor("#ff0000"));
    	layout.setLayoutParams(new LayoutParams(1, LayoutParams.FILL_PARENT));
    	layout.setPadding(10, 5, 5, 10);
    	return layout;
    }
    
    private View getHeadingOne(Context activityContext, String text) {
    	LinearLayout layout = new LinearLayout(activityContext);
    	layout.setPadding(0, 10, 0, 1);
    	layout.setBackgroundColor(Color.parseColor("#333333"));
    	//layout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
    	
    	TextView textView = new TextView(activityContext);
    	
    	textView.setTextColor(Color.parseColor("#ffffff"));
    	textView.setText(text);
    	layout.addView(textView);
    	return layout;
    }
    
}