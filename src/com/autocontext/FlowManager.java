package com.autocontext;

import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import com.autocontext.observers.CalenderEventContextSensor;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

public  class FlowManager {
    private static final String TAG = "FlowManager";
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
	
	public void Initialize(Context context) {
		mApplicationContext = context.getApplicationContext();
		for (ContextSensor sensor : mContextObservers.values()) {
			sensor.Initialize(context);
		}
	}

    public void loadFlows() {
        for (int ii = 0; ii < mFlows.size(); ++ii) {
            removeFlow(ii);
        }

        mPrefs = PreferenceManager.getDefaultSharedPreferences(mApplicationContext);



        try {
            JSONArray flowsJson = new JSONArray(mPrefs.getString("flows", "[]"));
            Log.i(TAG, "LOADING JSON");
            Log.i(TAG, flowsJson.toString());
            for (int ii = 0; ii < flowsJson.length(); ++ii) {
                Flow flow = new Flow(this);
                flow.loadFromJSON(flowsJson.getJSONObject(ii));
                mFlows.add(flow);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(mApplicationContext, "Error loading saved flows.", Toast.LENGTH_LONG);
        }
    }

    public void saveFlows() {

        int ii = 0;
        JSONArray flowsJson = new JSONArray();

        try {
            for (Flow flow : mFlows) {
                JSONObject flowJson = flow.saveToJSON();
                flowsJson.put(flowJson);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(mApplicationContext, "Error saving flows.", Toast.LENGTH_LONG);
        }

        SharedPreferences.Editor ed = mPrefs.edit();
        Log.i(TAG, "SAVING JSON");
        Log.i(TAG, flowsJson.toString());
        ed.putString("flows", flowsJson.toString());
        ed.commit();
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

    public void removeFlow(int flow_ii) {
        Flow flow = mFlows.get(flow_ii);
        if (flow.getContextSpec() != null) {
            this.onBeforeRemoveContextSpec(flow);
        }
        mFlows.remove(flow);
    }

    public int getNewFlow() {
        Flow flow = new Flow(this);
        mFlows.add(flow);
        return mFlows.size()-1;
    }
}