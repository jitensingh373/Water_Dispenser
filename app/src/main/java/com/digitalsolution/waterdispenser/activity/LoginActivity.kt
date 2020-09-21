package com.digitalsolution.waterdispenser.activity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.digitalsolution.waterdispenser.R
import com.digitalsolution.waterdispenser.data.LoginCredential
import com.google.firebase.database.DatabaseReference

class LoginActivity : AppCompatActivity(){

    private var viewModel: UserConsumptionViewModel? = null
    private var mDatabase: DatabaseReference? = null
    private var loginAllUser = mutableListOf<List<LoginCredential>>()
    lateinit var loginButton :Button
    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        viewModel = ViewModelProvider(this).get(UserConsumptionViewModel::class.java)
        viewModel!!.validateUserAndPassword()
        val usernameEditText = findViewById<EditText>(R.id.username)
        val passwordEditText = findViewById<EditText>(R.id.password)
         loginButton = findViewById<Button>(R.id.login)
        val loadingProgressBar = findViewById<ProgressBar>(R.id.loading)

        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            loginToApp(usernameEditText.text.toString(), passwordEditText.text.toString());
        }
        subscribeToData()
    }

    private fun loginToApp(userName:String, passWord : String) {
        for (localUser in loginAllUser) {
            for (forach in localUser) {
                if(forach.USERNAME?.equals(userName )!! && forach.PASSCODE.equals(passWord)){
                    Toast.makeText(this,"User validated !!!",Toast.LENGTH_LONG)
                    splashActivity()
                    //finish()
                }
            }
        }

    }
    private fun splashActivity() {
        Handler().postDelayed({
            val currentapiVersion = Build.VERSION.SDK_INT
            if (HomeActivity.BUILD_VERSION <= currentapiVersion) {
                startActivity(Intent(this@LoginActivity, OpenWifiNetworkAdd::class.java))
            } else {
                startActivity(Intent(this@LoginActivity, ScanQRCode::class.java))
            }
        }, 1000)
    }

    private fun subscribeToData() {
        viewModel?.loginCredentialFetch?.observe(this, Observer {
            loginAllUser.add(it)
        })
    }

}