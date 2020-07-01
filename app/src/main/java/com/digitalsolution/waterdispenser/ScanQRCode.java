package com.digitalsolution.waterdispenser;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

public class ScanQRCode extends AppCompatActivity {
    Button btnAction;
    private IntentIntegrator mScannerView;
    private String ssId;
    private String mPassword;
    private String ssIdUser = "Chetan";

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanned_barcode);
        initViews();

    }

    private void initViews() {
        btnAction = findViewById(R.id.btnAction);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mScannerView = new IntentIntegrator(ScanQRCode.this);
                mScannerView.setOrientationLocked(false);
                mScannerView.setBeepEnabled(true);
                mScannerView.initiateScan();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (intentResult != null) {
            if (resultCode == RESULT_OK) {
                String ssid = "";
                String password = "";
                String contents = data.getStringExtra("SCAN_RESULT");
                String[] arrOfStr = contents.split(":", 5);
                if (arrOfStr[2] != null) {
                    String[] arrOfStr1 = arrOfStr[2].split(";");
                    ssid = arrOfStr1[0];
                    ssId = ssid;
                }
                if (arrOfStr[4] != null) {
                    String[] arrOfStr1 = arrOfStr[4].split(";");
                    password = arrOfStr1[0];
                    mPassword = password;
                }
                if (ssId.trim().equalsIgnoreCase(ssIdUser)) {
                    Intent intent = new Intent(ScanQRCode.this, MainActivity.class);
                    intent.putExtra("SSID", ssid);
                    intent.putExtra("Password", password);
                    startActivity(intent);
                }else{
                    Toast.makeText(this, "Not Registered User!!!", Toast.LENGTH_SHORT).show();
                }
            }
            if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Scanned failed !!!", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(ScanQRCode.this);
        builder.setTitle(R.string.app_name);
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setCancelable(true);
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

}







