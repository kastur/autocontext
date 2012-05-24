package com.autocontext;

import android.app.Activity;
import com.autocontext.contexts.CalendarEventContext;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Kasturi Rangan Raghavan
 */
public interface ContextSpec {
    ContextSpecKind getType();
    void attachToSensor(ContextSensor sensor);
    void detachFromSensor(ContextSensor sensor);
    EditableModel getEditable(Activity activity);
}