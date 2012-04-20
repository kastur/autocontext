package com.autocontext;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;
import android.os.Bundle;

import com.autocontext.Autocontext.ActionFlow;
import com.autocontext.Autocontext.ContextActionPair;
import com.autocontext.Autocontext.ContextType;
import com.autocontext.Autocontext.IAction;
import com.autocontext.Autocontext.IContext;
import com.autocontext.Autocontext.IContextObserver;
import com.autocontext.Autocontext.IContextReceiver;
import com.autocontext.observers.ImmediateContextObserver;

public  class FlowManager implements IContextReceiver {
	HashMap<ContextType, IContextObserver> mContextObservers;
	HashMap<ContextType, IContext> mContexts;
	HashSet<ActionFlow> mActionFlows;
	
	HashSet<ContextActionPair> mContextActionPairs;
	
	Context mApplicationContext;
	
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