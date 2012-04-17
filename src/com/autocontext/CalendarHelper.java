package com.autocontext;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.text.format.DateUtils;
import android.util.Pair;

public class CalendarHelper {
	public static void observeCalendar(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
		Handler handler = new Handler();
		ContentObserver observer = new ContentObserver(handler) {
			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
			}
		};
		
		contentResolver.registerContentObserver(Uri.parse("content://com.android.calendar/calendars"), true, observer);
	}
	
	
	public static void getCalendars(Context context) {
		ContentResolver contentResolver = context.getContentResolver();
	
		final Cursor cursor = contentResolver.query(
				Uri.parse("content://com.android.calendar/calendars"),
				new String[] { "_id", "displayName", "selected" },
				null, null, null);
		while (cursor.moveToNext()) {
			final String _id = cursor.getString(0);
			final String displayName = cursor.getString(1);
			final Boolean selected = !cursor.getString(2).equals("0");
			System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
		}
	}
	
	public static void getEvents(Context context, int calendarId) {
		Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
		long now = new Date().getTime();
		ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
		ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
		
		ContentResolver contentResolver = context.getContentResolver();
		Cursor eventCursor = contentResolver.query(builder.build(),
				new String[] { "title", "begin", "end", "allDay"}, "Calendars._id=" + calendarId,
				null, "startDay ASC, startMinute ASC");

		while (eventCursor.moveToNext()) {
			final String title = eventCursor.getString(0);
			final Date begin = new Date(eventCursor.getLong(1));
			final Date end = new Date(eventCursor.getLong(2));
			final Boolean allDay = !eventCursor.getString(3).equals("0");
			System.out.println("Title: " + title + " Begin: " + begin + " End: " + end + " All Day: " + allDay);
		}
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
	
	public static List<CalendarEvent> getEventsList(Context context, int calendarId, CharSequence filterText) {
		ArrayList<CalendarEvent> events = new ArrayList<CalendarEvent>();
		Uri.Builder builder = Uri.parse("content://com.android.calendar/instances/when").buildUpon();
		long now = new Date().getTime();
		ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
		ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
		
		ContentResolver contentResolver = context.getContentResolver();
		Cursor eventCursor = contentResolver.query(builder.build(),
				new String[] { "event_id", "title", "begin", "end", "allDay"}, "Calendars._id=" + calendarId,
				null, "startDay ASC, startMinute ASC");

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
}
