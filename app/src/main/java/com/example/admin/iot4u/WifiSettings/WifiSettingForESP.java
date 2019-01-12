package com.example.admin.iot4u.WifiSettings;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.admin.iot4u.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

//Not use
public class WifiSettingForESP extends Activity implements View.OnClickListener {
    private Button btnScan;
    private ListView lvWifi;

    private WifiManager wifiManager;
    SimpleAdapter simpleAdapter;

    ArrayList<HashMap<String,String>> arrayList = new ArrayList<HashMap<String,String>>();
    String ITEM_KEY = "key";

    List<ScanResult> results;

    int size = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_setting_for_esp);
        btnScan = findViewById(R.id.btnScanWifi);
        lvWifi = findViewById(R.id.lvWifiList);

        btnScan.setOnClickListener(this);

        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Toast.makeText(this, "MAC00 " + wifiManager.getConnectionInfo().getMacAddress().toString(), Toast.LENGTH_SHORT).show();
        Toast.makeText(this, "MAC01 " + wifiManager.getConnectionInfo().getBSSID().toString(), Toast.LENGTH_SHORT).show();
//        Log.d("MACADDRESS00: ", wifiManager.getConnectionInfo().getMacAddress().toString());
//        Log.d("MACADDRESS01: ", wifiManager.getConnectionInfo().getBSSID().toString());

        if(wifiManager.isWifiEnabled()==false) {
            Toast.makeText(this, "Wifi is disable, making it enable", Toast.LENGTH_SHORT).show();
            wifiManager.setWifiEnabled(true);
        }

        this.simpleAdapter = new SimpleAdapter(WifiSettingForESP.this,arrayList,R.layout.wifi_row,
                new String[] {ITEM_KEY}, new int[] {R.id.tvWifiRow}  );

        lvWifi.setAdapter(this.simpleAdapter);

//        this.registerReceiver(new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//                results = wifiManager.getScanResults();
//                size = results.size();
//
//            }
//        } ,new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context c, Intent intent) {
                if (intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                    List<ScanResult> mScanResults = wifiManager.getScanResults();
                    // add your logic here
                    Toast.makeText(c, "SIZE: " + mScanResults.size(), Toast.LENGTH_SHORT).show();

                }
                boolean success = intent.getBooleanExtra(
                        WifiManager.EXTRA_RESULTS_UPDATED, false);
                if (success) {
                    scanSuccess();
                } else {
                    // scan failure handling
                    scanFailure();
                }
            }


        };

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        this.registerReceiver(wifiScanReceiver, intentFilter);

        boolean success = wifiManager.startScan();
        if (!success) {
            // scan failure handling
            scanFailure();
        }





    }

    private void scanSuccess() {
        results = wifiManager.getScanResults();
        Toast.makeText(this, "SUCCESSS", Toast.LENGTH_SHORT).show();
        size = results.size();

    }

    private void scanFailure() {
        results = wifiManager.getScanResults();
        Toast.makeText(this, "SCAN FAILED", Toast.LENGTH_SHORT).show();

    }
    @Override
    public void onClick(View v) {

        arrayList.clear();
        wifiManager.startScan();

        Toast.makeText(this, "Scanning...." + size, Toast.LENGTH_SHORT).show();
        try
        {
            size = size - 1;
            while (size >= 0)
            {
                HashMap<String, String> item = new HashMap<String, String>();
                item.put(ITEM_KEY, results.get(size).SSID + "  " + results.get(size).capabilities);

                arrayList.add(item);
                size--;
                simpleAdapter.notifyDataSetChanged();
            }
        }
        catch (Exception e)
        { }


    }
}
