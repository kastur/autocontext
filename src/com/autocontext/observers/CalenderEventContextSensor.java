package com.autocontext.observers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.*;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import android.util.Log;
import com.autocontext.*;
import com.autocontext.contexts.CalendarEventContext;

import java.util.*;

public class CalenderEventContextSensor extends ContextSensor {
    private static final String TAG = "CalenderEventContextSensor";

	public static final boolean kAndroidVersionGingerbread = false;
	private Context mContext;
	private HashSet<CalendarEventContext> mRegisteredContexts;
	private	PriorityQueue<CalendarInstanceTrigger> triggerQueue;

	public class CalendarInstanceTrigger extends SensedContext implements Comparable<CalendarInstanceTrigger> {
		public Date time;
		public Integer event_id;
		public String event_title;
		public CalendarEventContext context;

		public int compareTo(CalendarInstanceTrigger e) {
			return this.time.compareTo(e.time);
		}
	}

    @Override
	public ContextSpecKind getKind() {
		return ContextSpecKind.CONTEXT_CALENDAR_EVENT;
	}

	@Override
	public void Initialize(Context appContext) {
        Log.i(TAG, "Initialize...");
		mContext = appContext;
		mRegisteredContexts = new HashSet<CalendarEventContext>();
		triggerQueue = new PriorityQueue<CalendarInstanceTrigger>();
		initializeCalendarEventsContentObserver();	
	}

	@Override
	public void addContextSpec(ContextSpec contextSpec) {
		mRegisteredContexts.add((CalendarEventContext)contextSpec);
        Log.i(TAG, "contextSpec added, refreshing.");
        ResetAndPopulateCalendarQueue();

        /*
        ContentResolver resolver = mContext.getContentResolver();
        final CalendarEventContext calendarEventContext = (CalendarEventContext)context;
        List<CalendarInstanceTrigger> contextInstances = getInstancesThisWeek(resolver, calendarEventContext);

        int numMatches = contextInstances.size();

        if (numMatches > 0) {
            String exampleMatch = contextInstances.get(0).event_title;
            calendarEventContext.setFeedbackText(exampleMatch+ ", and " + (numMatches - 1) + " others.");
        } else {
            calendarEventContext.setFeedbackText("No matches");
        }
        */
	}

    @Override
    public void notifyAboutUpdatedContextSpec(ContextSpec contextSpec) {
        mRegisteredContexts.add((CalendarEventContext)contextSpec);
        Log.i(TAG, "contextSpec updated, refreshing.");
        ResetAndPopulateCalendarQueue();
    }

    @Override
    public void removeContextSpec(ContextSpec contextSpec) {
        mRegisteredContexts.remove(contextSpec);
        Log.i(TAG, "contextSpec removed, refreshing.");
        ResetAndPopulateCalendarQueue();
    }

    public void triggerNextQueuedContext() {
		if (triggerQueue.size() > 0)
			runContexts(triggerQueue.peek().time);
	}
	
	private void initializeCalendarEventsContentObserver() {
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri calendarEventsUri;
		if (kAndroidVersionGingerbread) {
			calendarEventsUri = Uri.parse("content://com.android.calendar/calendars");
		} else {
			calendarEventsUri = CalendarContract.Events.CONTENT_URI;
		}
		contentResolver.registerContentObserver(
				calendarEventsUri,
				true /* notify descendants */,
				mCalendarChangeObserver);
		mContext.registerReceiver(mAlarmReceiver, new IntentFilter("ALARM"));
	}
	
	private Handler mHandler = new Handler();
	
	private ContentObserver mCalendarChangeObserver = new ContentObserver(mHandler) {
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
            Log.i(TAG, "The calendar is updated, refreshing.");
			ResetAndPopulateCalendarQueue();
		}
	};
	
	private void ResetAndPopulateCalendarQueue() {
		ContentResolver contentResolver = mContext.getContentResolver();

		triggerQueue = new PriorityQueue<CalendarInstanceTrigger>();
		for (CalendarEventContext registeredContext : mRegisteredContexts) {
			List<CalendarInstanceTrigger> triggers = 
					getInstancesThisWeek(contentResolver, registeredContext);
			for (CalendarInstanceTrigger trigger : triggers) {
				triggerQueue.add(trigger);
			}
		}


        if (triggerQueue.size() == 0) {
            Log.i(TAG, "EVENT QUEUE IS EMPTY");
        }  else {
            Log.i(TAG, triggerQueue.peek().event_title + " " + triggerQueue.peek().time);
        }
		
		WakeUpNextOn(triggerQueue.peek());
	}
	
	private void WakeUpNextOn(CalendarInstanceTrigger calendarEvent) {
		if (calendarEvent == null)
			return;
		
		PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, new Intent("ALARM"), PendingIntent.FLAG_CANCEL_CURRENT);
        long instanceBeginTime = calendarEvent.time.getTime();

        AlarmManager alarmMan = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);

        Log.i(TAG, "Setting alarm for next event: " +  calendarEvent.time);
        alarmMan.set(AlarmManager.RTC_WAKEUP, instanceBeginTime, sender);
	}
	
	
	private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			runContexts(new Date());
			
			// Set up next alarm.
			WakeUpNextOn(triggerQueue.peek());
		}
	};
	
	private void runContexts(Date now) {
		while (true) {
			// Peek at the first in the queue to see if it's occurred.
			CalendarInstanceTrigger contextEvent = triggerQueue.peek();
			if (contextEvent.time.getTime() > now.getTime())
				break;
			// Remove the event.
			triggerQueue.poll();
			
			// Let the context receiver know about the event.
			Bundle payload = new Bundle();
			payload.putString("toastExtras", contextEvent.kind.toString() + ": " + contextEvent.event_title);

			mManager.triggerContext(contextEvent.context, contextEvent, payload);
		}
	}
	
	public List<CalendarInstanceTrigger> getInstancesThisWeek(ContentResolver resolver, CalendarEventContext searchContext) {
		final String searchTitle = searchContext.getEventFilterText();
		
		Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
		long now = new Date().getTime();
		ContentUris.appendId(builder, now);
		ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
		
		String[] projection = {
				CalendarContract.Instances.EVENT_ID,
				CalendarContract.Instances.TITLE,
				CalendarContract.Instances.BEGIN,
				CalendarContract.Instances.END,
				CalendarContract.Instances.ALL_DAY,
		};
		
		
		final Cursor instanceCursor = resolver.query(
				builder.build(),
				projection,
				CalendarContract.Instances.TITLE + " LIKE ?",
				new String[] { searchTitle },
				null);
		
		LinkedList<CalendarInstanceTrigger> triggers = new LinkedList<CalendarInstanceTrigger>();
		
		while (instanceCursor.moveToNext()) {
			final Integer id = instanceCursor.getInt(0);
			final String title = instanceCursor.getString(1);
			final Date begin_date = new Date(instanceCursor.getLong(2));
			final Date end_date = new Date(instanceCursor.getLong(3));
			final Boolean allDay = !instanceCursor.getString(4).equals("0");
			
			{
				CalendarInstanceTrigger contextEvent = new CalendarInstanceTrigger();
                contextEvent.kind = SensedContextKind.ENTER_EVENT;
                contextEvent.id = "CALENDAR_EVENT" + id;
                contextEvent.time  = begin_date;
                contextEvent.context = searchContext;
                contextEvent.event_id = id;
                contextEvent.event_title = title;
				triggers.add(contextEvent);
			}
			
			{
				CalendarInstanceTrigger contextEvent = new CalendarInstanceTrigger();
                contextEvent.kind = SensedContextKind.EXIT_EVENT;
                contextEvent.id = "CALENDAR_EVENT" + id;
                contextEvent.time  = end_date;
                contextEvent.context = searchContext;
                contextEvent.event_id = id;
                contextEvent.event_title = title;
				triggers.add(contextEvent);
			}
		}
		return triggers;
	}
	
	
}
