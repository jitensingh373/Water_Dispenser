package com.digitalsolution.waterdispenser.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.digitalsolution.waterdispenser.R

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_activity)
        startActivity(Intent(this@SplashActivity, HomeActivity::class.java))
    }
}