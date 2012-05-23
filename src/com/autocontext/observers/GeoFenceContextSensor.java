package com.autocontext.observers;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.autocontext.ContextCond;
import com.autocontext.ContextSensor;
import com.autocontext.ContextSpecKind;
import com.autocontext.FlowManager;
import com.autocontext.contexts.GeofenceContext;

import java.util.HashSet;

public class GeoFenceContextSensor extends ContextSensor {
	FlowManager mFlowManager;
	HashSet<GeofenceContext> mRegisteredContexts;
	
	@Override
	public ContextSpecKind getKind() {
		return ContextSpecKind.CONTEXT_CALENDAR_EVENT;
	}

	@Override
	public void onCreate(Context appContext) {
		mRegisteredContexts = new HashSet<GeofenceContext>();
		LocationManager locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mListener);
	}

	@Override
	public void registerManager(FlowManager manager) {
		mFlowManager = manager;
	}

	@Override
	public void addCond(ContextCond contextCond) {
		mRegisteredContexts.add((GeofenceContext) contextCond);
	}

    @Override
    public void removeCond(ContextCond contextCond) {
        mRegisteredContexts.remove(contextCond);
    }
	
	public void triggerContext() {
		Bundle payload = new Bundle();
		payload.putLong(ContextSpecKind.CONTEXT_IMMEDIATE.name() + "_timestamp", System.currentTimeMillis());
		for (ContextCond context : mRegisteredContexts) {
			mFlowManager.triggerContext(context, null, payload);
		}
	}
	
	private LocationListener mListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			for (GeofenceContext geoContext : mRegisteredContexts) {
				if (Math.abs(location.getLongitude() - geoContext.getLongitude()) < geoContext.getRadius() &&
					Math.abs(location.getLatitude() - geoContext.getLatitude()) < geoContext.getRadius()) {
					mFlowManager.triggerContext(geoContext, null, new Bundle());
				}
			}
		}

		@Override
		public void onProviderDisabled(String arg0) { }

		@Override
		public void onProviderEnabled(String arg0) { }

		@Override
		public void onStatusChanged(String arg0, int arg1, Bundle arg2) {	}
		
	};
}
