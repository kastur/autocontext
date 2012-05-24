package com.autocontext.contexts;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.autocontext.*;
import org.json.JSONObject;

public class CalendarEventContext implements ContextSpec, Saveable {
	ContextSensor mSensor;
	
	String mFilterText = "";

    @Override
     public void loadFromJSON(JSONObject savedJson) {
        try {
            mFilterText = savedJson.getString("filterText");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public JSONObject saveToJSON() {
        return new JSONObject();
    }

    @Override
    public void attachToSensor(ContextSensor sensor) {
        mSensor = sensor;
        mSensor.addContextSpec(this);
        mSensor.notifyAboutUpdatedContextSpec(this);
    }

    @Override
    public void detachFromSensor(ContextSensor sensor) {
        mSensor.removeContextSpec(this);
    }

    @Override
    public EditableModel getEditable(Activity activity) {
        return new ModelView(activity);
    }

    private void setEventFilterText(String filterText) {
        mFilterText = filterText;
        mSensor.notifyAboutUpdatedContextSpec(this);
    }

    public String getEventFilterText() {
        return mFilterText;
    }

    public class ModelView implements EditableModel, TextWatcher {
        Activity activity;

        public ModelView(Activity activity) {
            this.activity = activity;
        }

        @Override
        public View getEditView() {
            String filterText = CalendarEventContext.this.getEventFilterText();
            View view = activity.getLayoutInflater().inflate(R.layout.calendar_spec_view, null);
            EditText editText = (EditText)view.findViewById(R.id.event_title_filter_text);
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
            CalendarEventContext.this.setEventFilterText(editable.toString());
        }
    };

    public ContextSpecKind getType() {
		return ContextSpecKind.CONTEXT_CALENDAR_EVENT;
	}


}
