package com.autocontext;

import java.util.Date;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.TimeZone;
import java.util.UUID;

import com.autocontext.Autocontext.FlowMap;
import com.autocontext.Autocontext.FlowType;
import com.autocontext.Autocontext.IFlow;
import com.autocontext.CalendarHelper.CalendarEvent;
import com.autocontext.GUI.BrightnessFlow;
import com.autocontext.GUI.CalendarEventFilterFlow;
import com.autocontext.GUI.CalendarEventFlow;
import com.autocontext.GUI.IdentifierFlow;
import com.autocontext.GUI.LaunchPackageFlow;
import com.autocontext.GUI.NotifyFlow;
import com.autocontext.GUI.WifiFlow;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

public class AutocontextService extends Service {
    public class LocalBinder extends Binder {
    	AutocontextService getService() {
            // Return this instance of AutocontextService so clients can call public methods
            return AutocontextService.this;
        }
    }
    
	private final IBinder mBinder = new LocalBinder();
	private final OneShotAlarm mAlarm = new OneShotAlarm();
	private final OneShotNotification mNotificationClicked = new OneShotNotification();
	private PriorityQueue<CalendarEvent> calendarQueue;
	private HashMap<CalendarEvent, String> calendarFlows;
	HashMap<String, FlowMap> registeredFlows;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		calendarQueue = new PriorityQueue<CalendarEvent>();
		calendarFlows = new HashMap<CalendarEvent, String>();
		registeredFlows = new HashMap<String, Autocontext.FlowMap>();
		registerReceiver(mAlarm, new IntentFilter("ALARM"));
		registerReceiver(mNotificationClicked, new IntentFilter("NOTIFICATION_CLICKED"));
	}
	
	/* ---------------------------------------------------------------------------- */
	public void addCalendarAction(CalendarEvent event, String flow_id) {
		calendarQueue.add(event);
		calendarFlows.put(event,  flow_id);

        PendingIntent sender = PendingIntent.getBroadcast(this, 0, new Intent("ALARM"), PendingIntent.FLAG_CANCEL_CURRENT);
        long upcomingEventTime = calendarQueue.peek().begin.getTime();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 3);
        upcomingEventTime = calendar.getTimeInMillis();

        // Schedule the alarm!
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, upcomingEventTime, sender);
	}
	
	public String submitFlow(FlowMap flowMap) {
		String flow_id = null;
		
		if (flowMap.containsKey(FlowType.IDENTIFIER)) {
			IdentifierFlow flow = (IdentifierFlow)flowMap.get(FlowType.IDENTIFIER); 
			flow_id = flow.getIdString();
			registeredFlows.put(flow.getIdString(), flowMap);
		}
		
		if (flowMap.containsKey(FlowType.CONTEXT_IMMEDIATE)) {
			performActions(flow_id);
		}
	
		if (flowMap.containsKey(FlowType.CONTEXT_CALENDAR_EVENT_FILTER)) {
			CalendarEventFilterFlow flow =
					(CalendarEventFilterFlow)flowMap.get(FlowType.CONTEXT_CALENDAR_EVENT_FILTER);
			List<CalendarEvent> matching_events = 
					CalendarHelper.getEventsList(getApplicationContext(), 1, flow.getFilterText());
			CalendarEvent next_event = matching_events.iterator().next();
			addCalendarAction(next_event, flow_id);
		}
		
		return "OK";
	}
	
	/* -------------------------------------------------------------------------------- */
	
	// Called if calendar alarm goes off.
	public class OneShotAlarm extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			CalendarEvent head_event = calendarQueue.poll();
			String flow_id = calendarFlows.get(head_event);
			
			System.out.println("HAPPENING: " + head_event.id + " " + head_event.title + " " + head_event.begin.toString());
			
			// Create a new flow for this particular calendar event.
			FlowMap flowList = (FlowMap)registeredFlows.get(flow_id).clone();
			CalendarEventFlow calendarEventFlow = new CalendarEventFlow(getApplicationContext());
			calendarEventFlow.setEvent(head_event);
			flowList.put(FlowType.CONTEXT_CALENDAR_EVENT, calendarEventFlow);
			String new_flow_id = UUID.randomUUID().toString();
			registeredFlows.put(new_flow_id, flowList);
			
			performActions(new_flow_id);
		}
	}
	
	private void performActions(String flow_id) {
		PackageManager packageManager = (PackageManager)getPackageManager();
		FlowMap flowMap = registeredFlows.get(flow_id);
		if (flowMap.containsKey(FlowType.ACTION_NOTIFY)) {
			// Only show notification if not already shown.
			if (!((IdentifierFlow)flowMap.get(FlowType.IDENTIFIER)).actionNotificationShown()) {
				Intent newIntent = new Intent("NOTIFICATION_CLICKED");
				newIntent.putExtra("flow_id", flow_id);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);
				
				
				String notification_ticker;
				String notification_contentTitle;
				String notification_contentText;
				Date notification_date;
				if(flowMap.containsKey(FlowType.CONTEXT_CALENDAR_EVENT)) {
					CalendarEventFlow calendarEventFlow =
							(CalendarEventFlow)flowMap.get(FlowType.CONTEXT_CALENDAR_EVENT);
					CalendarEvent calendarEvent = calendarEventFlow.getEvent();
					notification_ticker = "[AUTO]: " + calendarEvent.title;
					notification_contentTitle = calendarEvent.title;
					notification_contentText = "Autocontext calender event filter match.";
					notification_date = calendarEvent.begin;
				} else {
					notification_ticker = "[AUTO EVENT]";
					notification_contentTitle = "[AUTO EVENT]";
					notification_contentText = "";
					notification_date = new Date(System.currentTimeMillis());
				}
				
				int notification_id = flow_id.hashCode();
				
				NotificationHelper.QueueNotification(
						getApplicationContext(), notification_id, notification_date,
						notification_ticker, notification_contentTitle, notification_contentText,
						pendingIntent);
				
				((IdentifierFlow)flowMap.get(FlowType.IDENTIFIER)).setActionNotificationShown();
				return;
			}
		}
		
		if (flowMap.containsKey(FlowType.ACTION_LAUNCH_PACKAGE)) {
			LaunchPackageFlow flow = (LaunchPackageFlow)flowMap.get(FlowType.ACTION_LAUNCH_PACKAGE);
			Intent launchIntent = packageManager.getLaunchIntentForPackage(flow.getSelectedPackage());
			launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			startActivity(launchIntent);
		}
		
		if (flowMap.containsKey(FlowType.ACTION_BRIGHTNESS_VALUE)) {
			BrightnessFlow flow = (BrightnessFlow)flowMap.get(FlowType.ACTION_BRIGHTNESS_VALUE);
			flow.run();
		}
		
		if (flowMap.containsKey(FlowType.ACTION_WIFI)) {
			WifiFlow flow = (WifiFlow)flowMap.get(FlowType.ACTION_WIFI);
			flow.run();
		}
		
		
	}
	
	public class OneShotNotification extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String flow_id = intent.getStringExtra("flow_id");
			performActions(flow_id);
		}
	}
}
