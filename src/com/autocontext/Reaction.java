package com.autocontext;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

public abstract class Reaction {

    public Reaction(Bundle savedState) {
        onCreate(savedState);
    }
    public abstract void onCreate(Bundle savedState);
    public abstract void run(Context appContext, SensedContext obs, Bundle payload);
    public abstract View getView(Context context);
    public abstract void destroyView();
}