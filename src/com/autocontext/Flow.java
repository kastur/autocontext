package com.autocontext;

import android.app.ActionBar;
import android.app.Activity;
import android.database.DataSetObserver;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
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
    private String mName;

    public Flow(FlowManager manager) {
        mManager = manager;
        mContextSpec = null;
        mReactions = new ArrayList<Reaction>();
        mName = "";
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

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public EditableModel getEditable(Activity activity) {
        return new ModelView(activity);
    }

    private class ModelView implements EditableModel {
        Activity activity;
        View rootView;
        ActionArrayAdapter actionAdapter;

        public ModelView(Activity activity) {
            this.activity = activity;
            this.actionAdapter = new ActionArrayAdapter();
        }

        @Override
        public View getEditView() {
            rootView = activity.getLayoutInflater().inflate(R.layout.flow_view, null);

            EditText nameTextView =
                    (EditText)rootView.findViewById(R.id.flow_edit_name);
            nameTextView.setText(getName());
            nameTextView.addTextChangedListener(nameTextWatcher);

            LinearLayout contextLayout =
                    (LinearLayout)rootView.findViewById(R.id.context_layout);
            if (mContextSpec != null) {
                View contextView = mContextSpec.getEditable(activity).getEditView();
                contextLayout.addView(contextView);
            } else {
                View chooseContextView =
                        activity.getLayoutInflater().inflate(R.layout.flow_context_chooser, null);
                Button chooseContextButton = (Button)chooseContextView.findViewById(R.id.choose_calendar_event_context);
                chooseContextButton.setOnClickListener(chooseContextClickListener);
                contextLayout.addView(chooseContextView);
            }

            ListView actionsListView = (ListView)rootView.findViewById(R.id.actions_layout);
            actionsListView.setAdapter(actionAdapter);
            actionsListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
            actionsListView.setOnItemLongClickListener(actionLongClickListener);
            actionAdapter.addAll(mReactions);

            Button addActionButton = (Button)rootView.findViewById(R.id.add_suppress_gps_action);
            addActionButton.setOnClickListener(addActionClickListener);

            return rootView;

        }

        private TextWatcher nameTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //To change body of implemented methods use File | Settings | File Templates.
            }

            @Override
            public void afterTextChanged(Editable editable) {
                setName(editable.toString());
            }
        };

        private View.OnClickListener chooseContextClickListener = new View.OnClickListener() {
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
        };

        private View.OnClickListener addActionClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ListView actionsListView =
                        (ListView)rootView.findViewById(R.id.actions_layout);

                Reaction newReaction = null;
                if (view.getId() == R.id.add_suppress_gps_action) {
                    newReaction = new SuppressGPSAction();
                }

                mReactions.add(newReaction);
                actionAdapter.add(newReaction);
                actionAdapter.notifyDataSetChanged();
            }
        };

        private class ActionArrayAdapter extends ArrayAdapter<Reaction> {
            ActionArrayAdapter() {
                super(activity, R.layout.action_removable);
            }
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View actionView = getItem(position).getEditable(activity).getEditView();
                return actionView;
            }

            public void removeSelected() {
                Reaction reaction = getItem(mSelectedActionIndex);
                remove(reaction);
                removeReaction(reaction);
                notifyDataSetChanged();
            }
        };

        int mSelectedActionIndex = -1;
        AdapterView.OnItemLongClickListener actionLongClickListener = new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int item_ii, long l) {

                ActionBar actionBar = activity.getActionBar();
                ActionMode actionMode = activity.startActionMode(mActionLongPressContextualActionBarCallback);
                view.setSelected(true);
                mSelectedActionIndex = item_ii;
                return true;
            }
        };

        ActionMode.Callback mActionLongPressContextualActionBarCallback = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                MenuInflater inflater = actionMode.getMenuInflater();
                inflater.inflate(R.menu.action_actbar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                if (menuItem.getItemId() == R.id.action_actbar_remove_action) {
                    actionAdapter.removeSelected();
                }
                actionMode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                //To change body of implemented methods use File | Settings | File Templa   tes.
            }
        };


    }
}
