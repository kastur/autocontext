package com.autocontext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.autocontext.Autocontext.Flow;
import com.autocontext.Autocontext.FlowType;
import com.autocontext.Autocontext.IFlow;
import com.autocontext.Internal.AutocontextServiceConnection;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;


public class GUI {
	
	public static class IdentifierFlow implements IFlow {
		Context mContext;
		LinearLayout layout;
		TextView idText;

		IdentifierFlow(Context context) {
			mContext = context;
			createView();
		}

		public View getView() {
			return layout;
		}

		public FlowType getType() {
			return FlowType.IDENTIFIER;
		}
		
		public String getIdString() {
			return idText.getText().toString();
		}
		
		private void createView() {
			layout = new LinearLayout(mContext);
			layout.setOrientation(LinearLayout.VERTICAL);

	        idText = new TextView(mContext);
	        String idString = UUID.randomUUID().toString();
	        idText.setText(idString);
	        layout.addView(idText);
		}
	}
	

	public static class SubmitView implements OnClickListener {
		Context mContext;
		AutocontextServiceConnection mServiceConn;
		Flow mFlow;
		LinearLayout layout;
		TextView resultText;

		SubmitView(Context context, Autocontext.Flow flow, AutocontextServiceConnection service) {
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