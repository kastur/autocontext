package com.autocontext.observers;

import java.util.HashSet;

import android.content.Context;
import android.os.Bundle;

import com.autocontext.Autocontext.ContextType;
import com.autocontext.Autocontext.IContext;
import com.autocontext.Autocontext.IContextObserver;
import com.autocontext.Autocontext.IContextReceiver;

public class ImmediateContextObserver implements IContextObserver {
	IContextReceiver mContextReceiver;
	HashSet<IContext> mRegisteredContexts;
	
	@Override
	public ContextType getType() {
		return ContextType.CONTEXT_IMMEDIATE;
	}

	@Override
	public void init(Context context) {
		mRegisteredContexts = new HashSet<IContext>();
	}

	@Override
	public void registerCallback(IContextReceiver contextReceiver) {
		mContextReceiver = contextReceiver;
	}

	@Override
	public void registerContext(IContext context) {
		mRegisteredContexts.add(context);
	}
	
	public void triggerContext() {
		Bundle payload = new Bundle();
		payload.putLong(ContextType.CONTEXT_IMMEDIATE.name() + "_timestamp", System.currentTimeMillis());
		for (IContext context : mRegisteredContexts) {
			mContextReceiver.triggerContext(context, payload);
		}
	}

	@Override
	public void onContextUpdated(IContext context) {
		registerContext(context);
	}
	

}
