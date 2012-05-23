package com.autocontext.observers;

import android.content.Context;
import android.os.Bundle;
import com.autocontext.*;

import java.util.HashSet;

public class ImmediateContextSensor extends ContextSensor {
	HashSet<ContextCond> mRegisteredContexts;
	
	@Override
	public ContextSpecKind getKind() {
		return ContextSpecKind.CONTEXT_IMMEDIATE;
	}

	@Override
	public void onCreate(Context context) {
		mRegisteredContexts = new HashSet<ContextCond>();
	}

	@Override
	public void addCond(ContextCond contextCond) {
		mRegisteredContexts.add(contextCond);
	}

    @Override
    public void removeCond(ContextCond contextCond) {
        mRegisteredContexts.remove(contextCond);
    }
	
	public void triggerContext() {
		Bundle payload = new Bundle();
		payload.putLong(ContextSpecKind.CONTEXT_IMMEDIATE.name() + "_timestamp", System.currentTimeMillis());

        SensedContext event = new SensedContext();
        event.id = "IMMEDIATE";
        event.kind = SensedContextKind.IMMEDIATE_EVENT;

		for (ContextCond context : mRegisteredContexts) {
			mManager.triggerContext(context, event, payload);
		}
	}
}
