package com.autocontext;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import com.autocontext.actions.SuppressGPSAction;
import com.autocontext.contexts.CalendarEventContext;
import com.autocontext.observers.CalenderEventContextSensor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public  class FlowManager {
	HashMap<ContextSpecKind, ContextSensor> mContextObservers;
    ArrayList<Flow> mFlows;
    HashMap<ContextSpec, Flow> mFlowsByContext;

	Context mApplicationContext;
	SharedPreferences mPrefs;
	
	public FlowManager() {
		mContextObservers = new HashMap<ContextSpecKind, ContextSensor>();
		mFlowsByContext = new HashMap<ContextSpec, Flow>();
		mFlows = new ArrayList<Flow>();
	}
	
	public void init(Context context) {
		mApplicationContext = context.getApplicationContext();
		
		for (ContextSensor sensor : mContextObservers.values()) {
			sensor.onCreate(context);
		}

		
		mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
		Map<String, ?> allPrefs = mPrefs.getAll();
		for (Object dataObj : allPrefs.values()) {
			final String dataStr = (String)dataObj;
			AddContextActionFromString(dataStr);
		}		
	}
	
	public void AddContextActionFromString(String savedJson) {
		try {
			JSONArray flowsJson = new JSONArray(savedJson);
			for (int ii = 0; ii < flowsJson.length(); ++ii) {
				JSONObject flowJson = flowsJson.getJSONObject(ii);
                Flow parsedFlow = new Flow(this);

                // Parse the ContextSpec
                JSONObject contextJson = flowJson.getJSONObject("ContextSpec");
                String contextSpecKindString = contextJson.getString("ContextSpecKind");
                ContextSpecKind contextSpecKind =
                        ContextSpecKind.valueOf(contextSpecKindString);
                ContextSpec parsedContextSpec = null;
                switch (contextSpecKind) {
                    case CONTEXT_CALENDAR_EVENT:
                        CalendarEventContext newContextSpec = new CalendarEventContext();
                        newContextSpec.loadFromJSON(contextJson);
                        parsedContextSpec = newContextSpec;
                        break;
				}
                parsedFlow.setContextSpec(parsedContextSpec);

                JSONArray actionsJson = flowJson.getJSONArray("Reactions");
                for (int aa = 0; aa < actionsJson.length(); ++aa) {

                    JSONObject actionJson = actionsJson.getJSONObject(aa);
                    String reactionKindString = actionJson.getString("ReactionKind");
                    ReactionKind reactionKind = ReactionKind.valueOf(reactionKindString);
                    Reaction parsedReaction = null;
                    switch(reactionKind) {
                        case REACTION_SUPPRESS_GPS:
                            SuppressGPSAction newAction = new SuppressGPSAction();
                            newAction.loadFromJSON(actionJson);
                            parsedReaction = newAction;
                    }

                    if (parsedReaction != null) {
                        parsedFlow.addReaction(parsedReaction);
                    }
                }

                onAddContextSpec(parsedFlow);
                mFlows.add(parsedFlow);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void registerContextObserver(ContextSensor contextSensor) {
		mContextObservers.put(contextSensor.getKind(), contextSensor);
		contextSensor.registerManager(this);
	}

    public void onBeforeRemoveContextSpec(Flow flow) {
        ContextSpec contextSpec = flow.getContextSpec();
        ContextSensor contextSensor = mContextObservers.get(contextSpec.getType());
        contextSpec.detachFromSensor(contextSensor);
        mFlowsByContext.remove(contextSpec);
    }


    public void onAddContextSpec(Flow flow) {
        ContextSpec contextSpec = flow.getContextSpec();
        ContextSensor contextSensor = mContextObservers.get(contextSpec.getType());
        contextSpec.attachToSensor(contextSensor);
        mFlowsByContext.put(contextSpec, flow);
    }

    public Flow getNewFlow() {
        Flow flow = new Flow(this);
        mFlows.add(flow);
        return flow;
    }

	public void triggerNextCalendarContext() {
		CalenderEventContextSensor contextObserver =
		(CalenderEventContextSensor)mContextObservers.get(ContextSpecKind.CONTEXT_CALENDAR_EVENT);
		contextObserver.triggerNextQueuedContext();
	}
	
	public void triggerContext(ContextSpec contextSpec, SensedContext event, Bundle payload) {
        Flow flow = mFlowsByContext.get(contextSpec);
        for (Reaction reaction : flow.getActions()) {
            reaction.run(mApplicationContext, event, payload);
        }
	}

	public ArrayList<Flow> getFlows() {
		return mFlows;
	}

    public Flow getFlow(int flow_ii) {
        return mFlows.get(flow_ii);
    }
}