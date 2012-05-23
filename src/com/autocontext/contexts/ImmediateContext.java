package com.autocontext.contexts;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.autocontext.ContextCond;
import com.autocontext.ContextSensor;
import com.autocontext.ContextSpecKind;

public class ImmediateContext extends ContextCond {
	Bundle params;
	LinearLayout editLayout;

	public ImmediateContext(Bundle savedState) {
		super(savedState);
	}

	@Override
	public ContextSpecKind getType() {
		return ContextSpecKind.CONTEXT_IMMEDIATE;
	}

	@Override
	public View createView(Context appContext) {
        editLayout = new LinearLayout(appContext);
        TextView textView = new TextView(appContext);
        textView.setText("IMMEDIATE");
        editLayout.addView(textView);
        return editLayout;
	}

    @Override
    public void destroyView() {

    }

	@Override
	public void onAttached(ContextSensor sensor) {
		// TODO Auto-generated method stub
		
	}

}
