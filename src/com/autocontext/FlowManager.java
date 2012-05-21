package com.autocontext;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.CalendarContract;

import com.autocontext.Autocontext.ActionFlow;
import com.autocontext.Autocontext.ContextActionPair;
import com.autocontext.Autocontext.ContextType;
import com.autocontext.Autocontext.IAction;
import com.autocontext.Autocontext.IContext;
import com.autocontext.Autocontext.IContextObserver;
import com.autocontext.Autocontext.IContextReceiver;
import com.autocontext.contexts.CalendarEventContext;
import com.autocontext.observers.CalenderEventContextObserver;
import com.autocontext.observers.ImmediateContextObserver;

public  class FlowManager implements IContextReceiver {
	HashMap<ContextType, IContextObserver> mContextObservers;
	HashMap<ContextType, IContext> mContexts;
	HashSet<ActionFlow> mActionFlows;
	
	HashSet<ContextActionPair> mContextActionPairs;
	
	Context mApplicationContext;
	SharedPreferences mPrefs;
	
	public FlowManager() {
		mContextObservers = new HashMap<Autocontext.ContextType, Autocontext.IContextObserver>();
		mContexts = new HashMap<Autocontext.ContextType, Autocontext.IContext>();
		mActionFlows = new HashSet<Autocontext.ActionFlow>();
		mContextActionPairs = new HashSet<Autocontext.ContextActionPair>();
	}
	
	public void init(Context context) {
		mApplicationContext = context.getApplicationContext();
		
		for (IContextObserver observer : mContextObservers.values()) {
			observer.init(context);
		}

		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		Map<String, ?> allPrefs = mPrefs.getAll();
		for (Object value : allPrefs.values()) {
			final String data = (String)value;
			AddContextActionFromString(data);
		}		
	}
	
	public void AddContextActionFromString(String json) {
		try {
			JSONArray json_params = new JSONArray(json);
			for (int ii = 0; ii < json_params.length(); ++ii) {
				JSONObject json_param = json_params.getJSONObject(ii);
				final ContextType contextType = ContextType.valueOf(json_param.getString("ContextType"));
				switch (contextType) {
					case CONTEXT_CALENDAR_EVENT: 
				}
			}
			for (JSONObject json_param : json_params.notify())
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	public void registerContextObserver(IContextObserver contextObserver) {
		mContextObservers.put(contextObserver.getType(), contextObserver);
		contextObserver.registerCallback(this);
	}
	
	public void registerContext(IContext context) {
		mContexts.put(context.getType(), context);
		mContextObservers.get(context.getType()).registerContext(context);
	}
	
	public void registerActionFlow(ActionFlow actionFlow) {
		mActionFlows.add(actionFlow);
	}
	
	public void registerContextAction(IContext context, ActionFlow actionFlow) {
		ContextActionPair pair = new ContextActionPair(context, actionFlow);
		mContextActionPairs.add(pair);
	}
	
	public void triggerImmediateContexts() {
		ImmediateContextObserver immediateContextObserver = 
		(ImmediateContextObserver)mContextObservers.get(ContextType.CONTEXT_IMMEDIATE);
		immediateContextObserver.triggerContext();
	}

	public void triggerNextCalendarContext() {
		CalenderEventContextObserver contextObserver = 
		(CalenderEventContextObserver)mContextObservers.get(ContextType.CONTEXT_CALENDAR_EVENT);
		contextObserver.triggerNextQueuedContext();
	}
	
	public void triggerContext(IContext context, Bundle payload) {
		for (ContextActionPair pair : mContextActionPairs) {
			if (pair.getContext().equals(context)) {
				for (IAction action : pair.getActions()) {
					action.run(payload);
				}
			}
		}
	}

	public Collection<IContext> getContexts() {
		return mContexts.values();
	}
	
	public Collection<ActionFlow> getActionFlows() {
		return mActionFlows;
	}
	
	public Collection<ContextActionPair> getContextActions() {
		return mContextActionPairs;
	}
}