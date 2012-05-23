package com.autocontext;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import com.autocontext.actions.SuppressGPSAction;

import java.util.LinkedList;

public class ReactionsList extends LinkedList<Reaction> {
    private static final long serialVersionUID = 44044658025241362L;

    private LinearLayout layout;

    public View getView(Context context) {
        layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);

        for (Reaction action : this) {
            View actionView = action.getView(context);
            layout.addView(actionView);
        }

        Button addActionButton = new Button(context);
        addActionButton.setText("Add action");
        addActionButton.setOnClickListener(addActionClicked);
        layout.addView(addActionButton);

        return layout;
    }

    public void destroyView() {
        layout.removeAllViews();
        for (Reaction action : this) {
            action.destroyView();
        }
    }

    View.OnClickListener addActionClicked = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Reaction newAction = new SuppressGPSAction(new Bundle());
            ReactionsList.this.add(newAction);
            View actionView = newAction.getView(view.getContext());
            layout.addView(actionView);
        }
    };
}