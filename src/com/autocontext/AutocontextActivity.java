package com.autocontext;

import java.util.List;

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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AutocontextActivity extends Activity {
	LinearLayout layout;
	TextView packageText;
	private static final int SELECT_PACKAGE_ID = 1;
	private AutocontextService mService;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mService = null;
        startService(new Intent(this, AutocontextService.class));
        bindService(
        		new Intent(this, AutocontextService.class),
        		new ServiceConnection() {
					
					public void onServiceDisconnected(ComponentName name) {
					}
					
					public void onServiceConnected(ComponentName name, IBinder binder) {
						mService = ((AutocontextService.LocalBinder)binder).getService();
					}
				},
				Context.BIND_AUTO_CREATE);
        
        layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        
        TextView filterLabel = new TextView(this);
        filterLabel.setText("Calender event title contains:");
        layout.addView(filterLabel);
        
        final EditText filterText = new EditText(this);
        filterText.setText("a");
        layout.addView(filterText);
        
        
        TextView packageLabel = new TextView(this);
        packageLabel.setText("Launch application:");
        layout.addView(packageLabel);
        
        packageText = new TextView(this);
        packageText.setText("com.android.browser");
        layout.addView(packageText);
        
        {
	        Button b = new Button(this);
	        b.setText("Find app");
	        b.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
					Intent activityIntent = new Intent(view.getContext(), SelectPackageActivity.class);
	                startActivityForResult(activityIntent, SELECT_PACKAGE_ID);
				}
			});
	        layout.addView(b);
        }
        
        {
	        Button b = new Button(this);
	        b.setText("Done");
	        b.setOnClickListener(new OnClickListener() {
				public void onClick(View view) {
	    			PackageManager manager = getPackageManager();
	    			String packageName = (String) packageText.getText();
	    			List<CalendarHelper.CalendarEvent> events = CalendarHelper.getEventsList(getApplicationContext(), 1, filterText.getText());
	    			for (CalendarHelper.CalendarEvent event : events) {
	    				mService.addCalendarAction(event, packageName);
	    			}
	    			
	    		}
			});
	        layout.addView(b);
        }
    }
    
    @Override
    protected void onResume() {
    	super.onResume();
    	
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	if (requestCode == SELECT_PACKAGE_ID) {
    		if (resultCode == RESULT_OK) {
    			packageText.setText(data.getStringExtra("packageName"));
    		}
    	}
    }
}