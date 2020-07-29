package com.digitalsolution.waterdispenser;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class MainActivity extends MyBaseActivity implements View.OnClickListener {
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
    private boolean clickStopButton = true;
    private boolean clickStopFromGif = true;
    private Dialog alertDialogBuilder;
    private static final int BUILD_VERSION = 29;
    private int countValueEnd = 14000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        intializeView();
    }

    private void intializeView() {
        Intent intent = getIntent();
        if (intent.getStringExtra("SSID") != null && intent.getStringExtra("Password") != null) {
            ssId = intent.getStringExtra("SSID");
            mPassword = intent.getStringExtra("Password");
        }
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (BUILD_VERSION <= currentapiVersion) {
            Toast.makeText(this, "Connected to Whirlpool Dispenser !! ", Toast.LENGTH_SHORT).show();
        } else {
            connectToWIFI();
            Toast.makeText(this, "Connected to Whirlpool Dispenser !!  ", Toast.LENGTH_SHORT).show();
        }
        textView = findViewById(R.id.inst_multi);
        textView.setText("1. Turn off mobile Data.\n2. Select the type [hot,normal,cold].\n2. Select the quantity of water.\n3. Click on dispense button.");
        buttonHot = findViewById(R.id.button_hot);
        buttonNormal = findViewById(R.id.button_normal);
        buttonCold = findViewById(R.id.button_cold);
        button250 = findViewById(R.id.button_250);
        button500 = findViewById(R.id.button_500);
        button750 = findViewById(R.id.button_750);
        dispense = findViewById(R.id.button_on);

        buttonHot.setOnClickListener(this);
        buttonNormal.setOnClickListener(this);
        buttonCold.setOnClickListener(this);
        button250.setOnClickListener(this);
        button500.setOnClickListener(this);
        button750.setOnClickListener(this);
        dispense.setOnClickListener(this);
    }

    private void resetButtonAction() {
        mTypeWater = null;
        mQTYWater = null;
        Drawable drawable = getResources().getDrawable(R.drawable.gradient_normal_orange);
        dispense.setBackground(drawable);
        countValueEnd = countValueEnd + 0;
    }

    private void alertDialogFirstPopUp() {
        AlertDialog.Builder alertDialogBuilderTimer = new AlertDialog.Builder(this).setCancelable(false);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_box_timer, null);
        alertDialogBuilderTimer.setView(dialogView);
        final TextView tvCount = (TextView) dialogView.findViewById(R.id.textViewID);
        Button btnStop = (Button) dialogView.findViewById(R.id.btn_stop);
        alertDialog = alertDialogBuilderTimer.create();
        alertDialog.show();
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStopButton = false;
                Toast.makeText(MainActivity.this, "User Stopped, Resetting Selected Changes !!!", Toast.LENGTH_SHORT).show();
                resetButtonAction();
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
            }
        });
        new CountDownTimer(6000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tvCount.setText("" + (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                if (alertDialog != null) {
                    alertDialog.dismiss();
                }
                displayGIFPopUP();
            }

            private void displayGIFPopUP() {
                if (clickStopButton) {
                    try {
                        Thread thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    if (mTypeWater != null && mQTYWater != null) {
                                        char firstTypeLetterType = mTypeWater.charAt(0);
                                        String strQty = "0" + mQTYWater.substring(0, 3);
                                        SendCommandToAppliance(firstTypeLetterType, strQty);
                                        clickStopButton = true;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        thread.start();
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
        if (wifiManager != null && wifiManager.isWifiEnabled()) {
            wifiManager.setWifiEnabled(false);
        }
    }

    private void displayExitCommand() {
        if (clickStopFromGif) {
            final AlertDialog.Builder alertDialogBuilderTimer = new AlertDialog.Builder(this).setCancelable(false);
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_box_exit, null);
            alertDialogBuilderTimer.setView(dialogView);
            Button btnStop = dialogView.findViewById(R.id.btn_stop);
            alertDialog = alertDialogBuilderTimer.create();
            alertDialog.show();
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (wifiManager != null && wifiManager.isWifiEnabled()) {
                        wifiManager.setWifiEnabled(false);
                    }
                    if (alertDialog != null) {
                        alertDialog.dismiss();
                    }
                    int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                    if (BUILD_VERSION <= currentapiVersion) {
                        startActivity(new Intent(MainActivity.this, OpenWifiNetworkAdd.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, ScanQRCode.class));
                    }
                }
            });
        }
    }

    private void loadPopUpGIF() {
        alertDialogBuilder = new Dialog(
                this);
        alertDialogBuilder.setContentView(R.layout.alert_box);
        alertDialogBuilder.setCancelable(false);
        Glide.with(this).load(R.drawable.animation)
                .into((ImageView) alertDialogBuilder.findViewById(R.id.drawable_conn));
        Button btnStop = alertDialogBuilder.findViewById(R.id.btn_stop);
        alertDialogBuilder.show();
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickStopFromGif = false;
                resetButtonAction();
                Toast.makeText(MainActivity.this, "Stopped command sent to dispenser !!!", Toast.LENGTH_SHORT).show();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            SendCommandToAppliance('S', "STOP");
                        } catch (Exception e) {
                            e.printStackTrace();
                            clickStopFromGif = false;
                        }
                    }
                });
                thread.start();
                if (alertDialogBuilder != null) {
                    alertDialogBuilder.dismiss();
                }
            }
        });
        if (clickStopFromGif) {
            final Handler handler = new Handler();
            final Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (alertDialogBuilder != null) {
                        alertDialogBuilder.dismiss();
                    }
                    displayExitCommand();
                    clickStopFromGif = true;
                }
            };
            handler.postDelayed(runnable, countValueEnd);

        }
    }

    private void connectToWIFI() {
        conf = new WifiConfiguration();
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

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
                    if (config.networkId < 0) {
                        WifiConfiguration config1 = getProfileToRemove(selectedWifiNetwork, wifiManager);
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(config1.networkId, true);
                        wifiManager.reconnect();
                        wifiManager.enableNetwork(config1.networkId, true);
                    } else {
                        wifiManager.disconnect();
                        wifiManager.enableNetwork(config.networkId, true);
                        wifiManager.reconnect();
                        wifiManager.enableNetwork(config.networkId, true);
                    }
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
                    if (wifi.SSID != null && wifi.SSID.contains(macAddress))
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
            buttonHot.setBackground(getResources().getDrawable(R.drawable.gradient_clicked_button_hot));
            buttonNormal.setBackground(getResources().getDrawable(R.drawable.gradient_normal_normal));
            buttonCold.setBackground(getResources().getDrawable(R.drawable.gradient_normal_cold));
            mTypeWater = buttonHot.getText().toString();
        } else if (v.getId() == R.id.button_normal) {
            buttonNormal.setEnabled(true);
            buttonHot.setBackground(getResources().getDrawable(R.drawable.gradient_normal_hot));
            buttonNormal.setBackground(getResources().getDrawable(R.drawable.gradient_clicked_button_normal));
            buttonCold.setBackground(getResources().getDrawable(R.drawable.gradient_normal_cold));
            mTypeWater = buttonNormal.getText().toString();
        } else if (v.getId() == R.id.button_cold) {
            buttonCold.setEnabled(true);
            buttonHot.setBackground(getResources().getDrawable(R.drawable.gradient_normal_hot));
            buttonNormal.setBackground(getResources().getDrawable(R.drawable.gradient_normal_normal));
            buttonCold.setBackground(getResources().getDrawable(R.drawable.gradient_clicked_button_cold));
            mTypeWater = buttonCold.getText().toString();
        } else if (v.getId() == R.id.button_250) {
            button250.setEnabled(true);
            button250.setBackground(getResources().getDrawable(R.drawable.gradient_clicked_button_oneml));
            button500.setBackground(getResources().getDrawable(R.drawable.gradient_normal_button));
            button750.setBackground(getResources().getDrawable(R.drawable.gradient_normal_button));
            mQTYWater = button250.getText().toString();
            countValueEnd = countValueEnd + 0;
        } else if (v.getId() == R.id.button_500) {
            button500.setEnabled(true);
            button500.setBackground(getResources().getDrawable(R.drawable.gradient_clicked_button_twoml));
            button250.setBackground(getResources().getDrawable(R.drawable.gradient_normal_button));
            button750.setBackground(getResources().getDrawable(R.drawable.gradient_normal_button));
            mQTYWater = button500.getText().toString();
            countValueEnd = countValueEnd + 14000;
        } else if (v.getId() == R.id.button_750) {
            button750.setEnabled(true);
            button750.setBackground(getResources().getDrawable(R.drawable.gradient_clicked_button_threeml));
            button500.setBackground(getResources().getDrawable(R.drawable.gradient_normal_button));
            button250.setBackground(getResources().getDrawable(R.drawable.gradient_normal_button));
            mQTYWater = button750.getText().toString();
            countValueEnd = countValueEnd + 26000;

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
                    clickStopButton = true;
                    dispense.setBackgroundColor(getResources().getColor(R.color.green));
                    alertDialogFirstPopUp();
                } else {
                    if (ssId != null && mPassword != null && wifiManager != null) {
                        Toast.makeText(this, "WIFI connection lost, Please scan again!!!!!", Toast.LENGTH_LONG).show();
                        ConnectPhoneWIFIToHotspot(wifiManager, mPassword, ssId);
                    } else {
                        Toast.makeText(this, "No Wifi connection.Please scan QR code again !!", Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                Toast.makeText(this, "Please select water type and quantity to proceed.", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void SendCommandToAppliance(char type, String qty) throws UnsupportedEncodingException {
        String text = "";
        if (qty != null) {
            String path = "?TYPE=" + type + "&QTY=" + qty;
            HttpURLConnection connToESP32 = null;
            try {
                // Defined URL  where to send data
                URL url = new URL("http://192.168.4.1/" + path);
                connToESP32 = (HttpURLConnection) url.openConnection();
                connToESP32.setRequestMethod("GET");
                connToESP32.setRequestProperty("Accept", "text/plain");
                connToESP32.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
                connToESP32.setConnectTimeout(3000);
                int responseCode = connToESP32.getResponseCode();
                // Get the server response
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this, "Sent request to appliance!!!", Toast.LENGTH_LONG).show();
                }

                // Get the server response
            } catch (MalformedURLException e) {
                text = e.toString();
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                text = e.toString();
                //clickStopFromGif = false;
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            } catch (Exception e) {
                text = e.toString();
                Toast.makeText(this, text, Toast.LENGTH_LONG).show();
            } finally {
                if (connToESP32 != null) {
                    connToESP32.disconnect();
                }
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}



