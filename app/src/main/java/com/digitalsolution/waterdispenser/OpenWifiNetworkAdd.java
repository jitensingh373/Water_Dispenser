package com.digitalsolution.waterdispenser;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class OpenWifiNetworkAdd extends AppCompatActivity {
    static final int STATIC_RESULT = 2;
    Button btnAction ;
    Button open;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_setting);
        open = findViewById(R.id.btnAction_to_Connect_wifi);
        textView = findViewById(R.id.instruction);
        textView.setText("1. Read all steps carefully. " +
                "\n2. You have to click on Connect To Wifi button." +
                "\n3. You will be navigated to wifi add network screen" +
                "\n4. If wifi not enable, enable it" +
                "\n5. Please click on Jal Durg in wifi list and enter password." +
                "\n6. Once connected to wifi,back pressed.");
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWifiSettings();
            }
        });
    }

    public void openWifiSettings() {
        Intent openWirelessSettings = new Intent("android.net.wifi.PICK_WIFI_NETWORK");
        startActivityForResult(openWirelessSettings, STATIC_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == STATIC_RESULT) {
            goTOMainScreen();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void goTOMainScreen(){
        btnAction = findViewById(R.id.btnAction_to_Connect);
        open.setVisibility(View.GONE);
        btnAction.setVisibility(View.VISIBLE);
        textView.setVisibility(View.GONE);
        TextView textView = findViewById(R.id.instruction1);
        textView.setVisibility(View.GONE);
        btnAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(OpenWifiNetworkAdd.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure you want to close this app?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }

                })
                .setNegativeButton("No", null)
                .show();
    }
}
