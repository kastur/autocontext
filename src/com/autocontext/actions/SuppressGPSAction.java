package com.autocontext.actions;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.autocontext.*;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class SuppressGPSAction implements Reaction, Saveable {
    HashMap<String, SensedContext> suppressContexts = new HashMap<String, SensedContext>();

    @Override
    public EditableModel getEditable(Activity activity) {
        return new ModelView(activity);
    }

    @Override
    public void loadFromJSON(JSONObject json) throws JSONException {

    }

    @Override
    public JSONObject saveToJSON() throws JSONException {
        return new JSONObject();
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

    public void createNotification(Context appContext, int imageResourceId, String titleText, String statusText) {
        NotificationManager notificationManager =
                (NotificationManager)appContext.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new Notification(imageResourceId, titleText, System.currentTimeMillis());

        notification.flags |= Notification.FLAG_ONGOING_EVENT;
        notification.setLatestEventInfo(appContext, titleText, statusText, null);

        notificationManager.notify(1, notification);
    }

    public void destroyNotification(Context appContext) {
        NotificationManager notificationManager =
                (NotificationManager)appContext.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private class ModelView implements EditableModel {
        Activity activity;
        ModelView(Activity activity) {
            this.activity = activity;
        }
        @Override
        public View getEditView() {
            View view = activity.getLayoutInflater().inflate(R.layout.action_suppress_gps, null);
            return view;
        }
    }
}
