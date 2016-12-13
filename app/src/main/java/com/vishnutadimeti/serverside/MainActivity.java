package com.vishnutadimeti.serverside;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    Server server;
    TextView infoip;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        infoip = (TextView) findViewById(R.id.infoip);
        server = new Server(this);
        infoip.setText(server.getIpAddress()+":"+server.getPort());

        // Test Code
        listView = (ListView) findViewById(R.id.list);
        List<String> datalist = new ArrayList<>();
        datalist.add("foo");
        datalist.add("bar");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_list_item_1,
                datalist);
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        server.onDestroy();
    }


}
