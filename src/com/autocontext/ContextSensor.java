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
    public abstract ContextSpecKind getKind();
    public abstract void Initialize(Context appContext);
    public abstract void addContextSpec(ContextSpec contextSpec);
    public abstract void removeContextSpec(ContextSpec contextSpec);
    public abstract void notifyAboutUpdatedContextSpec(ContextSpec contextSpec);
    public void registerManager(FlowManager manager) {
        mManager = manager;
    }

    protected FlowManager mManager;
}