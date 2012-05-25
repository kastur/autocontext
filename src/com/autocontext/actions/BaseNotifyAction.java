package com.autocontext.actions;

import android.*;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.autocontext.*;
import com.autocontext.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public abstract class BaseNotifyAction implements Reaction, Saveable {
    HashMap<String, SensedContext> suppressContexts = new HashMap<String, SensedContext>();

    protected String mNotificationTitle = "NOTIFY";
    protected String mEditLabel = "This is the edit label.";

    public BaseNotifyAction(String notificationTitle, String editLabel) {
        mNotificationTitle = notificationTitle;
        mEditLabel = editLabel;
    }

    @Override
    public ReactionKind getType() {
        return ReactionKind.UNDEFINED;
    }

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
            createNotification(appContext, android.R.drawable.ic_lock_lock, mNotificationTitle, "In " + suppressContexts.size() + " sensitive contexts.");
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
            View view = activity.getLayoutInflater().inflate(R.layout.just_a_label, null);
            TextView textView = (TextView)view.findViewById(R.id.text_label);
            textView.setText(mEditLabel);
            return view;
        }
    }
}
