package com.autocontext.observers;

import java.util.HashSet;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.autocontext.Autocontext.ContextType;
import com.autocontext.Autocontext.IContext;
import com.autocontext.Autocontext.IContextObserver;
import com.autocontext.Autocontext.IContextReceiver;
import com.autocontext.contexts.GeofenceContext;

public class GeoFenceContextObserver implements IContextObserver {
	IContextReceiver mContextReceiver;
	HashSet<GeofenceContext> mRegisteredContexts;
	
	@Override
	public ContextType getType() {
		return ContextType.CONTEXT_IMMEDIATE;
	}

	@Override
	public void init(Context appContext) {
		mRegisteredContexts = new HashSet<GeofenceContext>();
		LocationManager locationManager = (LocationManager)appContext.getSystemService(Context.LOCATION_SERVICE);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mListener);
	}

	@Override
	public void registerCallback(IContextReceiver contextReceiver) {
		mContextReceiver = contextReceiver;
	}

	@Override
	public void registerContext(IContext context) {
		mRegisteredContexts.add((GeofenceContext)context);
	}
	
	public void triggerContext() {
		Bundle payload = new Bundle();
		payload.putLong(ContextType.CONTEXT_IMMEDIATE.name() + "_timestamp", System.currentTimeMillis());
		for (IContext context : mRegisteredContexts) {
			mContextReceiver.triggerContext(context, payload);
		}
	}
	
	private LocationListener mListener = new LocationListener() {

		@Override
		public void onLocationChanged(Location location) {
			for (GeofenceContext geoContext : mRegisteredContexts) {
				if (Math.abs(location.getLongitude() - geoContext.getLongitude()) < geoContext.getRadius() &&
					Math.abs(location.getLatitude() - geoContext.getLatitude()) < geoContext.getRadius()) {
					mContextReceiver.triggerContext(geoContext, new Bundle());
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
