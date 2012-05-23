package com.autocontext.contexts;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.autocontext.ContextCond;
import com.autocontext.ContextSensor;
import com.autocontext.ContextSpecKind;

public class CalendarEventContext extends ContextCond {
	ContextSensor mSensor;
	
	Bundle params;
	LinearLayout editLayout;
	TextView feedbackLabel;
	
	public CalendarEventContext(Bundle savedState) {
		super(savedState);
        mSensor = null;
        params = savedState;

	}

	@Override
	public ContextSpecKind getType() {
		return ContextSpecKind.CONTEXT_CALENDAR_EVENT;
	}

	@Override
	public View createView(Context appContext) {
        editLayout = new LinearLayout(appContext);

        TextView editLabel = new TextView(appContext);
        editLabel.setText("Calendar event filter:");
        editLayout.addView(editLabel);

        EditText editText = new EditText(appContext);
        editText.setText(params.getString("name_filter", ""));
        editLayout.addView(editText);

        feedbackLabel = new TextView(appContext);
        editLayout.addView(feedbackLabel);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) {
                params.putString("name_filter", s.toString());
                if (mSensor != null)
                    mSensor.addCond(CalendarEventContext.this);
            }
        });

        return editLayout;
	}

    @Override
    public void destroyView() {

    }

	@Override
	public void onAttached(ContextSensor sensor) {
		mSensor = sensor;
	}
	
	public void setFeedbackText(String feedbackText) {
        if (feedbackLabel != null) {
		    feedbackLabel.setText(feedbackText);
	    }
    }

    public String getFilterText() {
        return params.getString("name_filter", "");
    }
}
