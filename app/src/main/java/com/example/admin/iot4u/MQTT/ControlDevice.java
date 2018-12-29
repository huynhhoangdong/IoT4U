package com.example.admin.iot4u.MQTT;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

//import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
//import com.amazonaws.regions.Region;
//import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.example.admin.iot4u.R;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;
import java.util.UUID;

public class ControlDevice extends Activity implements View.OnClickListener {



    TextView tvNameDevice;
    TextView tvMacDevice;
    Button btnON;
    Button btnOFF;






    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_device);
        tvNameDevice = findViewById(R.id.tvControlDeviceName);
        tvMacDevice = findViewById(R.id.tvControlDeviceMac);
        btnON = findViewById(R.id.btnControlON);
        btnOFF = findViewById(R.id.btnControlOFF);


        Intent intent  = getIntent();
        String name = intent.getStringExtra("Name");
        String mac = intent.getStringExtra("Mac");

        tvNameDevice.setText(name);
        tvMacDevice.setText(mac);

        btnOFF.setOnClickListener(this);
        btnON.setOnClickListener(this);
        //---AWS IOT



        //---




    }

    @Override
    public void onClick(View v) {
        int i = v.getId();

        switch (i) {
            case R.id.btnControlON:
                Toast.makeText(v.getContext(), "Publish ON message", Toast.LENGTH_SHORT).show();
            case R.id.btnControlOFF:
                Toast.makeText(v.getContext(), "Publish OFF message", Toast.LENGTH_SHORT).show();

        }

    }
}
