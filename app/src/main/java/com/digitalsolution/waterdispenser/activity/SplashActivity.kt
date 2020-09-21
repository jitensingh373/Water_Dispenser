package com.digitalsolution.waterdispenser.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.digitalsolution.waterdispenser.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
    }

    private fun splashActivity() {
        Handler().postDelayed({
            val currentapiVersion = Build.VERSION.SDK_INT
            if (BUILD_VERSION <= currentapiVersion) {
                startActivity(Intent(this@SplashActivity, OpenWifiNetworkAdd::class.java))
            } else {
                startActivity(Intent(this@SplashActivity, ScanQRCode::class.java))
            }
        }, 1000)
    }

    companion object {
        private const val BUILD_VERSION = 29
    }
}