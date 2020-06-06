package com.digitalsolution.waterdispenser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static String wifiIP;
    private TextView textView;
    private Button buttonHot;
    private Button buttonNormal;
    private Button buttonCold;
    private Button button250;
    private Button button500;
    private Button button750;
    private Button dispense;
    private String mTypeWater;
    private String mQTYWater;
    WifiManager wifiManager;
    WifiConfiguration conf;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intializeView();
        connectToWIFI();

    }

    private void intializeView(){
        textView = findViewById(R.id.inst_multi);
        textView.setText("1. Select the type [hot,normal,cold].\n\n2. Select the quantity of water.\n\n3. Click on dispense button.");
        buttonHot = findViewById(R.id.button_hot);
        buttonNormal = findViewById(R.id.button_normal);
        buttonCold = findViewById(R.id.button_cold);
        button250 = findViewById(R.id.button_250);
        button500 = findViewById(R.id.button_500);
        button750 = findViewById(R.id.button_750);
        dispense = findViewById(R.id.button_on);

        buttonHot.setOnClickListener((View.OnClickListener) this);
        buttonNormal.setOnClickListener((View.OnClickListener) this);
        buttonCold.setOnClickListener((View.OnClickListener) this);
        button250.setOnClickListener((View.OnClickListener) this);
        button500.setOnClickListener((View.OnClickListener) this);
        button750.setOnClickListener((View.OnClickListener) this);
        dispense.setOnClickListener((View.OnClickListener)this);

    }

    private void connectToWIFI() {
        Toast.makeText(this,"Started..",Toast.LENGTH_SHORT).show();
        conf = new WifiConfiguration();
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (!wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(true);
        }
        //conf.SSID = String.format("\"%s\"", "Jitu");
        // conf.preSharedKey = String.format("\"%s\"", "Jiten374");

        conf.SSID =  "\""+"Chetan"+"\"";
        conf.preSharedKey = "\""+"qwerty123"+"\"";
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
        Toast.makeText(this,conf.status,Toast.LENGTH_SHORT).show();
        wifiManager.disconnect();
        Toast.makeText(this,networkId,Toast.LENGTH_SHORT).show();
        wifiManager.enableNetwork(networkId, true);
        wifiManager.reconnect();
        Toast.makeText(this,conf.status,Toast.LENGTH_SHORT).show();
    }
    public void onClick( View v ){
        if ( v.getId()== R.id.button_hot ) {
            buttonHot.setEnabled(true);
            mTypeWater= buttonHot.getText().toString();
           // buttonHot.setBackgroundColor(getResources().getColor(R.color.green));
            // do stuff
        }
        else if ( v.getId() == R.id.button_normal ) {
            buttonNormal.setEnabled(true);
            mTypeWater= buttonNormal.getText().toString();

           // buttonNormal.setBackgroundColor(getResources().getColor(R.color.green));
            // do stuff
        }
        else if( v.getId() == R.id.button_cold ) {
            buttonCold.setEnabled(true);
            mTypeWater= buttonCold.getText().toString();
          //  buttonCold.setBackgroundColor(getResources().getColor(R.color.green));
            // do stuff
        }
        else if ( v.getId() == R.id.button_250 ) {
            button250.setEnabled(true);
            mQTYWater = button250.getText().toString();
         //   button250.setBackgroundColor(getResources().getColor(R.color.green));
            // do stuff
        }
        else if ( v.getId() == R.id.button_500 ) {
            button500.setEnabled(true);
            mQTYWater = button500.getText().toString();
         //   button500.setBackgroundColor(getResources().getColor(R.color.green));
            // do stuff
        }
        else if( v.getId() == R.id.button_750 ) {
            button750.setEnabled(true);
            mQTYWater = button750.getText().toString();
         //   button750.setBackgroundColor(getResources().getColor(R.color.green));
            // do stuff
        }
        else if(v.getId() == R.id.button_on){
            dispense.setBackgroundColor(getResources().getColor(R.color.green));
            if(mQTYWater != null && mTypeWater != null){
                try {
                    SendCommandTOAppliance(mTypeWater,mQTYWater);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void SendCommandTOAppliance(String type, String qty) throws UnsupportedEncodingException {

        //http://192.168.168.110:8080/dispense?type=hot&qty=250

        String data = URLEncoder.encode("cycle", "UTF-8")
                + "=" + URLEncoder.encode("CycleID", "UTF-8");

        String wifiIP ="192.168.168.110:80";
        String path = "type="+type+"&qty="+qty;
        String text="";
        BufferedReader reader=null;
        byte[] responseBytes = null;
        try
        {
            // Defined URL  where to send data
            URL url = new URL("http://" + wifiIP + "/dispense?" +path);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/plain");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(5000);

            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            // Get the server response
            if (responseCode == HttpURLConnection.HTTP_OK) {
                int n;
                byte[] byteChunk = new byte[4096];
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                InputStream is = conn.getInputStream();
                while ( (n = is.read(byteChunk)) > 0 ) {
                    outputStream.write(byteChunk, 0, n);
                }
                responseBytes = outputStream.toByteArray();
                text = outputStream.toString();
            }

            // Get the server response
        } catch (MalformedURLException e) {
            text = e.toString();
        } catch (IOException e) {
            text = e.toString();
        } catch (Exception e) {
            text = e.toString();
        }
        // Show response on activity
        //content2.setText( text  );
    }

}



