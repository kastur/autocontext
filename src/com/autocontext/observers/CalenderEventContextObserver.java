package com.autocontext.observers;

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.CalendarContract;
import android.text.format.DateUtils;

import com.autocontext.Autocontext.ContextType;
import com.autocontext.Autocontext.IContext;
import com.autocontext.Autocontext.IContextObserver;
import com.autocontext.Autocontext.IContextReceiver;
import com.autocontext.contexts.CalendarEventContext;

public class CalenderEventContextObserver implements IContextObserver {
	public static final boolean kAndroidVersionGingerbread = false;
	
	private IContextReceiver mContextReceiver;
	private Context mContext;
	private HashSet<CalendarEventContext> mRegisteredContexts;
	private	PriorityQueue<CalendarInstanceTrigger> triggerQueue;
	
	public enum EventTriggerCondition {
		ENTER_EVENT,
		EXIT_EVENT
	};

	public class CalendarInstanceTrigger implements Comparable<CalendarInstanceTrigger> {
		public EventTriggerCondition type;
		public Date time;
		public Integer event_id;
		public String event_title;
		public CalendarEventContext context;
		public int compareTo(CalendarInstanceTrigger e) {
			return this.time.compareTo(e.time);
		}
	};
	
	@Override
	public ContextType getType() {
		return ContextType.CONTEXT_CALENDAR_EVENT;
	}

	@Override
	public void init(Context appContext) {
		mContext = appContext;
		mRegisteredContexts = new HashSet<CalendarEventContext>();
		triggerQueue = new PriorityQueue<CalendarInstanceTrigger>();
		initializeCalendarEventsContentObserver();	
	}

	@Override
	public void registerCallback(IContextReceiver contextReceiver) {
		mContextReceiver = contextReceiver;
	}

	@Override
	public void registerContext(IContext context) {
		mRegisteredContexts.add((CalendarEventContext)context);
		context.onAttached(this);
	}
	
	@Override
	public void onContextUpdated(IContext context) {
		ResetAndPopulateCalendarQueue();
		
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
		
		WakeUpNextOn(triggerQueue.peek());
	}
	
	private void WakeUpNextOn(CalendarInstanceTrigger calendarEvent) {
		if (calendarEvent == null)
			return;
		
		PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, new Intent("ALARM"), PendingIntent.FLAG_CANCEL_CURRENT);
        long instanceBeginTime = calendarEvent.time.getTime();

        AlarmManager alarmMan = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
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
			CalendarInstanceTrigger triggerEvent = triggerQueue.peek();
			if (triggerEvent.time.getTime() > now.getTime())
				break;
			// Remove the event.
			triggerQueue.poll();
			
			// Let the context receiver know about the event.
			Bundle payload = new Bundle();
			payload.putString("toastExtras", triggerEvent.type.toString() + ": " + triggerEvent.event_title);
			mContextReceiver.triggerContext(triggerEvent.context, payload);
		}
	}
	
	public List<CalendarInstanceTrigger> getInstancesThisWeek(ContentResolver resolver, CalendarEventContext searchContext) {
		final String searchTitle = searchContext.getFilterText();
		
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
				CalendarInstanceTrigger enterTrigger = new CalendarInstanceTrigger();
				enterTrigger.type = EventTriggerCondition.ENTER_EVENT;
				enterTrigger.time  = begin_date;
				enterTrigger.context = searchContext;
				enterTrigger.event_id = id;
				enterTrigger.event_title = title;
				triggers.add(enterTrigger);
			}
			
			{
				CalendarInstanceTrigger exitTrigger = new CalendarInstanceTrigger();
				exitTrigger.type = EventTriggerCondition.EXIT_EVENT;
				exitTrigger.time  = end_date;
				exitTrigger.context = searchContext;
				exitTrigger.event_id = id;
				exitTrigger.event_title = title;
				triggers.add(exitTrigger);
			}
		}
		return triggers;
	}
	
	
}
