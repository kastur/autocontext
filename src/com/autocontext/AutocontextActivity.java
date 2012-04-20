package com.autocontext;

import com.autocontext.Autocontext.ActionFlow;
import com.autocontext.Autocontext.ContextActionPair;
import com.autocontext.Autocontext.FlowManager;
import com.autocontext.Autocontext.IAction;
import com.autocontext.Autocontext.IContext;
import com.autocontext.actions.ToastAction;
import com.autocontext.contexts.ImmediateContext;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
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
        
        setContentView(layout);
    }
    
    public void onConnect() {
    	ImmediateContext immediateContext = new ImmediateContext();
    	mFlowManager.registerContext(immediateContext);
    	
    	ActionFlow actionFlow = new ActionFlow();
    	actionFlow.add(new ToastAction());
    	mFlowManager.registerActionFlow(actionFlow);
    	
    	mFlowManager.registerContextAction(immediateContext, actionFlow);
    	draw();
    }
    
    public void draw() {
    	final Context applicationContext = getApplicationContext();
        {
        TextView textView = new TextView(applicationContext);
        textView.setText("Configure contexts");
        layout.addView(textView);
        layout.addView(getContextsView(applicationContext));
        }
        
        {
        TextView textView = new TextView(applicationContext);
        textView.setText("Configure action flows");
        layout.addView(textView);
        layout.addView(getActionFlowsView(applicationContext));
        }
        
        {
        TextView textView = new TextView(applicationContext);
        textView.setText("Configure context <--> action flow mappings");
        layout.addView(textView);
        layout.addView(getContextActionView(applicationContext));
        }
        
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
    		layout.addView(context.getView(activityContext));
    	}
    	return layout;
    }
    
    private View getActionFlowsView(Context activityContext) {
    	LinearLayout layout = new LinearLayout(activityContext);
    	
    	for (ActionFlow actionFlow : mFlowManager.getActionFlows()) {
    		for (IAction action : actionFlow) {
    			layout.addView(action.getView(activityContext));
    		}
    	}
    	return layout;
    }
    
    private View getContextActionView(Context activityContext) {
    	LinearLayout layout = new LinearLayout(activityContext);
    	
    	for (ContextActionPair pair : mFlowManager.getContextActions()) {
    		layout.addView(pair.getContext().getView(activityContext));
    		for (IAction action : pair.getActions()) {
    			layout.addView(action.getView(activityContext));
    		}
    	}
    	return layout;
    }
    
}