package com.autocontext;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import android.content.Context;
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
	
	public static interface IContext {
		public abstract ContextType getType();
		public abstract View getView(Context context);
	}
	
	public static interface IAction {
		public abstract void run(Context context);
		public abstract View getView(Context context);
	}

	public static class ActionFlow extends LinkedList<IAction> {
		private static final long serialVersionUID = 44044658025241362L;
	}
	
	public static class FlowManager implements IContextReceiver {
		HashMap<ContextType, IContextObserver> mContextObservers;
		HashMap<ContextType, IContext> mContexts;
		HashSet<ActionFlow> mActionFlows;
		
		HashSet<ContextActionPair> mContextActionPairs;
		
		public FlowManager() {
			mContextObservers = new HashMap<Autocontext.ContextType, Autocontext.IContextObserver>();
			mContexts = new HashMap<Autocontext.ContextType, Autocontext.IContext>();
			mActionFlows = new HashSet<Autocontext.ActionFlow>();
		}
		
		public void init(Context context) {
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

		public void triggerContext(IContext context) {
			System.out.println("NOT IMPLEMENTED");
		}

		public Collection<IContext> getContexts() {
			return mContexts.values();
		}
		
		public Collection<ActionFlow> getActionFlows() {
			return mActionFlows;
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