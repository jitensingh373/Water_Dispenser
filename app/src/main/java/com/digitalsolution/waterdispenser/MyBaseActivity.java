package com.digitalsolution.waterdispenser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class MyBaseActivity extends Activity {

    public static final long DISCONNECT_TIMEOUT = 120000;
    private WifiManager  wifiManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiManager = (WifiManager) this.getBaseContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
    }

    private Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                    MyBaseActivity.this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("Alert");
            alertDialog
                    .setMessage("\"Session Time Out\" - Please Scan Again");
            alertDialog.setNegativeButton("OK",
                    new DialogInterface.OnClickListener() {

                        public void onClick(DialogInterface dialog, int which) {
                           if (wifiManager != null && wifiManager.isWifiEnabled()) {
                                wifiManager.setWifiEnabled(false);
                            }
                            Intent intent = new Intent(MyBaseActivity.this, SplashActivity.class);
                            startActivity(intent);
                            dialog.cancel();
                        }
                    });

            alertDialog.show();
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        resetDisconnectTimer();
    }

    @Override
    public void onResume() {
        super.onResume();
        resetDisconnectTimer();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopDisconnectTimer();
    }
}
