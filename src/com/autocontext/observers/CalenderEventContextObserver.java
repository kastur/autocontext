package com.autocontext.observers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import android.text.format.DateUtils;
import android.widget.Toast;

import com.autocontext.Autocontext.ContextType;
import com.autocontext.Autocontext.IContext;
import com.autocontext.Autocontext.IContextObserver;
import com.autocontext.Autocontext.IContextReceiver;
import com.autocontext.contexts.CalendarEventContext;

public class CalenderEventContextObserver implements IContextObserver {
	IContextReceiver mContextReceiver;
	Context mContext;
	
	HashSet<CalendarEventContext> mRegisteredContexts;
	
	PriorityQueue<CalendarEvent> calendarQueue;
	
	@Override
	public ContextType getType() {
		return ContextType.CONTEXT_CALENDAR_EVENT;
	}

	@Override
	public void init(Context appContext) {
		mContext = appContext;
		mRegisteredContexts = new HashSet<CalendarEventContext>();
		calendarQueue = new PriorityQueue<CalenderEventContextObserver.CalendarEvent>();
		
		ContentResolver contentResolver = mContext.getContentResolver();
		Uri calendarUri = Uri.parse("content://com.android.calendar/calendars");
		contentResolver.registerContentObserver(
				calendarUri, true, mCalendarChangeObserver);
		
		mContext.registerReceiver(mAlarmReceiver, new IntentFilter("ALARM"));
		
		init_events();
		
		
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
	
	public void triggerContext() {
		Bundle payload = new Bundle();
		payload.putLong(ContextType.CONTEXT_IMMEDIATE.name() + "_timestamp", System.currentTimeMillis());
		for (IContext context : mRegisteredContexts) {
			mContextReceiver.triggerContext(context, payload);
		}
	}
	
	Handler mHandler = new Handler();
	
	ContentObserver mCalendarChangeObserver = new ContentObserver(mHandler) {
		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			init_events();
		}
	};
	
	private void init_events() {
		ContentResolver contentResolver = mContext.getContentResolver();
		HashMap<String, String> calendarsMap = getCalendars(mContext);
		
		
		List<CalendarEvent> calendarEvents = getMockEventsList(mContext);
		for (CalendarEvent e : calendarEvents) {
			calendarQueue.add(e);
		}
		
		set_alarm(calendarQueue.peek());
	}
	
	private void set_alarm(CalendarEvent calendarEvent) {
		PendingIntent sender = PendingIntent.getBroadcast(mContext, 0, new Intent("ALARM"), PendingIntent.FLAG_CANCEL_CURRENT);
        long upcomingEventTime = calendarQueue.peek().begin.getTime();

        // Schedule the alarm!
        AlarmManager alarmMan = (AlarmManager)mContext.getSystemService(Context.ALARM_SERVICE);
        alarmMan.set(AlarmManager.RTC_WAKEUP, upcomingEventTime, sender);
	}
	
	
	private BroadcastReceiver mAlarmReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {			
			CalendarEvent contextEvent = calendarQueue.poll();
			for (CalendarEventContext calendarContext : mRegisteredContexts) {
				final String filterText = calendarContext.getFilterText();
				if (filterText == null)
					continue;
				
				if (contextEvent.title.matches(".*"+filterText+".*")) {
					Bundle payload = new Bundle();
					payload.putString("calendar_event_title", contextEvent.title);
					mContextReceiver.triggerContext(calendarContext, payload);
				}
			}
			
			set_alarm(calendarQueue.peek());
		}
	};
	
	public static HashMap<String, String> getCalendars(Context context) {
		ContentResolver contentResolver = context.getContentResolver();

		final Cursor cursor = contentResolver.query(
				Uri.parse("content://com.android.calendar/calendars"),
				new String[] { "_id", "displayName", "selected" },
				null, null, null);
		HashMap<String, String> calendarsMap = new HashMap<String, String>();
		while (cursor.moveToNext()) {
			final String _id = cursor.getString(0);
			final String _displayName = cursor.getString(1);
			final Boolean _selected = !cursor.getString(2).equals("0");
			calendarsMap.put(_id, _displayName);
		}
		return calendarsMap;
	}
	
	public static class CalendarEvent implements Comparable<CalendarEvent> {
		public Integer id;
		public String title;
		public Date begin;
		public Date end;
		public Boolean allDay;
		public int compareTo(CalendarEvent e) {
			return this.begin.compareTo(e.begin);
		}
	}
	
	public static List<CalendarEvent> getEventsList(Context context) {
		Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
		long now = new Date().getTime();
		ContentUris.appendId(builder, now);
		ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
	
		ContentResolver contentResolver = context.getContentResolver();
		Cursor eventCursor = contentResolver.query(
				builder.build(),
				new String[] { "event_id", "title", "begin", "end", "allDay"},
				"" /* WHERE Clause */,
				null,
				"startDay ASC, startMinute ASC");
	
		ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent> ();
		while (eventCursor.moveToNext()) {
			final Integer id = eventCursor.getInt(0);
			final String title = eventCursor.getString(1);
			final Date begin = new Date(eventCursor.getLong(2));
			final Date end = new Date(eventCursor.getLong(3));
			final Boolean allDay = !eventCursor.getString(4).equals("0");
			CalendarEvent event = new CalendarEvent();
			event.id = id;
			event.title = title;
			event.begin = begin;
			event.end = end;
			event.allDay = allDay;
			events.add(event);
		}
		return events;
	}
	
	public static List<CalendarEvent> getMockEventsList(Context context) {
		ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent> ();

		Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
       
		for (int ii = 0; ii < 100; ++ii) {
			CalendarEvent testEvent = new CalendarEvent();
			testEvent.title = "TEST";
	        calendar.add(Calendar.SECOND, 5);
	        testEvent.begin = calendar.getTime();
	        calendar.add(Calendar.SECOND, 5);
			testEvent.end = calendar.getTime();
			testEvent.allDay = false;
			events.add(testEvent);
		}
		
		return events;
	}
	
	public static List<CalendarEvent> getEventsList(Context context, int calendarId, String filterText) {
		Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
		long now = new Date().getTime();
		ContentUris.appendId(builder, now);
		ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
	
		ContentResolver contentResolver = context.getContentResolver();
		Cursor eventCursor = contentResolver.query(
				builder.build(),
				new String[] { "event_id", "title", "begin", "end", "allDay"},
				"Calendars._id=" + calendarId,
				null,
				"startDay ASC, startMinute ASC");
	
		ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent> ();
		
		// Add test event always;

		
		while (eventCursor.moveToNext()) {
			final Integer id = eventCursor.getInt(0);
			final String title = eventCursor.getString(1);
			final Date begin = new Date(eventCursor.getLong(2));
			final Date end = new Date(eventCursor.getLong(3));
			final Boolean allDay = !eventCursor.getString(4).equals("0");
			if (title.contains(filterText)) {
				CalendarEvent event = new CalendarEvent();
				event.id = id;
				event.title = title;
				event.begin = begin;
				event.end = end;
				event.allDay = allDay;
				events.add(event);
			}
		}
		return events;
	}

	@Override
	public void onContextUpdated(IContext context) {
		Toast.makeText(mContext, "Updated context", Toast.LENGTH_SHORT).show();
	}
}
