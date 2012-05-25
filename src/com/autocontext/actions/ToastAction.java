package com.autocontext.actions;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.autocontext.*;
import org.json.JSONException;
import org.json.JSONObject;

public class ToastAction implements Reaction, Saveable {
    private String mToastText;

    @Override
    public ReactionKind getType() {
        return ReactionKind.REACTION_TOAST;
    }

    @Override
    public EditableModel getEditable(Activity activity) {
        return new ModelView(activity);
    }

    @Override
    public void loadFromJSON(JSONObject savedJson) throws JSONException {
        try {
            mToastText = savedJson.getString("toastText");
        } catch (Exception e) {
            mToastText = "";
        }
    }

    @Override
    public JSONObject saveToJSON() throws JSONException {
        return new JSONObject();
    }

    @Override
    public void run(Context appContext, SensedContext event, Bundle payload) {
        String extras = payload.getString("toastExtras", "No extra details.");
        Toast.makeText(appContext, mToastText + "::" + extras, Toast.LENGTH_LONG).show();
    }

    public String getToastText() {
        return mToastText;
    }

    public void setToastText(String toastText) {
        mToastText = toastText;
    }

    public class ModelView implements EditableModel, TextWatcher {
        Activity activity;

        public ModelView(Activity activity) {
            this.activity = activity;
        }

        @Override
        public View getEditView() {
            String filterText = ToastAction.this.getToastText();
            View view = activity.getLayoutInflater().inflate(R.layout.toast_action_layout, null);
            EditText editText = (EditText)view.findViewById(R.id.toast_text);
            editText.setText(filterText);
            editText.addTextChangedListener(this);
            return view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {
            ToastAction.this.setToastText(editable.toString());
        }
    };
}
