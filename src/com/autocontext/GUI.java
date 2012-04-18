package com.autocontext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.autocontext.Autocontext.FlowMap;
import com.autocontext.Autocontext.FlowType;
import com.autocontext.Autocontext.IFlow;
import com.autocontext.CalendarHelper.CalendarEvent;
import com.autocontext.Internal.AutocontextServiceConnection;
import com.geekyouup.android.autobright.AutoBright;

import android.app.Activity;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;


public class GUI {
	
	public static class IdentifierFlow implements IFlow {
		Context mContext;
		LinearLayout layout;
		
		String mFlowIdString;
		
		Boolean mNotificationShown;

		IdentifierFlow(Context context) {
			mContext = context;
			mFlowIdString = UUID.randomUUID().toString();
			mNotificationShown = false;
		}

		public View getView() {
			createView();
			return layout;
		}

		public FlowType getType() {
			return FlowType.IDENTIFIER;
		}
		
		public String getIdString() {
			return mFlowIdString;
		}
		
		public boolean actionNotificationShown() {
			return mNotificationShown;
		}
		
		public void setActionNotificationShown() {
			mNotificationShown = true;
		}
		
		private void createView() {
			layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.VERTICAL);

			{
		        TextView t = new TextView(mContext);
		        t.setText(mFlowIdString);
		        layout.addView(t);
			}
			
			{
				TextView t = new TextView(mContext);
		        t.setText(mNotificationShown.toString());
		        layout.addView(t);
			}
		}
	}
	

	public static class SubmitView implements OnClickListener {
		Context mContext;
		AutocontextServiceConnection mServiceConn;
		FlowMap mFlow;
		LinearLayout layout;
		TextView resultText;

		SubmitView(Context context, Autocontext.FlowMap flow, AutocontextServiceConnection service) {
			mContext = context;
			mFlow = flow;
			mServiceConn = service;
			createView();
		}

		public View getView() {
			return layout;
		}

		public void onClick(View view) {
			String result = mServiceConn.getService().submitFlow(mFlow);
			resultText.setText(result);
		}

		private void createView() {
			layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.VERTICAL);

			Button submitButton = new Button(mContext);
	        submitButton.setText("Done");
	        submitButton.setOnClickListener(this);
			layout.addView(submitButton);

	        resultText = new TextView(mContext);
	        resultText.setText("");
	        layout.addView(resultText);
		}
	}
	
	public static class CalendarEventFlow implements IFlow {
		Context mContext;
		LinearLayout layout;
		CalendarEvent mCalendarEvent;
		
		public CalendarEventFlow(Context context) {
			mContext = context;
			createView();
		}
		
		public View getView() {
			return layout;
		}
		
		public FlowType getType() {
			return FlowType.CONTEXT_CALENDAR_EVENT;
		}
		
		public void setEvent(CalendarEvent calendarEvent) {
			mCalendarEvent = calendarEvent;
		}
		
		public CalendarEvent getEvent() {
			return mCalendarEvent;
		}
		
		private void createView() {
			layout = new LinearLayout(mContext);
			TextView textView = new TextView(mContext);
			textView.setText("INTERNAL CALENDAR EVENT");
			layout.addView(textView);
		}
		
	}

	public static class CalendarEventFilterFlow implements IFlow {
		private Context mContext;
		private LinearLayout layout;
		private EditText filterText;

		public CalendarEventFilterFlow(Context context) {
			mContext = context;
			createView();
		}

		public View getView() {
			return layout;
		}
		
		public FlowType getType() {
			return FlowType.CONTEXT_CALENDAR_EVENT_FILTER;
		}
		
		public String getFilterText() {
			return filterText.getText().toString();
		}

		private void createView() {
			layout = new LinearLayout(mContext);
	        layout.setOrientation(LinearLayout.VERTICAL);
	        
	        TextView filterLabel = new TextView(mContext);
	        filterLabel.setText("Calender event title contains:");
	        layout.addView(filterLabel);
	        
	        filterText = new EditText(mContext);
	        filterText.setText("Artificial");
	        layout.addView(filterText);
		}
	}
	
	public static class NotifyFlow implements IFlow {
		Context mContext;
		LinearLayout layout;
		
		public NotifyFlow(Context context) {
			mContext = context;
			createView();
		}
		
		public View getView() {
			return layout;
		}
		
		public FlowType getType() {
			return FlowType.ACTION_NOTIFY;
		}
		
		private void createView() {
			layout = new LinearLayout(mContext);
			TextView textView = new TextView(mContext);
			textView.setText("Notify ON");
			layout.addView(textView);
		}
	}
	
	public static class BrightnessFlow implements IFlow {
		Context mContext;
		LinearLayout layout;
		SeekBar brightnessBar;
		
		public BrightnessFlow(Context context) {
			mContext = context;
			createView();
		}
		
		public View getView() {
			return layout;
		}
		
		public FlowType getType() {
			return FlowType.ACTION_BRIGHTNESS_VALUE;
		}
		
		private void createView() {
			layout = new LinearLayout(mContext);
			
			TextView textView = new TextView(mContext);
			textView.setText("Brightness: ");
			layout.addView(textView);
			
			brightnessBar = new SeekBar(mContext);
			brightnessBar.setProgress(128);
			brightnessBar.setMinimumWidth(100);
			brightnessBar.setMax(255);
			LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			brightnessBar.setLayoutParams(layoutParams);
			
			layout.addView(brightnessBar);
		}
		
		public void run() {
			int brightnessValue = brightnessBar.getProgress();
			Settings.System.putInt(
					mContext.getContentResolver(), 
					Settings.System.SCREEN_BRIGHTNESS, brightnessValue);
			System.out.println("Changing brightness to " + brightnessValue);
			
			Intent defineIntent2 = new Intent(mContext, AutoBright.class);
			mContext.startActivity(defineIntent2, Intent.FLAG_ACTIVITY_NEW_TASK);
				
		}
	}

	public static class LaunchPackageFlow implements IFlow {
		Context mContext;
		Activity mActivity;
		LinearLayout layout;
		Spinner packageSpinner;

		public LaunchPackageFlow(Context context, Activity activity) {
			mContext = context;
			mActivity = activity;
			createView();
		}

		public View getView() {
			return layout;
		}
		
		public FlowType getType() {
			return FlowType.ACTION_LAUNCH_PACKAGE;
		}

    	public String getSelectedPackage() {
    		String packageName = packageSpinner.getSelectedItem().toString();
    		return packageName;
    	}

		private void createView() {
			layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.VERTICAL);
	        
			PackageManager pkgMan = mContext.getPackageManager();
			List<ApplicationInfo> pkgList = pkgMan.getInstalledApplications(0);

			ArrayList<String> packages = new ArrayList<String>();
			packages.add("com.android.browser");
			for (ApplicationInfo pkg : pkgList) {
				packages.add(pkg.processName);
			}
			
			ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(mActivity, android.R.layout.simple_spinner_dropdown_item, packages);
			packageSpinner = new Spinner(mActivity);
			packageSpinner.setAdapter(spinnerArrayAdapter);
			layout.addView(packageSpinner);
    	}
    }
}