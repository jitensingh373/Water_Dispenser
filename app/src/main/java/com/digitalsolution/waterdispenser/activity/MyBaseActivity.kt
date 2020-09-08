package com.digitalsolution.waterdispenser.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import androidx.appcompat.app.AppCompatActivity

open class MyBaseActivity : AppCompatActivity() {
    private var wifiManager: WifiManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wifiManager = this.baseContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    private val disconnectHandler: Handler = object : Handler() {
        override fun handleMessage(msg: Message) {}
    }
    private val disconnectCallback = Runnable {
        val alertDialog = AlertDialog.Builder(
                this@MyBaseActivity)
        alertDialog.setCancelable(false)
        alertDialog.setTitle("Alert")
        alertDialog
                .setMessage("\"Session Time Out\" - Please Scan Again")
        alertDialog.setNegativeButton("OK"
        ) { dialog, which ->
            if (wifiManager != null && wifiManager!!.isWifiEnabled) {
                wifiManager!!.isWifiEnabled = false
            }
            val intent = Intent(this@MyBaseActivity, SplashActivity::class.java)
            startActivity(intent)
            dialog.cancel()
        }
        alertDialog.show()
    }

    fun resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback)
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT)
    }

    fun stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback)
    }

    override fun onUserInteraction() {
        resetDisconnectTimer()
    }

    public override fun onResume() {
        super.onResume()
        resetDisconnectTimer()
    }

    public override fun onStop() {
        super.onStop()
        stopDisconnectTimer()
    }

    companion object {
        const val DISCONNECT_TIMEOUT: Long = 120000
    }
}