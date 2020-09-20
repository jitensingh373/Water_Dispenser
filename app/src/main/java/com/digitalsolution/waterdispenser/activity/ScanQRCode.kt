package com.digitalsolution.waterdispenser.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.digitalsolution.waterdispenser.R
import com.google.zxing.integration.android.IntentIntegrator

class ScanQRCode : AppCompatActivity() {
    var btnAction: Button? = null
    private var mScannerView: IntentIntegrator? = null
    private var ssId: String? = null
    private var mPassword: String? = null

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanned_barcode)
        initViews()
    }

    private fun initViews() {
        btnAction = findViewById(R.id.btnAction)
        btnAction?.setOnClickListener(View.OnClickListener {

            val intent = Intent(this@ScanQRCode, MainActivity::class.java)
            startActivity(intent)

            mScannerView = IntentIntegrator(this@ScanQRCode)
            mScannerView!!.setOrientationLocked(false)
            mScannerView!!.setBeepEnabled(true)
            mScannerView!!.initiateScan()
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (intentResult != null) {
            if (resultCode == Activity.RESULT_OK) {
                var ssid = ""
                var password = ""
                val contents = data!!.getStringExtra("SCAN_RESULT")
                val arrOfStr: Array<String?> = contents.split(":".toRegex(), 5).toTypedArray()
                if (arrOfStr[2] != null) {
                    val arrOfStr1 = arrOfStr[2]!!.split(";".toRegex()).toTypedArray()
                    ssid = arrOfStr1[0]
                    ssId = ssid
                }
                if (arrOfStr[4] != null) {
                    val arrOfStr1 = arrOfStr[4]!!.split(";".toRegex()).toTypedArray()
                    password = arrOfStr1[0]
                    mPassword = password
                }
                val intent = Intent(this@ScanQRCode, MainActivity::class.java)
                intent.putExtra("SSID", ssid)
                intent.putExtra("Password", password)
                startActivity(intent)
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(this, "Scanned failed, try again !!!", Toast.LENGTH_SHORT).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val builder = AlertDialog.Builder(this@ScanQRCode)
        builder.setTitle(R.string.app_name)
        builder.setIcon(R.mipmap.ic_launcher)
        builder.setCancelable(true)
        builder.setMessage("Do you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id -> finish() }
                .setNegativeButton("No") { dialog, id -> dialog.cancel() }
        val alert = builder.create()
        alert.show()
    }
}