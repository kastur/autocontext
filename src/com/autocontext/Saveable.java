package com.autocontext;

import org.json.JSONException;
import org.json.JSONObject;

public interface Saveable {
    void loadFromJSON(JSONObject json) throws JSONException;
    JSONObject saveToJSON() throws JSONException;
}
