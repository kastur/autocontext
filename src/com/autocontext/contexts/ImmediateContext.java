package com.autocontext.contexts;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.autocontext.Autocontext.ContextType;
import com.autocontext.Autocontext.IContext;

public class ImmediateContext implements IContext {
	@Override
	public ContextType getType() {
		return ContextType.CONTEXT_IMMEDIATE;
	}

	@Override
	public View getView(Context context) {
		LinearLayout layout = new LinearLayout(context);
		TextView textView = new TextView(context);
		textView.setText("INTERNAL: Immediate context");
		layout.addView(textView);
		return layout;
	}
}
