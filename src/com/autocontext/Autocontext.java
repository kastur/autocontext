package com.autocontext;

import java.util.HashMap;
import java.util.LinkedList;

import android.view.View;

public class Autocontext {

	public static enum FlowType {
		IDENTIFIER,
	
		CONTEXT_CALENDAR_EVENT,
		CONTEXT_CALENDAR_EVENT_FILTER,
		
		ACTION_NOTIFY,
		ACTION_LAUNCH_PACKAGE,
		ACTION_BRIGHTNESS_VALUE
	};

	public static interface IFlow {
		public abstract FlowType getType();
		public abstract View getView();
	}

	public static class FlowMap extends HashMap<FlowType, IFlow> {
		private static final long serialVersionUID = 44044658025241362L;
	}
}