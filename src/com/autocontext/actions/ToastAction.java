package com.autocontext.actions;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.autocontext.Autocontext.IAction;

public class ToastAction extends IAction {
	public ToastAction(Context context) {
		super(context);
	}

	Bundle params;
	LinearLayout editLayout;
	LinearLayout dispLayout;
	
	@Override
	public void run(Bundle payload) {
		Toast.makeText(mAppContext, params.getString("toastText"), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onCreate(Bundle savedState) {
		params = savedState;
		
		editLayout = new LinearLayout(mAppContext);
		TextView textView = new TextView(mAppContext);
		textView.setText("Message: ");
		editLayout.addView(textView);
		final EditText editText = new EditText(mAppContext);
		editText.setText(params.getString("toastText"));
		editLayout.addView(editText);
		
		dispLayout = new LinearLayout(mAppContext);
	    TextView dispLabel = new TextView(mAppContext);
	    dispLabel.setText("Toast: ");
	    dispLayout.addView(dispLabel);
		final TextView dispView = new TextView(mAppContext);
		dispView.setText(params.getString("toastText"));
		dispLayout.addView(dispView);
		
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				dispView.setText(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				params.putString("toastText", s.toString());
			}
		});
		
		
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
