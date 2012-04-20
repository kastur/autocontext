package com.autocontext;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.autocontext.observers.ImmediateContextObserver;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

public class Autocontext {

	public static enum ContextType {
		IDENTIFIER,
	
		CONTEXT_CALENDAR_EVENT,
		CONTEXT_CALENDAR_EVENT_FILTER,
		CONTEXT_IMMEDIATE,
		CONTEXT_EVENT_DISPLAY_OFF,
	};
	
	public static enum ActionType {
		ACTION_NOTIFY,
		ACTION_LAUNCH_PACKAGE,
		ACTION_BRIGHTNESS_VALUE,
		ACTION_WIFI,
	};
	
	public static class ContextActionPair {
		private IContext mContext;
		private ActionFlow mActions;
		
		public ContextActionPair(IContext context, ActionFlow actions) {
			mContext = context;
			mActions = actions;
		}
		public IContext getContext() {
			return mContext;
		}
		public ActionFlow getActions() {
			return mActions;
		}
	}
	
	public static abstract class IContext {
		protected Context mAppContext;
		public IContext(Context context) {
			mAppContext = context;
			onCreate(new Bundle());
		}
		public abstract void onCreate(Bundle savedState);
		public abstract ContextType getType();
		public abstract View getEditView();
		public abstract View getDispView();
	}
	
	public static abstract class IAction {
		protected Context mAppContext;
		public IAction(Context context) {
			mAppContext = context;
			onCreate(new Bundle());
		}
		public abstract void onCreate(Bundle savedState);
		public abstract View getEditView();
		public abstract View getDispView();
		public abstract void run();
	}

	public static class ActionFlow extends LinkedList<IAction> {
		private static final long serialVersionUID = 44044658025241362L;
	}
	
	public static class FlowManager implements IContextReceiver {
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

		public void triggerContext(IContext context) {
			for (ContextActionPair pair : mContextActionPairs) {
				if (pair.getContext().equals(context)) {
					for (IAction action : pair.getActions()) {
						action.run();
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
	
	public static interface IContextReceiver {
		public abstract void triggerContext(IContext context);
	}
	
	public static interface IContextObserver {
		public abstract ContextType getType();
		public abstract void init(Context context);
		public abstract void registerCallback(IContextReceiver contextReceiver);
		public abstract void registerContext(IContext context);
	}
}