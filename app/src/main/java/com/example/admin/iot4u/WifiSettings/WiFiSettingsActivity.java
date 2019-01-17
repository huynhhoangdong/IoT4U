package com.example.admin.iot4u.WifiSettings;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.iot4u.Database.DeviceInforDatabase;
import com.example.admin.iot4u.Database.DeviceInfor;
import com.example.admin.iot4u.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WiFiSettingsActivity extends Activity {

    private Button btnSend;
    private Button btnGetWifiList;
    //TextView ssid;
    private TextView pass;
    private TextView wifiListShow;
    private TextView testResult;
    private Spinner spnWifiSSID;

    private String dataOut;
    private String dataIn;
    private JSONObject configInfo, getWifiList, readJson;
    private JSONArray wifiFromESP;
    private List<String> wifiList;

    private String udid;
    private String cmd;
    private String result;
    private String mac;

    DeviceInforDatabase dbDeviceInfor;
    DeviceInfor deviceInfor;

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_setting);

        btnSend = findViewById(R.id.btnSend);
        btnGetWifiList = findViewById(R.id.btnGetWifiList);
        //ssid = findViewById(R.id.edtWifiSSID);
        pass = findViewById(R.id.edtWifiPASS);
        wifiListShow = findViewById(R.id.tvShow);
        spnWifiSSID = findViewById(R.id.spnWifiSSID);

        configInfo = new JSONObject();
        getWifiList = new JSONObject();

        wifiList = new ArrayList<>();

        dbDeviceInfor = new DeviceInforDatabase(this);

        udid = UUID.randomUUID().toString();

        //ssid.setText("AURIO-TECH ROOM");
        pass.setText("0922222870");

        btnGetWifiList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    getWifiList.put("CMD", "GET_WIFI_LIST");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                dataOut = getWifiList + ".";
                Thread socketClientThead;
                socketClientThead = new Thread(new SocketClientThread());
                socketClientThead.start();
                try {
                    socketClientThead.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                try {
                    readJson = new JSONObject(dataIn);
                    cmd = readJson.getString("CMD");
                    if (cmd.equals("WIFI_LIST")) {
                        //Toast.makeText(WiFiSettingsActivity.this, "WIFI_LIST", Toast.LENGTH_SHORT).show();
                        Log.d("WIFI_LIST", "RECEIVE WIFI LIST");
                        mac = readJson.getString("MAC");
                        wifiFromESP = readJson.getJSONArray("WIFI");
                        wifiList.clear();
                        for (int i = 0; i < wifiFromESP.length(); i++) {
                            String wifiItem = wifiFromESP.getString(i);
                            wifiList.add(wifiItem);


                        }
                        for (String s : wifiList) {
                            Log.d("WIFI", s);
                        }

                        ArrayAdapter<String> adapter = new ArrayAdapter(WiFiSettingsActivity.this, android.R.layout.simple_spinner_item, wifiList);
                        adapter.setDropDownViewResource(android.R.layout.simple_list_item_single_choice);
                        spnWifiSSID.setAdapter(adapter);

                        if (wifiList.size() != 0) {
                            spnWifiSSID.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                                                      @Override
                                                                      public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                                                          //Toast.makeText(WiFiSettingsActivity.this, spnWifiSSID.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                                                                          Log.d("WIFI SELECTION", spnWifiSSID.getSelectedItem().toString());
                                                                      }

                                                                      @Override
                                                                      public void onNothingSelected(AdapterView<?> parent) {
                                                                      }
                                                                  }
                            );
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    configInfo.put("CMD", "CONFIG_INFO");
                    configInfo.put("UDID", udid);
                    configInfo.put("SSID", spnWifiSSID.getSelectedItem().toString());
                    configInfo.put("PASS", pass.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Initial thread variable
                dataOut = configInfo + ".";
                Thread socketClientThead;
                socketClientThead = new Thread(new SocketClientThread());
                socketClientThead.start();
                try {
                    socketClientThead.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    readJson = new JSONObject(dataIn);
                    cmd = readJson.getString("CMD");
                    if (cmd.equals("WIFI_CONNECTION_RESULT")) {
                        result = readJson.getString("RESULT");
                            if (result.equals("OK")) {
                            Toast.makeText(WiFiSettingsActivity.this, "WIFI OK", Toast.LENGTH_SHORT).show();
                            deviceInfor = new DeviceInfor("IOT4U", mac, udid);
                            ArrayList<DeviceInfor> listDevice = (ArrayList<DeviceInfor>) DeviceInforDatabase.getInstance(WiFiSettingsActivity.this).getAllDevice();
                            int listDeviceSize = listDevice.size();

                            // Remove the duplicated device
                            for (int i=0; i<listDeviceSize;i++){
                                if(mac.equals(listDevice.get(i).getDeviceMac())){
                                    DeviceInforDatabase.getInstance(WiFiSettingsActivity.this).deleteDevice(listDevice.get(i));
                                    Toast.makeText(WiFiSettingsActivity.this, "Remove " + listDevice.get(i).getDeviceMac(), Toast.LENGTH_SHORT).show();
                                }
                            }

                            // Add new device
                            DeviceInforDatabase.getInstance(WiFiSettingsActivity.this).addDevice(deviceInfor);

                        } else if (result.equals("FAILED")) {
                            Toast.makeText(WiFiSettingsActivity.this, "WIFI FAILED", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(WiFiSettingsActivity.this, "UNKNOW RESULT", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private class SocketClientThread implements Runnable {
        DataInputStream dataInputStream;
        DataOutputStream dataOutputStream;
        Socket socket;

        @Override
        public void run() {
            try {
                InetAddress serverAdrr = InetAddress.getByName("192.168.4.1");
                socket = new Socket(serverAdrr, 80);
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());

                // Send out data to Stream
                dataOutputStream.writeBytes(dataOut);

                //dataOutputStream.writeUTF(dataOut);
                Log.d("Dong", dataOut);
                dataOutputStream.flush();

                //Receiver data from Stream
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                dataIn = bufferedReader.readLine();
                //dataIn = dataInputStream.readUTF();
                Log.d("Dong", dataIn);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wifiListShow.setText(dataIn);
                        //Toast.makeText(WiFiSettingsActivity.this, dataIn, Toast.LENGTH_SHORT).show();
                        Log.d("DATA-IN", dataIn);
                    }
                });

            } catch (UnknownHostException ignore) {

            } catch (IOException ignore) {

            } catch (RuntimeException ignore) {

            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException ignore) {

                    }
                }
            }
        }
    }
}
