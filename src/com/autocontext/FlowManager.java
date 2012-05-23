package com.autocontext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.autocontext.observers.CalenderEventContextSensor;
import com.autocontext.observers.ImmediateContextSensor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public  class FlowManager {
	HashMap<ContextSpecKind, ContextSensor> mContextObservers;
	HashMap<ContextSpecKind, ContextCond> mContexts;
	HashSet<ReactionsList> mReactionsLists;
	
	ArrayList<Flow> mFlows;
	
	Context mApplicationContext;
	SharedPreferences mPrefs;
	
	public FlowManager() {
		mContextObservers = new HashMap<ContextSpecKind, ContextSensor>();
		mContexts = new HashMap<ContextSpecKind, ContextCond>();
		mReactionsLists = new HashSet<ReactionsList>();
		mFlows = new ArrayList<Flow>();
	}
	
	public void init(Context context) {
		mApplicationContext = context.getApplicationContext();
		
		for (ContextSensor sensor : mContextObservers.values()) {
			sensor.onCreate(context);
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
				final ContextSpecKind contextType = ContextSpecKind.valueOf(json_param.getString("ContextSpecKind"));
				switch (contextType) {
					case CONTEXT_CALENDAR_EVENT: 
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}

	public void registerContextObserver(ContextSensor contextSensor) {
		mContextObservers.put(contextSensor.getKind(), contextSensor);
		contextSensor.registerManager(this);
	}
	
	public void registerContext(ContextCond contextCond) {
		mContexts.put(contextCond.getType(), contextCond);
		mContextObservers.get(contextCond.getType()).addCond(contextCond);
	}

    public void unregisterContext(ContextCond contextCond) {
        mContexts.remove(contextCond);
        mContextObservers.get(contextCond.getType()).removeCond(contextCond);
    }
	
	public void registerActionFlow(ReactionsList reactionsList) {
		mReactionsLists.add(reactionsList);
	}

    public Flow getEmptyContextAction() {
        ReactionsList reactionsList = new ReactionsList();
        Flow pair = new Flow(this);
        mFlows.add(pair);
        return pair;
    }
	
	public void triggerImmediateContexts() {
		ImmediateContextSensor immediateContextObserver =
		(ImmediateContextSensor)mContextObservers.get(ContextSpecKind.CONTEXT_IMMEDIATE);
		immediateContextObserver.triggerContext();
	}

	public void triggerNextCalendarContext() {
		CalenderEventContextSensor contextObserver =
		(CalenderEventContextSensor)mContextObservers.get(ContextSpecKind.CONTEXT_CALENDAR_EVENT);
		contextObserver.triggerNextQueuedContext();
	}
	
	public void triggerContext(ContextCond contextCond, SensedContext event, Bundle payload) {
		for (Flow pair : mFlows) {
			if (pair.getContext().equals(contextCond)) {
				for (Reaction action : pair.getActions()) {
					action.run(mApplicationContext, event, payload);
				}
			}
		}
	}

	public Collection<ContextCond> getContexts() {
		return mContexts.values();
	}
	
	public Collection<ReactionsList> getActionFlows() {
		return mReactionsLists;
	}
	
	public ArrayList<Flow> getContextActions() {
		return mFlows;
	}
}