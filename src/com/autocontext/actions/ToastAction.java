package com.autocontext.actions;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.autocontext.*;
import org.json.JSONException;
import org.json.JSONObject;

public class ToastAction implements Reaction, Saveable {

	String mToastText = "";

    @Override
    public ReactionKind getType() {
        return ReactionKind.REACTION_TOAST;
    }

    @Override
	public void run(Context appContext, SensedContext event, Bundle payload) {
		String extraMessage = payload.getString("toastExtras", "No extras");
		Toast.makeText(appContext, mToastText + ": " + extraMessage, Toast.LENGTH_SHORT).show();
	}

    @Override
    public EditableModel getEditable(Activity activity) {
        return new ModelView(activity);
    }

    @Override
    public void loadFromJSON(JSONObject json) throws JSONException {
        mToastText = json.getString("toast_text");
    }

    @Override
    public JSONObject saveToJSON() throws JSONException {
        JSONObject json = new JSONObject();
        json.put("toast_text", mToastText);
        return json;
    }

    private class ModelView implements EditableModel, TextWatcher {
        Activity activity;
        public ModelView(Activity activity) {
            this.activity = activity;
        }

        @Override
        public View getEditView() {
            View view = activity.getLayoutInflater().inflate(R.layout.action_toast, null);
            EditText editText = (EditText)view.findViewById(R.id.toast_text);
            editText.setText(mToastText);
            editText.addTextChangedListener(this);
            return view;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

        @Override
        public void afterTextChanged(Editable editable) {
            mToastText = editable.toString();
        }
    }
}
