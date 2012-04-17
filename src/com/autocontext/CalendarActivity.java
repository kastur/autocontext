package com.autocontext;

import java.util.Date;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.text.format.DateUtils;

public class CalendarActivity extends Activity {
	protected void onCreate(android.os.Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	public void getCalendars() {
		ContentResolver contentResolver = getContentResolver();

		final Cursor cursor = contentResolver.query(
				Uri.parse("content://calendar/calendars"),
				new String[] { "_id", "displayName", "selected" },
				null, null, null);
		while (cursor.moveToNext()) {
			final String _id = cursor.getString(0);
			final String displayName = cursor.getString(1);
			final Boolean selected = !cursor.getString(2).equals("0");
			System.out.println("Id: " + _id + " Display Name: " + displayName + " Selected: " + selected);
		}
		finish();
	}
	
	
	public void getEvents(String id) {
		
		Uri.Builder builder = Uri.parse("content://calendar/instances/when").buildUpon();
		long now = new Date().getTime();
		ContentUris.appendId(builder, now - DateUtils.WEEK_IN_MILLIS);
		ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);
		
		ContentResolver contentResolver = getContentResolver();
		Cursor eventCursor = contentResolver.query(builder.build(),
				new String[] { "title", "begin", "end", "allDay"}, "Calendars._id=" + id,
				null, "startDay ASC, startMinute ASC");

		while (eventCursor.moveToNext()) {
			final String title = eventCursor.getString(0);
			final Date begin = new Date(eventCursor.getLong(1));
			final Date end = new Date(eventCursor.getLong(2));
			final Boolean allDay = !eventCursor.getString(3).equals("0");
			System.out.println("Title: " + title + " Begin: " + begin + " End: " + end + " All Day: " + allDay);
		}
	}
}
