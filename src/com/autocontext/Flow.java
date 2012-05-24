package com.autocontext;

import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.autocontext.actions.SuppressGPSAction;
import com.autocontext.contexts.CalendarEventContext;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Flow {
    private FlowManager mManager;
    private ContextSpec mContextSpec;
    private ArrayList<Reaction> mReactions;

    public Flow(FlowManager manager) {
        mManager = manager;
        mContextSpec = null;
        mReactions = new ArrayList<Reaction>();
    }

    public ContextSpec getContextSpec() {
        return mContextSpec;
    }

    public void setContextSpec(ContextSpec spec) {
        if (mContextSpec != null)
            mManager.onBeforeRemoveContextSpec(this);

        mContextSpec = spec;
        mManager.onAddContextSpec(this);
    }

    public void addReaction(Reaction reaction) {
        mReactions.add(reaction);
    }

    public void removeReaction(Reaction reaction) {
        mReactions.remove(reaction);
    }

    public ArrayList<Reaction> getActions() {
        return mReactions;
    }

    public EditableModel getEditable(Activity activity) {
        return new ModelView(activity);
    }

    private class ModelView implements EditableModel, View.OnClickListener {
        Activity activity;
        View rootView;

        public ModelView(Activity activity) {
            this.activity = activity;
        }
        @Override
        public View getEditView() {
            rootView = activity.getLayoutInflater().inflate(R.layout.flow_view, null);

            LinearLayout contextLayout =
                    (LinearLayout)rootView.findViewById(R.id.context_layout);
            if (mContextSpec != null) {
                View contextView = mContextSpec.getEditable(activity).getEditView();
                contextLayout.addView(contextView);
            } else {
                View chooseContextView =
                        activity.getLayoutInflater().inflate(R.layout.flow_context_chooser, null);
                Button button = (Button)chooseContextView.findViewById(R.id.choose_calendar_event_context);
                button.setOnClickListener(this);
                contextLayout.addView(chooseContextView);
            }

            return rootView;
        }

        @Override
        public void onClick(View view) {
            LinearLayout contextLayout =
                    (LinearLayout)rootView.findViewById(R.id.context_layout);
            contextLayout.removeAllViews();

            if (mContextSpec != null) {
                mManager.onBeforeRemoveContextSpec(Flow.this);
            }

            if (view.getId() == R.id.choose_calendar_event_context) {
                mContextSpec = new CalendarEventContext();
                mManager.onAddContextSpec(Flow.this);
            }

            View contextView = mContextSpec.getEditable(activity).getEditView();
            contextLayout.addView(contextView);
        }
    }
}
