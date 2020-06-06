package com.digitalsolution.waterdispenser;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRCode  extends AppCompatActivity {
    Button btnAction;
    private IntentIntegrator mScannerView;
    WifiManager wifiManager;
    WifiConfiguration conf;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        initViews();

    }

    private void initViews() {
        //   surfaceView = findViewById(R.id.surfaceView);
        btnAction = findViewById(R.id.btnAction);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // mScannerView = new IntentIntegrator(ScanQRCode.this);
               // mScannerView.setOrientationLocked(false);
               // mScannerView.setBeepEnabled(false);
               // mScannerView.initiateScan();
                connectToWIFI();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (resultCode == RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT");
                Toast.makeText(this, contents, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(ScanQRCode.this, MainActivity.class));
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Scanned failed !!!", Toast.LENGTH_SHORT).show();
               // connectToWIFI();
                startActivity(new Intent(ScanQRCode.this, MainActivity.class));
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void connectToWIFI() {
        Toast.makeText(this,"Started..",Toast.LENGTH_SHORT).show();
        conf = new WifiConfiguration();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }else{
            wifiManager.setWifiEnabled(true);
        }
        conf.SSID = String.format("\"%s\"", "Chetan");
        conf.preSharedKey = String.format("\"%s\"", "qwerty1234");

       // conf.SSID =  "\""+"Chetan"+"\"";
        //conf.preSharedKey = "\""+"qwerty123"+"\"";
        Toast.makeText(this,conf.SSID,Toast.LENGTH_SHORT).show();
        Toast.makeText(this,conf.preSharedKey,Toast.LENGTH_SHORT).show();

        conf.status = WifiConfiguration.Status.ENABLED;
        conf.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
        conf.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        conf.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        conf.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
        //updateNetwork(wifiManager, config);

        int networkId = wifiManager.addNetwork(conf);
       // Toast.makeText(this,conf.status,Toast.LENGTH_SHORT).show();
        wifiManager.disconnect();
       // Toast.makeText(this,networkId,Toast.LENGTH_SHORT).show();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
        wifiManager.saveConfiguration();
       // Toast.makeText(this,conf.status,Toast.LENGTH_SHORT).show();
    }

}




