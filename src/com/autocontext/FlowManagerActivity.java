package com.autocontext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.*;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class FlowManagerActivity extends BaseFlowActivity {
    FlowArrayAdapter flowArrayAdapter = null;

    @Override
    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);
        setContentView(R.layout.flow_list);
        flowArrayAdapter = new FlowArrayAdapter();
    }

    @Override
    public void handleOnServiceConnected(Message msg) {
        super.handleOnServiceConnected(msg);

        ListView flowListView = (ListView)findViewById(R.id.flow_list_view);
        flowListView.setAdapter(flowArrayAdapter);
        flowListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        flowListView.setOnItemClickListener(flowItemClickListener);

        flowArrayAdapter.addAll(mFlowManager.getFlows());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.pact_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pact_save_flows:
                mFlowManager.saveFlows();
                return true;
            case R.id.pact_load_flows:
                for (Flow flow : mFlowManager.getFlows())  {
                    flowArrayAdapter.remove(flow);
                }
                mFlowManager.loadFlows();
                flowArrayAdapter.addAll(mFlowManager.getFlows());
                flowArrayAdapter.notifyDataSetInvalidated();
                return true;
            case R.id.pact_add_flow:
                int new_flow_ii = mFlowManager.getNewFlow();
                Flow newFlow = mFlowManager.getFlow(new_flow_ii);
                flowArrayAdapter.add(newFlow);
                startEditFlowActivity(new_flow_ii);
                return true;
            case R.id.pact_trigger_next_calendar_context:
                mFlowManager.triggerNextCalendarContext();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void startEditFlowActivity(int flow_ii) {
        Intent intent = new Intent(FlowManagerActivity.this, FlowActivity.class);
        intent.putExtra("flow_ii", flow_ii);
        startActivity(intent);
    }

    int mSelectedFlowIndex = -1;
    private AdapterView.OnItemClickListener flowItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int flow_ii, long l) {
            mSelectedFlowIndex = flow_ii;
            if (mActionMode == null) {
                mActionMode = startActionMode(mFlowSelectedActionCallback);
            }
        }
    };

    ActionMode mActionMode = null;
    ActionMode.Callback mFlowSelectedActionCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
            actionMode.getMenuInflater().inflate(R.menu.flow_actbar, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
            switch(menuItem.getItemId()) {
                case R.id.flow_actbar_remove_flow:
                    Flow remFlow = mFlowManager.getFlow(mSelectedFlowIndex);
                    mFlowManager.removeFlow(mSelectedFlowIndex);
                    flowArrayAdapter.remove(remFlow);

                    mActionMode = null;
                    return true;

                case R.id.flow_actbar_edit_flow:
                    startEditFlowActivity(mSelectedFlowIndex);
                    actionMode.finish();
                    mActionMode = null;
                    return true;
            }
            actionMode.finish();
            mActionMode = null;
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode actionMode) {
            //To change body of implemented methods use File | Settings | File Templates.
        }
    };

    class FlowArrayAdapter extends ArrayAdapter<Flow> {
        public FlowArrayAdapter() {
            super(FlowManagerActivity.this, R.layout.flow_item_view);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.flow_item_view, null);
            TextView textView = (TextView)view.findViewById(R.id.text_view);
            String flowName = getItem(position).getName();
            if (flowName.isEmpty())
                flowName = getString(R.string.unnamed_flow);
            textView.setText(flowName);
            return view;
        }
    }


}
