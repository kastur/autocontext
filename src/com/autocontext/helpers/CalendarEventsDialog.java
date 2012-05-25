package com.autocontext.helpers;

import android.app.Dialog;
import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.autocontext.R;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: k
 * Date: 5/25/12
 * Time: 3:29 AM
 * To change this template use File | Settings | File Templates.
 */
public class CalendarEventsDialog {
    private Context mContext;

    public CalendarEventsDialog(Context context) {
        mContext = context;
        listArrayAdapter = new ArrayAdapter<String>(mContext, R.layout.flow_item_view, R.id.text_view);
    }

    public void setEvents(ArrayList<String> events) {
        listArrayAdapter.addAll(events);
    }

    public void showDialog() {
        Dialog dialog = new Dialog(mContext);

        dialog.setContentView(R.layout.calendar_events_dialog);
        dialog.setTitle("Matching events");

        ListView listView = (ListView)dialog.findViewById(R.id.calendar_events_list);
        listView.setAdapter(listArrayAdapter);
        dialog.show();

    }

    private ArrayAdapter<String> listArrayAdapter;
}
