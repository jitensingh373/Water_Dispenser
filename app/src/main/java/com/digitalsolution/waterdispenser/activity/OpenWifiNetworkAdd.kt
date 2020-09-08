package com.digitalsolution.waterdispenser.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.digitalsolution.waterdispenser.R

class OpenWifiNetworkAdd : AppCompatActivity() {
    var btnAction: Button? = null
    var open: Button? = null
    var textView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.open_setting)
        open = findViewById(R.id.btnAction_to_Connect_wifi)
        textView = findViewById(R.id.instruction)
        textView?.setText("""
    1. Read all steps carefully.
    2. You have to click on Connect To Wifi button.
    3. You will be navigated to wifi add network screen
    4. If wifi not enable, enable it
    5. Please click on Jal Durg in wifi list and enter password.
    6. Once connected to wifi,back pressed.
    """.trimIndent())
        open?.setOnClickListener(View.OnClickListener { openWifiSettings() })
    }

    fun openWifiSettings() {
        val openWirelessSettings = Intent("android.net.wifi.PICK_WIFI_NETWORK")
        startActivityForResult(openWirelessSettings, STATIC_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == STATIC_RESULT) {
            goTOMainScreen()
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun goTOMainScreen() {
        btnAction = findViewById(R.id.btnAction_to_Connect)
        open?.visibility = View.GONE
        btnAction?.setVisibility(View.VISIBLE)
        textView!!.visibility = View.GONE
        val textView = findViewById<TextView>(R.id.instruction1)
        textView.visibility = View.GONE
        btnAction?.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@OpenWifiNetworkAdd, MainActivity::class.java)
            startActivity(intent)
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setMessage("Are you sure you want to close this app?")
                .setPositiveButton("Yes") { dialog, which -> finish() }
                .setNegativeButton("No", null)
                .show()
    }

    companion object {
        const val STATIC_RESULT = 2
    }
}