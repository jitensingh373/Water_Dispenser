package com.digitalsolution.waterdispenser;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int BUILD_VERSION = 29;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        splashActivity();
    }

    private void splashActivity() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                if (BUILD_VERSION <= currentapiVersion) {
                    startActivity(new Intent(SplashActivity.this, OpenWifiNetworkAdd.class));
                } else {
                    startActivity(new Intent(SplashActivity.this, ScanQRCode.class));
                }
            }
        }, 1000);
    }
}

