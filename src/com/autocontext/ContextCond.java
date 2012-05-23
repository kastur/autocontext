package com.autocontext;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public abstract class ContextCond {
    public ContextCond(Bundle savedState) { }
    public abstract ContextSpecKind getType();
    public abstract View createView(Context context);
    public abstract void destroyView();
    public abstract void onAttached(ContextSensor sensor);
}