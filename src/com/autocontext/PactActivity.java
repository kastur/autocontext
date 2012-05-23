package com.autocontext;

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


public class PactActivity extends Activity {
	LinearLayout layout;
	FlowManager mFlowManager;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context applicationContext = getApplicationContext();
        
        startService(new Intent(applicationContext, PactService.class));
        Intent intent = new Intent(applicationContext, PactService.class);
        ServiceConnection serviceConnection = new ServiceConnection() {
			public void onServiceConnected(ComponentName name, IBinder binder) {
				mFlowManager = ((PactService.LocalBinder)binder).getFlowManager();
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
    	draw();
    }
    
    public void draw() {
    	final Context applicationContext = getApplicationContext();

        Flow pair = mFlowManager.getContextActions().get(0);
        View pairView = pair.getView(applicationContext);
        layout.addView(pairView);

        Button triggerImmediateButton = new Button(applicationContext);
        triggerImmediateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFlowManager.triggerImmediateContexts();
			}
		});
        triggerImmediateButton.setText("Trigger immediate contexts.");
        layout.addView(triggerImmediateButton);

    
        Button triggerNextCalendarButton = new Button(applicationContext);
        triggerNextCalendarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mFlowManager.triggerNextCalendarContext();
			}
		});
        triggerNextCalendarButton.setText("Trigger next calendar context.");
        layout.addView(triggerNextCalendarButton);
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