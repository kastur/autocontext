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

public class ImmediateContext extends IContext {
	Bundle params;
	LinearLayout editLayout;
	LinearLayout dispLayout;
	public ImmediateContext(Context appContext) {
		super(appContext);
	}
	
	@Override
	public void onCreate(Bundle savedState) {
		params = savedState;
		editLayout = new LinearLayout(mAppContext);
		EditText editText = new EditText(mAppContext);
		editText.setText(params.getString("name"));
		editLayout.addView(editText);
		
		
		dispLayout = new LinearLayout(mAppContext);
		final TextView textView = new TextView(mAppContext);
		textView.setText(params.getString("name"));
		dispLayout.addView(textView);
		
		
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				textView.setText(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				params.putString("name", s.toString());
			}
		});
	}

	@Override
	public ContextType getType() {
		return ContextType.CONTEXT_IMMEDIATE;
	}

	@Override
	public View getEditView() {

		return editLayout;
	}
	
	@Override
	public View getDispView() {
		return dispLayout;
	}

}
