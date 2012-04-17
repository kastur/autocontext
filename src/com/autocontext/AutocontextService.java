package com.autocontext;

import java.util.Calendar;
import java.util.HashMap;
import java.util.PriorityQueue;

import com.autocontext.CalendarHelper.CalendarEvent;

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
	private PriorityQueue<CalendarEvent> calendar_events;
	private HashMap<CalendarEvent, String> calendar_actions;
	
	@Override
	public IBinder onBind(Intent arg0) {
		return mBinder;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		calendar_events = new PriorityQueue<CalendarEvent>();
		calendar_actions = new HashMap<CalendarEvent, String>();
		registerReceiver(mAlarm, new IntentFilter("ALARM"));
	}
	
	/* ---------------------------------------------------------------------------- */
	public void addCalendarAction(CalendarEvent event, String packageName) {
		calendar_events.add(event);
		calendar_actions.put(event,  packageName);

        PendingIntent sender = PendingIntent.getBroadcast(this, 0, new Intent("ALARM"), PendingIntent.FLAG_CANCEL_CURRENT);
        long upcomingEventTime = calendar_events.peek().begin.getTime();
        
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.SECOND, 5);
        upcomingEventTime = calendar.getTimeInMillis();

        // Schedule the alarm!
        AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, upcomingEventTime, sender);
	}
	
	/* -------------------------------------------------------------------------------- */
	public static final int MSG_ADD_CALENDAR_ACTION = 1;
	public Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
			case MSG_ADD_CALENDAR_ACTION:
				handleCalendarEvent(msg.getData());
				break;
			default:
				super.handleMessage(msg);
			}
		}
	};
	
	
	public class OneShotAlarm extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			CalendarEvent head_event = calendar_events.poll();
			
			PackageManager packageManager = (PackageManager)getPackageManager();
			String packageName = calendar_actions.get(head_event);
			Intent launchIntent = packageManager.getLaunchIntentForPackage(packageName);
			launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			System.out.println("HAPPENING: " + head_event.id + " " + head_event.title + " " + head_event.begin.toString());
			NotificationHelper.QueueNotification(getApplicationContext(), head_event.id, head_event.begin, head_event.title, head_event.title, "", launchIntent);
		}
	}
}
