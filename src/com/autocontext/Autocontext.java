package com.autocontext;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.autocontext.observers.ImmediateContextObserver;

import android.content.Context;
import android.os.Bundle;
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
		public abstract void run(Bundle payload);
		public abstract View getEditView();
		public abstract View getDispView();
	}

	public static class ActionFlow extends LinkedList<IAction> {
		private static final long serialVersionUID = 44044658025241362L;
	}
	
	public static interface IContextReceiver {
		public abstract void triggerContext(IContext context, Bundle payload);
	}
	
	public static interface IContextObserver {
		public abstract ContextType getType();
		public abstract void init(Context context);
		public abstract void registerCallback(IContextReceiver contextReceiver);
		public abstract void registerContext(IContext context);
	}
}