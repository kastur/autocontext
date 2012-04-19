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

public class ToastAction implements IAction {
	final Bundle mParams;
	
	public ToastAction() {
		mParams = new Bundle();
	}
	
	@Override
	public void run(Context context) {
		Toast.makeText(context, mParams.getString("toastText"), Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public View getView(Context context) {
		LinearLayout layout = new LinearLayout(context);
		
		TextView textView = new TextView(context);
		textView.setText("Message: ");
		layout.addView(textView);
		
		final EditText editText = new EditText(context);
		editText.setText(mParams.getString("toastText"));
		editText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				mParams.putString("toastText", s.toString());
			}
		});
		layout.addView(editText);

		return layout;
	}
}
