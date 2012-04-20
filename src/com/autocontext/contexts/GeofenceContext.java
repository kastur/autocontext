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

public class GeofenceContext extends IContext {
	Bundle params;
	LinearLayout editLayout;
	LinearLayout dispLayout;
	
	public GeofenceContext(Context appContext) {
		super(appContext);
	}
	
	public double getLatitude() {
		return params.getDouble("lat");
	}
	
	public double getLongitude() {
		return params.getDouble("lon");
	}
	
	public double getRadius() {
		return params.getDouble("rad");
	}
	
	@Override
	public void onCreate(Bundle savedState) {
		params = savedState;
		editLayout = new LinearLayout(mAppContext);
		
		EditText latText = new EditText(mAppContext);
		latText.setText(new Double(params.getDouble("lat")).toString());
		editLayout.addView(latText);
		
		EditText lonText = new EditText(mAppContext);
		lonText.setText(new Double(params.getDouble("lon")).toString());
		editLayout.addView(lonText);
		
		EditText radText = new EditText(mAppContext);
		radText.setText(new Double(params.getDouble("rad")).toString());
		editLayout.addView(radText);
		
		
		dispLayout = new LinearLayout(mAppContext);
		final TextView latDispText = new TextView(mAppContext);
		latDispText.setText(new Double(params.getDouble("lat")).toString());
		dispLayout.addView(latDispText);
		
		final TextView lonDispText = new TextView(mAppContext);
		lonDispText.setText(new Double(params.getDouble("lon")).toString());
		dispLayout.addView(lonDispText);
		
		final TextView radDispText = new TextView(mAppContext);
		radDispText.setText(new Double(params.getDouble("rad")).toString());
		dispLayout.addView(radDispText);
		
		latText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				latDispText.setText(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				params.putDouble("lat", Double.parseDouble(s.toString()));
			}
		});
		
		lonText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				lonDispText.setText(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				params.putDouble("lon", Double.parseDouble(s.toString()));
			}
		});
		
		radText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				radDispText.setText(s);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				params.putDouble("rad", Double.parseDouble(s.toString()));
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
