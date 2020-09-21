package com.digitalsolution.waterdispenser.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.digitalsolution.waterdispenser.R
import com.digitalsolution.waterdispenser.activity.HomeActivity

class HomeActivity : AppCompatActivity(), View.OnClickListener {
    private var adminTextView: TextView? = null
    private var userTextView: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        adminTextView = findViewById(R.id.admin_id)
        userTextView = findViewById(R.id.user_id)
        adminTextView!!.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@HomeActivity, LoginActivity::class.java)
            startActivity(intent)
        })
        userTextView!!.setOnClickListener(View.OnClickListener { splashActivity() })
    }

    private fun splashActivity() {
        Handler().postDelayed({
            val currentapiVersion = Build.VERSION.SDK_INT
            if (BUILD_VERSION <= currentapiVersion) {
                startActivity(Intent(this@HomeActivity, OpenWifiNetworkAdd::class.java))
            } else {
                startActivity(Intent(this@HomeActivity, ScanQRCode::class.java))
            }
        }, 1000)
    }

    override fun onClick(v: View) {}

    companion object {
        const val BUILD_VERSION = 29
    }
}