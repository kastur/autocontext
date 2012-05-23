package com.autocontext;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.autocontext.contexts.CalendarEventContext;

public class Flow {
    private FlowManager mManager;
    private ContextCond mContextCond;
    private ReactionsList mActions;

    private LinearLayout layout;
    private LinearLayout contextLayout;

    public Flow(FlowManager manager) {
        mManager = manager;
        mActions = new ReactionsList();

        mManager.registerActionFlow(mActions);
    }

    public ContextCond getContext() {
        return mContextCond;
    }

    public ReactionsList getActions() {
        return mActions;
    }


    public View getView(Context context) {
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        contextLayout = new LinearLayout(context);

        // Choose context button.
        Button chooseContextButton = new Button(context);
        chooseContextButton.setText("Choose Calendar Context");
        chooseContextButton.setOnClickListener(addContextClicked);
        layout.addView(chooseContextButton);

        // Display the chosen context (if any).
        if (mContextCond != null) {
            View contextView = mContextCond.createView(context);
            contextLayout.addView(contextView);
        }
        layout.addView(contextLayout);

        // List of Actions.
        View actionFlowView = mActions.getView(context);
        layout.addView(actionFlowView);

        return layout;
    }

    public void destroyView(Context context) {
        mContextCond.destroyView();
        mActions.destroyView();
        layout.removeAllViews();
    }

    View.OnClickListener addContextClicked = new View.OnClickListener() {
        public void onClick(View view) {
            Context appContext = view.getContext();

            if (mContextCond != null) {
                mContextCond.destroyView();
                mManager.unregisterContext(mContextCond);
            }

            contextLayout.removeAllViews();

            mContextCond = new CalendarEventContext(new Bundle());
            mManager.registerContext(mContextCond);
            View contextView = mContextCond.createView(appContext);
            contextLayout.addView(contextView);
        }
    };
}
