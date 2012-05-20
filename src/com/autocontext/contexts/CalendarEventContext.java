package com.autocontext.contexts;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autocontext.Autocontext.ContextType;
import com.autocontext.Autocontext.IContext;
import com.autocontext.Autocontext.IContextObserver;

public class CalendarEventContext extends IContext {
	IContextObserver mObserver;
	
	Bundle params;
	LinearLayout editLayout;
	LinearLayout dispLayout;
	TextView feedbackLabel;
	
	public CalendarEventContext(Context appContext) {
		super(appContext);
	}
	
	@Override
	public void onCreate(Bundle savedState) {
		mObserver = null;
		params = savedState;
		
		params.putString("name_filter", "");
		
		editLayout = new LinearLayout(mAppContext);
		
		TextView editLabel = new TextView(mAppContext);
		editLabel.setText("Calendar event filter:");
		editLayout.addView(editLabel);
		
		EditText editText = new EditText(mAppContext);
		editText.setText(params.getString("name_filter"));
		editLayout.addView(editText);
		
		
		dispLayout = new LinearLayout(mAppContext);
	    TextView dispLabel = new TextView(mAppContext);
	    dispLabel.setText("If calendar event matches: ");
	    dispLayout.addView(dispLabel);
		final TextView textView = new TextView(mAppContext);
		textView.setText(params.getString("name_filter"));
		dispLayout.addView(textView);
		
		feedbackLabel = new TextView(mAppContext);
		editLayout.addView(feedbackLabel);
		
		
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				textView.setText(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				params.putString("name_filter", s.toString());
				if (mObserver != null)
					mObserver.onContextUpdated(CalendarEventContext.this);
			}
		});
	}

	@Override
	public ContextType getType() {
		return ContextType.CONTEXT_CALENDAR_EVENT;
	}

	@Override
	public View getEditView() {

		return editLayout;
	}
	
	@Override
	public View getDispView() {
		return dispLayout;
	}
	
	public String getFilterText() {
		return params.getString("name_filter");
	}

	@Override
	public void onAttached(IContextObserver observer) {
		mObserver = observer;
	}
	
	public void setFeedbackText(String feedbackText) {
		feedbackLabel.setText(feedbackText);
	}

}
