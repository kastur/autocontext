package com.autocontext;

import android.content.Context;

/**
 * @author Kasturi Rangan Raghavan
 * Continuously senses a specific kind of context (e.g. location, time, ...).
 *
 * Flow manager specifies a set of conditions, and when one of them is encountered
 * the sensor calls back the flow manager.
 */
public abstract class ContextSensor {
    
    // The kind of context. Have to define a new constant.
    public abstract ContextSpecKind getKind();


    // Called once when the FlowManager starts up.
    public abstract void Initialize(Context appContext);

   
    // The FlowManager calls these two functions in order to
    // tell the ContextSensor about the specific contexts to
    // listen for. These are known as ContextSpecs, the specification
    // for what to listen for.
    // For example, if this is the CalenerEventSensor
    // then...the ContextSpec contains a string that matches
    // calendar events.

    // The Sensor has a member variable mManager. When the CotextSpec
    // event is detected, then mManager.functionXXX is called to let the 
    // manager know.
    public abstract void addContextSpec(ContextSpec contextSpec);
    public abstract void removeContextSpec(ContextSpec contextSpec);

    // Like add/remove contexSpec, except this is called when an existing
    // ContextSpec (already registered before) is updated the user.
    public abstract void notifyAboutUpdatedContextSpec(ContextSpec contextSpec);

    // Make sure you register this ContextSensor with the FlowManager.
    public void registerManager(FlowManager manager) {
        mManager = manager;
    }

    protected FlowManager mManager;
}
