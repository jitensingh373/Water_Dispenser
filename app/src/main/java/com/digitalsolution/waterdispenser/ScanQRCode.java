package com.digitalsolution.waterdispenser;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ScanQRCode  extends AppCompatActivity {
    SurfaceView surfaceView;
    Button btnAction;
    private IntentIntegrator mScannerView;
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
                mScannerView = new IntentIntegrator(ScanQRCode.this);
                mScannerView.setOrientationLocked(false);
                mScannerView.setBeepEnabled(false);
                mScannerView.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode,resultCode,data);
        if(intentResult != null) {
                if (resultCode == RESULT_OK) {
                    String contents = data.getStringExtra("SCAN_RESULT");
                    Toast.makeText(this,contents,Toast.LENGTH_SHORT).show();
                    connectToWIFI();
                    startActivity(new Intent(ScanQRCode.this, MainActivity.class));
                }
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this,"Scanned failed !!!",Toast.LENGTH_SHORT).show();
                    connectToWIFI();
                   // startActivity(new Intent(ScanQRCode.this, MainActivity.class));
                }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void connectToWIFI(){
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = String.format("\"%s\"", "Chetan");
        conf.preSharedKey = String.format("\"%s\"", "qwerty123");
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        int networkId = conf.networkId;
        wifiManager.disableNetwork(networkId);
        wifiManager.disconnect();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
        }

       private void connectTOWIF(){
            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if(!wifiManager.isWifiEnabled())
            {
                wifiManager.setWifiEnabled(true);
            }
            Method[] wmMethods = wifiManager.getClass().getDeclaredMethods();   //Get all declared methods in WifiManager class
            boolean methodFound=false;
            for(Method method: wmMethods){
                if(method.getName().equals("-get0")){
                    methodFound=true;
                    WifiConfiguration netConfig = new WifiConfiguration();
                    netConfig.SSID = "\""+"Jitu"+"\"";
                    netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                    try {
                     //boolean apstatus=(Boolean) method.invoke(wifiManager, netConfig,true);
                        for (Method isWifiApEnabledmethod: wmMethods)
                        {
                            if(isWifiApEnabledmethod.getName().equals("Jitu")){
                                while(!(Boolean)isWifiApEnabledmethod.invoke(wifiManager)){
                                };
                                for(Method method1: wmMethods){
                                    if(method1.getName().equals("getWifiApState")){
                                        int apstate;
                                        apstate=(Integer)method1.invoke(wifiManager);
                                    }
                                }
                            }
                        }

                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

