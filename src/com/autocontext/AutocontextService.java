package com.autocontext;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;

import com.autocontext.Autocontext.Flow;
import com.autocontext.Autocontext.FlowType;
import com.autocontext.Autocontext.IFlow;
import com.autocontext.CalendarHelper.CalendarEvent;
import com.autocontext.GUI.CalendarEventFilterFlow;
import com.autocontext.GUI.IdentifierFlow;
import com.autocontext.GUI.LaunchPackageFlow;

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
	HashMap<String, Flow> registeredFlows;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		calendarQueue = new PriorityQueue<CalendarEvent>();
		calendarFlows = new HashMap<CalendarEvent, String>();
		registeredFlows = new HashMap<String, Autocontext.Flow>();
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
	
	public String submitFlow(Flow flowList) {
		String flow_id = null;
		for (IFlow iflow : flowList) {
			if (iflow.getType() == FlowType.IDENTIFIER) {
				IdentifierFlow flow = (IdentifierFlow)iflow; 
				flow_id = flow.getIdString();
				registeredFlows.put(flow.getIdString(), flowList);
			}
		}
		
		for (IFlow iflow : flowList) {
			if (iflow.getType() == FlowType.CONTEXT_CALENDAR_EVENT_FILTER) {
				CalendarEventFilterFlow flow = (CalendarEventFilterFlow)iflow;
				List<CalendarEvent> matching_events = 
						CalendarHelper.getEventsList(getApplicationContext(), 1, flow.getFilterText());
				CalendarEvent next_event = matching_events.iterator().next();
				addCalendarAction(next_event, flow_id);
			}
		}
		return "OK";
	}
	
	/* -------------------------------------------------------------------------------- */
	
	// Called if calendar alarm goes off.
	public class OneShotAlarm extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			CalendarEvent head_event = calendarQueue.poll();
			PackageManager packageManager = (PackageManager)getPackageManager();
			String flow_id = calendarFlows.get(head_event);
			
			Flow flowList = registeredFlows.get(flow_id);
			
			for (IFlow iflow : flowList) {
				if (iflow.getType() == FlowType.ACTION_NOTIFY) {
					Intent newIntent = new Intent("NOTIFICATION_CLICKED");
					newIntent.putExtra("flow_id", flow_id);
					PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, newIntent, PendingIntent.FLAG_CANCEL_CURRENT);
					
					NotificationHelper.QueueNotification(getApplicationContext(), head_event.id, head_event.begin, head_event.title, head_event.title, "", pendingIntent);
					break;
				}
				if (iflow.getType() == FlowType.ACTION_LAUNCH_PACKAGE) {
					LaunchPackageFlow flow = (LaunchPackageFlow)iflow;
					Intent launchIntent = packageManager.getLaunchIntentForPackage(flow.getSelectedPackage());
					launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
					startActivity(launchIntent);
				}
			}
			
			System.out.println("HAPPENING: " + head_event.id + " " + head_event.title + " " + head_event.begin.toString());
		}
	}
	
	public class OneShotNotification extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			String flow_id = intent.getStringExtra("flow_id");
			Flow flowList = registeredFlows.get(flow_id);
			
			for (IFlow iflow : flowList) {
				if (iflow.getType() == FlowType.ACTION_LAUNCH_PACKAGE) {
					LaunchPackageFlow flow = (LaunchPackageFlow)iflow;
					PackageManager packageManager = (PackageManager)getPackageManager();
					Intent launchIntent = packageManager.getLaunchIntentForPackage(flow.getSelectedPackage());
					launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
					startActivity(launchIntent);
				}
			}
		}
		
	}
}
