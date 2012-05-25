package com.autocontext.contexts;

import android.app.ActionBar;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.autocontext.*;
import com.autocontext.helpers.CalendarEventsDialog;
import com.autocontext.helpers.CalendarHelper;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CalendarEventContext implements ContextSpec, Saveable {
	ContextSensor mSensor;
	
	String mFilterText = "";

    @Override
     public void loadFromJSON(JSONObject savedJson) throws JSONException {
        try {
        mFilterText = savedJson.getString("filterText");
        } catch (Exception e) {
            mFilterText = "";
        }
    }

    @Override
    public JSONObject saveToJSON() throws JSONException {
        JSONObject contextJson =  new JSONObject();
        contextJson.put("filterText", mFilterText);
        return contextJson;
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

    public class ModelView implements EditableModel, TextWatcher, View.OnFocusChangeListener {
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
            editText.setOnFocusChangeListener(this);
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

        ActionMode mActionMode = null;
        @Override
        public void onFocusChange(View view, boolean enterFocus) {
            if (enterFocus == true) {
                mActionMode = activity.startActionMode(mCalendarEventFilterFocusCallback);
            } else {
                mActionMode.finish();
                mActionMode = null;
            }
        }

        ActionMode.Callback mCalendarEventFilterFocusCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.calendar_event_filter_actbar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                doTestSearchAndShowDialog();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
            }
        };

        private void doTestSearchAndShowDialog() {
            ContentResolver contentResolver = activity.getContentResolver();
            ArrayList<String> calendar_events = CalendarHelper.searchCalendarEventTitlesInTheNextWeekFor(contentResolver, mFilterText);
            CalendarEventsDialog dialog = new CalendarEventsDialog(activity);
            dialog.setEvents(calendar_events);
            dialog.showDialog();
        }
    };

    public ContextSpecKind getType() {
		return ContextSpecKind.CONTEXT_CALENDAR_EVENT;
	}


}
