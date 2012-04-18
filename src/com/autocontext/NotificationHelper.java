package com.autocontext;

import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class NotificationHelper {
	
	public static void QueueNotification(Context context, Integer notification_id, Date begin, String ticker, String contentTitle, String contentText, PendingIntent intent) {
		NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		int icon = R.drawable.ic_launcher;
		long when = begin.getTime();
		Notification notification = new Notification(icon, ticker, when);

		notification.setLatestEventInfo(context, contentTitle, contentText, intent);
		
		mNotificationManager.notify(notification_id, notification);
	}
}
