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
import com.autocontext.Reaction;
import com.autocontext.SensedContext;

public class ToastAction extends Reaction {
	Bundle params;
	LinearLayout editLayout;

    public ToastAction(Bundle savedState) {
        super(savedState);
    }
	
	@Override
	public void run(Context appContext, SensedContext event, Bundle payload) {
		String extraMessage = payload.getString("toastExtras", "No extras");
		Toast.makeText(appContext, params.getString("toastText") + ": " + extraMessage, Toast.LENGTH_SHORT).show();
	}
	
	@Override
	public void onCreate(Bundle savedState) {
		params = savedState;
	}
	
	@Override
	public View getView(Context appContext) {
        editLayout = new LinearLayout(appContext);
        TextView textView = new TextView(appContext);
        textView.setText("Message: ");
        editLayout.addView(textView);
        final EditText editText = new EditText(appContext);
        editText.setText(params.getString("toastText"));
        editLayout.addView(editText);

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void afterTextChanged(Editable s) {
                params.putString("toastText", s.toString());
            }
        });
        return editLayout;
	}

    @Override
    public void destroyView() {

    }
}
