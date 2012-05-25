package com.autocontext;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;


public class FlowActivity extends BaseFlowActivity {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(this, FlowManagerActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void handleOnServiceConnected(Message msg) {
        super.handleOnServiceConnected(msg);

        final Activity activity = this;

        Bundle bundle = getIntent().getExtras();
        int flow_ii = bundle.getInt("flow_ii");
        Flow flow = mFlowManager.getFlow(flow_ii);
        View flowView = flow.getEditable(activity).getEditView();

        setContentView(flowView);
    }
}