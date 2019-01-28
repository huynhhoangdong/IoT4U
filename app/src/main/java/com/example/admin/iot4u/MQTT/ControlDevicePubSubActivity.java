package com.example.admin.iot4u.MQTT;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotKeystoreHelper;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttLastWillAndTestament;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.iot.AWSIotClient;
import com.amazonaws.services.iot.model.AttachPrincipalPolicyRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateRequest;
import com.amazonaws.services.iot.model.CreateKeysAndCertificateResult;
import com.example.admin.iot4u.R;

import java.io.UnsupportedEncodingException;
import java.security.KeyStore;


public class ControlDevicePubSubActivity extends AppCompatActivity implements View.OnClickListener{

    static final String LOG_TAG = ControlDevicePubSub.class.getCanonicalName();

    private Button btnON;
    private Button btnOFF;
    private Button btnRED;
    private Button btnGREEN;
    private Button btnBLUE;
    private ImageButton imgBtnOnOff;

    private String messageAWS;

    private TextView tvTest;

    boolean onStatus = true;

    private ControlDevicePubSub pubSubAWS;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.control_device_pubsub);

        btnON = findViewById(R.id.btnON);
        btnON.setOnClickListener(this);
        btnOFF = findViewById(R.id.btnOFF);
        btnOFF.setOnClickListener(this);

        btnRED = findViewById(R.id.btnRED);
        btnRED.setOnClickListener(this);

        btnGREEN = findViewById(R.id.btnGREEN);
        btnGREEN.setOnClickListener(this);

        btnBLUE = findViewById(R.id.btnBLUE);
        btnBLUE.setOnClickListener(this);

        imgBtnOnOff = findViewById(R.id.imgBtnOnOff);
        imgBtnOnOff.setOnClickListener(this);

        tvTest = findViewById(R.id.tvTest);

        //Get Data from DeviceListAdapter

        Intent intent = getIntent();
        String udid = intent.getStringExtra("UDID");

        pubSubAWS = new ControlDevicePubSub(this,udid);
        pubSubAWS.initialAWS();

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnON:
                messageAWS = "{\"FUNCTION\":\"ON\"}";
                imgBtnOnOff.setImageResource(R.drawable.power_off_button);
                break;
            case R.id.btnOFF:
                messageAWS = "{\"FUNCTION\":\"OFF\"}";
                imgBtnOnOff.setImageResource(R.drawable.power_on_button);
                break;
            case R.id.btnRED:
                messageAWS = "{\"FUNCTION\":\"RED\"}";
                break;
            case R.id.btnGREEN:
                messageAWS = "{\"FUNCTION\":\"GREEN\"}";
                break;
            case R.id.btnBLUE:
                messageAWS = "{\"FUNCTION\":\"BLUE\"}";
                break;

            case R.id.imgBtnOnOff:
                if(onStatus) {
                    messageAWS = "{\"FUNCTION\":\"ON\"}";
                    imgBtnOnOff.setImageResource(R.drawable.power_off_button);
                    //Toast.makeText(this, "Image Click ON", Toast.LENGTH_SHORT).show();
                    onStatus = false;
                } else {
                    messageAWS = "{\"FUNCTION\":\"OFF\"}";
                    imgBtnOnOff.setImageResource(R.drawable.power_on_button);
                    //Toast.makeText(this, "Image Click OFF", Toast.LENGTH_SHORT).show();
                    onStatus = true;
                }
                break;
        }

        try {
            pubSubAWS.publishAWS(messageAWS);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }

    }
}
