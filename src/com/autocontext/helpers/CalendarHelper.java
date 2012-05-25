package com.autocontext.helpers;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.text.format.DateUtils;
import com.autocontext.contexts.CalendarEventContext;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: k
 * Date: 5/25/12
 * Time: 3:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class CalendarHelper {
    public static ArrayList<String> searchCalendarEventTitlesInTheNextWeekFor(ContentResolver resolver, String searchString) {

        Uri.Builder builder = CalendarContract.Instances.CONTENT_URI.buildUpon();
        long now = new Date().getTime();
        ContentUris.appendId(builder, now);
        ContentUris.appendId(builder, now + DateUtils.WEEK_IN_MILLIS);

        String[] projection = {
                CalendarContract.Instances.TITLE,
        };

        final Cursor instanceCursor = resolver.query(
                builder.build(),
                projection,
                CalendarContract.Instances.TITLE + " LIKE ?",
                new String[] { searchString },
                null);

        ArrayList<String> events = new ArrayList<String>();
        while(instanceCursor.moveToNext()) {
            final String event_title = instanceCursor.getString(0);
            events.add(event_title);
        }
        return events;
    }
}
