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
    public abstract void onCreate(Context appContext);
    public abstract void addCond(ContextCond contextCond);
    public abstract void removeCond(ContextCond contextCond);
    public void registerManager(FlowManager manager) {
        mManager = manager;
    }

    protected FlowManager mManager;
}