package com.example.admin.iot4u.MQTT;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

    static final String LOG_TAG = ControlDevice.class.getCanonicalName();

    // --- Constants to modify per your configuration ---
    // Tim-IoT4U-N.Virginia
    // IoT endpoint
    // AWS Iot CLI describe-endpoint call returns: XXXXXXXXXX.iot.<region>.amazonaws.com
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "a2qe5byjpkn5pd-ats.iot.us-east-1.amazonaws.com";
    // Cognito pool ID. For this app, pool needs to be unauthenticated pool with
    // AWS IoT permissions.
    private static final String COGNITO_POOL_ID = "us-east-1:13de72b0-f65b-4128-abdb-d121462ba4b3";
    // Name of the AWS IoT policy to attach to a newly created certificate
    private static final String AWS_IOT_POLICY_NAME = "Policy_IoT4U";

    // Region of AWS IoT
    private static final Regions MY_REGION = Regions.US_EAST_1;
    // Filename of KeyStore file on the filesystem
    private static final String KEYSTORE_NAME = "AKIAJWZCBSFWJC65D4LA";
    // Password for the private key in the KeyStore
    private static final String KEYSTORE_PASSWORD = "BSocLIfz4Y059Gan4S6K6mUV84+KgNmzy2D1+DiH";
    // Certificate and key aliases in the KeyStore
    private static final String CERTIFICATE_ID = "2617167b00102ff961c4cd276789c70334c028b0d1e038cf4ba9135500068d07";
    private Button btnON;
    private Button btnOFF;
    private Button btnRED;
    private Button btnGREEN;
    private Button btnBLUE;
    private ImageButton imgBtnOnOff;

    private AWSIotClient mIotAndroidClient;
    private AWSIotMqttManager mqttManager;
    private String clientId;
    private String keystorePath;
    private String keystoreName;
    private String keystorePassword;

    private KeyStore clientKeyStore = null;
    private String certificateId;

    private String messageAWS;
    private String topicPubAWS;
    private String topicSubAWS;

    private TextView tvTest;

    boolean onStatus = true;

    CognitoCachingCredentialsProvider credentialsProvider;

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

        topicPubAWS = udid+"/A2E";
        topicSubAWS = udid+"/E2A";

        // MQTT client IDs are required to be unique per AWS IoT account.
        // This UUID is "practically unique" but does not _guarantee_
        // uniqueness.
        //clientId = UUID.randomUUID().toString();
        clientId = udid;
        tvTest.setText(udid);

        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );

        Region region = Region.getRegion(MY_REGION);

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        // Set keepalive to 10 seconds.  Will recognize disconnects more quickly but will also send
        // MQTT pings every 10 seconds.
        mqttManager.setKeepAlive(10);

        // Set Last Will and Testament for MQTT.  On an unclean disconnect (loss of connection)
        // AWS IoT will publish this message to alert other clients.
        AWSIotMqttLastWillAndTestament lwt = new AWSIotMqttLastWillAndTestament("my/lwt/topic",
                "Android client lost connection", AWSIotMqttQos.QOS0);
        mqttManager.setMqttLastWillAndTestament(lwt);

        // IoT Client (for creation of certificate if needed)
        mIotAndroidClient = new AWSIotClient(credentialsProvider);
        mIotAndroidClient.setRegion(region);

        keystorePath = getFilesDir().getPath();
        keystoreName = KEYSTORE_NAME;
        keystorePassword = KEYSTORE_PASSWORD;
        certificateId = CERTIFICATE_ID;

        // To load cert/key from keystore on filesystem
        try {
            if (AWSIotKeystoreHelper.isKeystorePresent(keystorePath, keystoreName)) {
                if (AWSIotKeystoreHelper.keystoreContainsAlias(certificateId, keystorePath,
                        keystoreName, keystorePassword)) {
                    Log.i(LOG_TAG, "Certificate " + certificateId
                            + " found in keystore - using for MQTT.");
                    // load keystore from file into memory to pass on connection
                    clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                            keystorePath, keystoreName, keystorePassword);
                    //btnConnect.setEnabled(true);
                    //Dong add to auto connect AWS service
                    connectToAWS();

                } else {
                    Log.i(LOG_TAG, "Key/cert " + certificateId + " not found in keystore.");
                }
            } else {
                Log.i(LOG_TAG, "Keystore " + keystorePath + "/" + keystoreName + " not found.");
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "An error occurred retrieving cert/key from keystore.", e);
        }

        if (clientKeyStore == null) {
            Log.i(LOG_TAG, "Cert/key was not found in keystore - creating new key and certificate.");

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // Create a new private key and certificate. This call
                        // creates both on the server and returns them to the
                        // device.
                        CreateKeysAndCertificateRequest createKeysAndCertificateRequest =
                                new CreateKeysAndCertificateRequest();
                        createKeysAndCertificateRequest.setSetAsActive(true);
                        final CreateKeysAndCertificateResult createKeysAndCertificateResult;
                        createKeysAndCertificateResult =
                                mIotAndroidClient.createKeysAndCertificate(createKeysAndCertificateRequest);
                        Log.i(LOG_TAG,
                                "Cert ID: " +
                                        createKeysAndCertificateResult.getCertificateId() +
                                        " created.");

                        // store in keystore for use in MQTT client
                        // saved as alias "default" so a new certificate isn't
                        // generated each run of this application
                        AWSIotKeystoreHelper.saveCertificateAndPrivateKey(certificateId,
                                createKeysAndCertificateResult.getCertificatePem(),
                                createKeysAndCertificateResult.getKeyPair().getPrivateKey(),
                                keystorePath, keystoreName, keystorePassword);

                        // load keystore from file into memory to pass on
                        // connection
                        clientKeyStore = AWSIotKeystoreHelper.getIotKeystore(certificateId,
                                keystorePath, keystoreName, keystorePassword);

                        // Attach a policy to the newly created certificate.
                        // This flow assumes the policy was already created in
                        // AWS IoT and we are now just attaching it to the
                        // certificate.
                        AttachPrincipalPolicyRequest policyAttachRequest =
                                new AttachPrincipalPolicyRequest();
                        policyAttachRequest.setPolicyName(AWS_IOT_POLICY_NAME);
                        policyAttachRequest.setPrincipal(createKeysAndCertificateResult
                                .getCertificateArn());
                        mIotAndroidClient.attachPrincipalPolicy(policyAttachRequest);
                        // Connect to AWS
                        connectToAWS();
                    } catch (Exception e) {
                        Log.e(LOG_TAG,
                                "Exception occurred when generating new private key and certificate.", e);
                    }
                }
            }).start();
        }
        // Subscribe
        subscribeAWSTopic();
    }

    private void connectToAWS() {
        Log.d(LOG_TAG, "clientId = " + clientId);
        try {
            mqttManager.connect(clientKeyStore, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status,
                                            final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == AWSIotMqttClientStatus.Connecting) {
                                //Toast.makeText(ControlDevicePubSubActivity.this, "Connecting...", Toast.LENGTH_SHORT).show();
                                tvTest.setText("Connecting...");


                            } else if (status == AWSIotMqttClientStatus.Connected) {
                                //Toast.makeText(ControlDevicePubSubActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                                tvTest.setText("Connected");
                                //subscribeAWSTopic();

                            } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                Toast.makeText(ControlDevicePubSubActivity.this, "Reconnecting...", Toast.LENGTH_SHORT).show();
                            } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                Toast.makeText(ControlDevicePubSubActivity.this, "Disconnected...", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ControlDevicePubSubActivity.this, "Disconnected...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (final Exception e) {
            Log.e(LOG_TAG, "Connection error.", e);
            Toast.makeText(this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void subscribeAWSTopic() {
        Log.d(LOG_TAG, "topic = " + topicSubAWS);
        try {
            mqttManager.subscribeToTopic(topicSubAWS, AWSIotMqttQos.QOS0,
                    new AWSIotMqttNewMessageCallback() {
                        @Override
                        public void onMessageArrived(final String topicSubAWS, final byte[] data) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String message = new String(data, "UTF-8");
                                        Log.d(LOG_TAG, "Message arrived:");
                                        Log.d(LOG_TAG, "   Topic: " + topicSubAWS);
                                        Log.d(LOG_TAG, " Message: " + message);

                                        //Toast.makeText(ControlDevicePubSubActivity.this, message, Toast.LENGTH_SHORT).show();
                                        tvTest.setText(message.toString());

                                    } catch (UnsupportedEncodingException e) {
                                        Log.e(LOG_TAG, "Message encoding error.", e);
                                    }
                                }
                            });
                        }
                    });
        } catch (Exception e) {
            Log.e(LOG_TAG, "Subscription error.", e);
        }
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
            mqttManager.publishString(messageAWS, topicPubAWS, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }

    }
}