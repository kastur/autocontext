package com.autocontext;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import org.json.JSONObject;

public interface Reaction {
    void run(Context appContext, SensedContext obs, Bundle payload);
    EditableModel getEditable(Activity activity);
}