package com.autocontext.contexts;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import com.autocontext.ContextCond;
import com.autocontext.ContextSensor;
import com.autocontext.ContextSpecKind;

public class GeofenceContext extends ContextCond {
	Bundle params;
	LinearLayout editLayout;
	
	public GeofenceContext(Bundle savedState) {
        super(savedState);
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
	public View createView(Context appContext) {
        editLayout = new LinearLayout(appContext);

        EditText latText = new EditText(appContext);
        latText.setText(new Double(params.getDouble("lat")).toString());
        editLayout.addView(latText);

        EditText lonText = new EditText(appContext);
        lonText.setText(new Double(params.getDouble("lon")).toString());
        editLayout.addView(lonText);

        EditText radText = new EditText(appContext);
        radText.setText(new Double(params.getDouble("rad")).toString());
        editLayout.addView(radText);

		latText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				params.putDouble("lat", Double.parseDouble(s.toString()));
			}
		});
		
		lonText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				params.putDouble("lon", Double.parseDouble(s.toString()));
			}
		});
		
		radText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) { }
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
			
			@Override
			public void afterTextChanged(Editable s) {
				params.putDouble("rad", Double.parseDouble(s.toString()));
			}
		});
        return editLayout;
	}

    @Override
    public void destroyView() {

    }

	@Override
	public ContextSpecKind getType() {
		return ContextSpecKind.CONTEXT_GEOFENCE;
	}

	@Override
	public void onAttached(ContextSensor sensor) {
		// TODO Auto-generated method stub
		
	}

}
