package com.autocontext;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.*;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;


public class FlowActivity extends BaseFlowActivity {


    @Override
    public void handleOnServiceConnected(Message msg) {
        super.handleOnServiceConnected(msg);

        final Activity activity = this;

        Bundle bundle = getIntent().getExtras();
        int flow_ii = bundle.getInt("flow_ii");
        Flow flow = mFlowManager.getFlow(flow_ii);
        View flowView = flow.getEditable(activity).getEditView();

        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.root_layout);
        rootLayout.addView(flowView);
    }
}