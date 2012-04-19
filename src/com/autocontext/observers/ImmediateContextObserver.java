package com.autocontext.observers;

import android.content.Context;

import com.autocontext.Autocontext.ContextType;
import com.autocontext.Autocontext.IContext;
import com.autocontext.Autocontext.IContextObserver;
import com.autocontext.Autocontext.IContextReceiver;

public class ImmediateContextObserver implements IContextObserver {
	IContextReceiver mContextReceiver;
	
	@Override
	public ContextType getType() {
		return ContextType.CONTEXT_IMMEDIATE;
	}

	@Override
	public void init(Context context) {
	}

	@Override
	public void registerCallback(IContextReceiver contextReceiver) {
		mContextReceiver = contextReceiver;
	}

	@Override
	public void registerContext(IContext context) {
		mContextReceiver.triggerContext(context);
	}
	

}
