package com.autocontext;

import android.app.Activity;
import com.autocontext.contexts.CalendarEventContext;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Holds the condition that the ContextSensor can use to detect 
 * a specific context event.
 *
 * For example, the CalendarEventContext holds a "filterText" field
 * that is used by the corresponding Sensor to match calendar events
 * by their name.
 *
 * However, the ContextSpec also needs to have the code to display/edit
 * these parameters. For example, a textbox to edit the filterText.
 *
 * The FlowManager creates ContextSpecs, and calls .attachToSensor or
 * .detachFromSensor. Then the ContextSpec should register itself
 * with the Sensor, and also update the Sensor as the user-specified
 * parameters are changed.
 *
 * @author Kasturi Rangan Raghavan
 */
public interface ContextSpec {
    ContextSpecKind getType();

    // Whenever the FlowManager tells the ContextSensor about
    // a new ContextSpec, the ContextSensor also lets each
    // of the registered ContextSpecs about the ContextSensor.
    // TODO: Put some default code here.
    void attachToSensor(ContextSensor sensor);
    void detachFromSensor(ContextSensor sensor);

    EditableModel getEditable(Activity activity);

    void loadFromJSON(JSONObject json) throws JSONException;
    JSONObject saveToJSON() throws JSONException;
}
