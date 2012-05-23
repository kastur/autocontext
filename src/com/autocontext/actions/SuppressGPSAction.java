package com.autocontext.actions;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.autocontext.R;
import com.autocontext.Reaction;
import com.autocontext.SensedContext;
import com.autocontext.SensedContextKind;

import java.util.HashMap;

public class SuppressGPSAction extends Reaction {
    Bundle params;
    LinearLayout editLayout;
    HashMap<String, SensedContext> suppressContexts;

    public SuppressGPSAction(Bundle savedBundle) {
        super(savedBundle);
    }

    @Override
    public void run(Context appContext, SensedContext event, Bundle payload) {
        if (event.kind == SensedContextKind.ENTER_EVENT) {
            suppressContexts.put(event.id, event);
        } else if (event.kind == SensedContextKind.EXIT_EVENT) {
            suppressContexts.remove(event.id);
        }

        if (suppressContexts.size() > 0) {
            createNotification(appContext, R.drawable.ic_launcher, "Suppressing GPS", "");
        } else {
            destroyNotification(appContext);
        }
    }

    public void createNotification(Context appContext, int imageResourceId, String titleText, String statusText)
    {
        NotificationManager notificationManager = (NotificationManager)appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(imageResourceId, titleText, System.currentTimeMillis());

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(appContext, titleText, statusText, null);

        notificationManager.notify(1, notification);
    }

    public void destroyNotification(Context appContext) {
        NotificationManager notificationManager = (NotificationManager)appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    @Override
    public void onCreate(Bundle savedState) {
        suppressContexts = new HashMap<String, SensedContext>();
        params = savedState;
    }

    @Override
    public View getView(Context appContext) {
        editLayout = new LinearLayout(appContext);
        TextView textView = new TextView(appContext);
        textView.setText("Suppress GPS");
        editLayout.addView(textView);
        return editLayout;
    }

    @Override
    public void destroyView() {

    }
}
