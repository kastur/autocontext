package com.autocontext;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
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



    AdapterView.OnItemClickListener flowItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int item_ii, long l) {
            Flow flow = flowArrayAdapter.getItem(item_ii);
            Intent intent = new Intent(FlowManagerActivity.this, FlowActivity.class);
            intent.putExtra("flow_ii", item_ii);
            startActivity(intent);
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
