package com.digitalsolution.waterdispenser;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
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
    private WifiConfiguration conf;
    private String ssId;
    private String mPassword;
    private WifiManager wifiManager;
    private AlertDialog alertDialog;
    private Dialog alertDialogBuilder;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        if (intent.getStringExtra("SSID") != null && intent.getStringExtra("Password") != null) {
            ssId = intent.getStringExtra("SSID");
            mPassword = intent.getStringExtra("Password");
        } else {
            Toast.makeText(this, "SSID and password can't be null.." + ssId + mPassword, Toast.LENGTH_SHORT).show();
        }
        intializeView();
    }

    private void intializeView() {
        textView = findViewById(R.id.inst_multi);
        textView.setText("1. Select the type [hot,normal,cold].\n2. Select the quantity of water.\n3. Click on dispense button.");
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
        dispense.setOnClickListener((View.OnClickListener) this);
        connectToWIFI();
    }

    private void AlertDialog() {
        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Ready to Dispense");
        alertDialog.setMessage("00:10");
        alertDialog.getWindow().setLayout(2000, 2000);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.GRAY));
        alertDialog.show();
        new CountDownTimer(11000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                alertDialog.setMessage("" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                try {
                    // SendCommandTOAppliance(mTypeWater, mQTYWater);
                    final Handler handler = new Handler();
                    final Runnable runnable = new Runnable() {
                        @Override
                        public void run() {
                            loadPopUpGIF();
                        }
                    };
                    handler.postDelayed(runnable, 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
    }

    private void displayExitCommand() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setTitle("WATER DISPENSE COMPLETED");
        alertDialogBuilder
                .setMessage("If you want more water please connect again..Thank You!!!!")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (wifiManager == null) {
                            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        }
                        if (wifiManager != null && wifiManager.isWifiEnabled()) {
                            wifiManager.setWifiEnabled(false);
                            Intent intent1 = new Intent(MainActivity.this, ScanQRCode.class);
                            startActivity(intent1);
                        }
                    }
                }).show();
    }

    private void loadPopUpGIF() {
        alertDialogBuilder = new Dialog(
                this);
        alertDialogBuilder.setContentView(R.layout.alert_box);
        Glide.with(this).load(R.drawable.water_dispense_new)
                .into((ImageView) alertDialogBuilder.findViewById(R.id.drawable_conn));
        if (!alertDialogBuilder.isShowing()) {
            alertDialogBuilder.show();
        }
        final Handler handler = new Handler();
        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (alertDialogBuilder.isShowing()) {
                    alertDialogBuilder.dismiss();
                }
                displayExitCommand();
            }
        };
        handler.postDelayed(runnable, 10000);

    }

    private void connectToWIFI() {
        Toast.makeText(this, "Started.." + ssId + mPassword, Toast.LENGTH_SHORT).show();
        conf = new WifiConfiguration();
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        Toast.makeText(this, "Started scanning bb.." + wifiManager.getConnectionInfo(), Toast.LENGTH_SHORT).show();

        Toast.makeText(this, "WIFI enabled.." + wifiManager.isWifiEnabled(), Toast.LENGTH_SHORT).show();

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (ssId != null && mPassword != null) {
                    ConnectPhoneWIFIToHotspot(wifiManager, mPassword, ssId);
                }
            }
        }).start();
    }

    public void ConnectPhoneWIFIToHotspot(WifiManager wifiManager, String password, String selectedWifiNetwork) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        if (wifiManager != null) {
            if (!wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(true);
            }
            wifiManager.disconnect();
            if (selectedWifiNetwork != null) {
                WifiConfiguration config = getProfileForConnection(selectedWifiNetwork, password, wifiManager);
                if (config != null) {
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(config.networkId, true);
                    wifiManager.reconnect();
                    wifiManager.enableNetwork(config.networkId, true);
                }
            }
        }
    }

    public WifiConfiguration getProfileForConnection(String macAddress, String password, WifiManager wifiManager) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        }
        if (wifiManager != null) {
            if (wifiManager.getConfiguredNetworks() != null) {
                for (WifiConfiguration wifi : wifiManager.getConfiguredNetworks()) {
                    if (wifi.SSID != null && removeSSidQuotes(wifi.SSID.toUpperCase()).contains(macAddress))
                        return wifi;
                }
            }
        }
        return createPhoneProfileInWIFIConf(macAddress, password, wifiManager);
    }

    public WifiConfiguration createPhoneProfileInWIFIConf(String ssid, String password, WifiManager wifiManager) {
        wifiManager = initWifiManager(wifiManager);
        WifiConfiguration wfc = getProfileToRemove(ssid, wifiManager);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        boolean isNewProfile = false;
        if (wfc == null) {
            wfc = new WifiConfiguration();
            isNewProfile = true;
            wfc.SSID = "\"".concat(ssid).concat("\"");
            wfc.status = WifiConfiguration.Status.ENABLED;
            int highest_int = 0;
            if (list != null) {
                for (WifiConfiguration i : list) {
                    if (i.priority > highest_int) {
                        highest_int = i.priority;
                    }
                }
                wfc.priority = highest_int + 1;
            } else {
                wfc.priority = 40;
            }
            wfc.status = WifiConfiguration.Status.ENABLED;
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        }
        wfc.preSharedKey = "\"".concat(password).concat("\"");
        if (wifiManager != null) {
            if (isNewProfile) {
                wifiManager.addNetwork(wfc);
            } else {
                wifiManager.updateNetwork(wfc);
            }
        }
        return wfc;
    }


    public WifiConfiguration getProfileToRemove(String macAddress, WifiManager wifiManager) {
        wifiManager = initWifiManager(wifiManager);
        if (!TextUtils.isEmpty(macAddress) && wifiManager != null) {
            if (wifiManager.getConfiguredNetworks() != null && !wifiManager.getConfiguredNetworks().isEmpty()) {
                for (WifiConfiguration wifi : wifiManager.getConfiguredNetworks()) {
                    if (wifi != null && wifi.SSID != null) {
                        if (wifi.SSID.contains(macAddress)) {
                            return wifi;
                        }
                    }
                }
            }
        }
        return null;
    }

    public String removeSSidQuotes(String ssid) {
        return ssid.replace('"', ' ').trim();
    }


    private WifiManager initWifiManager(WifiManager wifiManager) {
        if (wifiManager == null) {
            wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return wifiManager;
        } else {
            return wifiManager;
        }
    }

    public void onClick(View v) {
        if (v.getId() == R.id.button_hot) {
            buttonHot.setEnabled(true);
            mTypeWater = buttonHot.getText().toString();
        } else if (v.getId() == R.id.button_normal) {
            buttonNormal.setEnabled(true);
            mTypeWater = buttonNormal.getText().toString();
        } else if (v.getId() == R.id.button_cold) {
            buttonCold.setEnabled(true);
            mTypeWater = buttonCold.getText().toString();
        } else if (v.getId() == R.id.button_250) {
            button250.setEnabled(true);
            mQTYWater = button250.getText().toString();
        } else if (v.getId() == R.id.button_500) {
            button500.setEnabled(true);
            mQTYWater = button500.getText().toString();
        } else if (v.getId() == R.id.button_750) {
            button750.setEnabled(true);
            mQTYWater = button750.getText().toString();
        } else if (v.getId() == R.id.button_on) {

            if (mQTYWater != null && mTypeWater == null) {
                Toast.makeText(this, "Please select water type.", Toast.LENGTH_SHORT).show();
            } else if (mQTYWater == null && mTypeWater != null) {
                Toast.makeText(this, "Please select  water quantity..", Toast.LENGTH_SHORT).show();
            } else if (mQTYWater != null && mTypeWater != null) {
                if (wifiManager == null) {
                    wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                }
                if (wifiManager.isWifiEnabled()) {
                    dispense.setBackgroundColor(getResources().getColor(R.color.green));
                    AlertDialog();
                } else {
                    Toast.makeText(this, "Your WIFI connection lost.Let me try to connect again!!!", Toast.LENGTH_SHORT).show();
                    if (ssId != null && mPassword != null && wifiManager != null) {
                        Toast.makeText(this, "We have initiate the connection please, wait for second!!!!!", Toast.LENGTH_SHORT).show();
                        ConnectPhoneWIFIToHotspot(wifiManager, mPassword, ssId);
                    } else {
                        Toast.makeText(this, "Your QR scan not done properly, please try again!!", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                Toast.makeText(this, "Please select water type and quantity to proceed.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void SendCommandTOAppliance(String type, String qty) throws UnsupportedEncodingException {

        //http://192.168.168.110:8080/dispense?type=hot&qty=250
        // http://192.168.168.110:80/dispense?type=HOT&qty=250ML

        String data = URLEncoder.encode("cycle", "UTF-8")
                + "=" + URLEncoder.encode("CycleID", "UTF-8");
        String wifiIP = "192.168.168.110:80";
        String path = "type=" + type + "&qty=" + qty;
        try {
            // Defined URL  where to send data
            URL url = new URL("http://" + wifiIP + "/dispense?" + path);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "text/plain");
            conn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
            conn.setConnectTimeout(5000);
            OutputStream os = conn.getOutputStream();
            os.write(data.getBytes());
            os.flush();
            os.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}



