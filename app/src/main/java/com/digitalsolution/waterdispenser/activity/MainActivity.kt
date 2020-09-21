package com.digitalsolution.waterdispenser.activity

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.text.TextUtils
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.digitalsolution.waterdispenser.R
import com.digitalsolution.waterdispenser.activity.data.UserConsumptionDetails
import com.digitalsolution.waterdispenser.data.UserDetailsConstant
import com.digitalsolution.waterdispenser.data.UserDetailsConstant.FIREBASE_NODE_CHILD
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.sql.Timestamp
import kotlin.jvm.Throws

class MainActivity : MyBaseActivity(), View.OnClickListener {
    private var textView: TextView? = null
    private var buttonHot: Button? = null
    private var buttonNormal: Button? = null
    private var buttonCold: Button? = null
    private var button250: Button? = null
    private var button500: Button? = null
    private var button750: Button? = null
    private var dispense: Button? = null
    private var levelSize : TextView? = null
    private var mTypeWater: String? = null
    private var mQTYWater: String? = null
    private var conf: WifiConfiguration? = null
    private var ssId: String? = null
    private var mPassword: String? = null
    private var wifiManager: WifiManager? = null
    private var alertDialog: AlertDialog? = null
    private var clickStopButton = true
    private var clickStopFromGif = true
    private var alertDialogBuilder: Dialog? = null
    private var countValueEnd = 14000
    private lateinit var viewModel: UserConsumptionViewModel
    private var authors = mutableListOf<List<UserConsumptionDetails>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProvider(this).get(UserConsumptionViewModel::class.java)

        if (intent.getStringExtra("SSID") != null && intent.getStringExtra("Password") != null) {
            ssId = intent.getStringExtra("SSID")
            FIREBASE_NODE_CHILD = ssId ;
            mPassword = intent.getStringExtra("Password")
        }
        intializeView()
        viewModel.lastfillTimeStamp()
        viewModel.timeStampValue.observe(this, Observer {
            viewModel.fetchUserConsumptionAllDetails(it)
        })

        viewModel.authors.observe(this, Observer {
            waterConsumption(it)
        })


    }

   private fun waterConsumption(userData : List<UserConsumptionDetails>) {
       var sumOfQuantity = 0
       for (userDataQuantity in userData) {
           val localValue = userDataQuantity.WATERQTY?.toInt()
              if(null != localValue && null != sumOfQuantity) {
                  sumOfQuantity += localValue!!
              }
       }
       var dispenserStatus = quantityRange(UserDetailsConstant.DISPENSER_SIZE - sumOfQuantity)
       if(dispenserStatus == "very Low"){
           informAdminForRefil()
           levelSize?.text = dispenserStatus
       }
       else {
           levelSize?.text = dispenserStatus
       }

   }

    private fun informAdminForRefil() {
        if (clickStopFromGif) {
            val alertDialogBuilderTimer = AlertDialog.Builder(this).setCancelable(false)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.admin_information_layout, null)
            alertDialogBuilderTimer.setView(dialogView)
            val btnStop = dialogView.findViewById<Button>(R.id.btn_stop)
            alertDialog = alertDialogBuilderTimer.create()
            alertDialog!!.show()
            btnStop.setOnClickListener {
                if (alertDialog != null) {
                    alertDialog!!.dismiss()
                }
                val currentapiVersion = Build.VERSION.SDK_INT
                if (BUILD_VERSION <= currentapiVersion) {
                    startActivity(Intent(this@MainActivity, OpenWifiNetworkAdd::class.java))
                } else {
                    startActivity(Intent(this@MainActivity, ScanQRCode::class.java))
                }
            }
        }
    }

    private fun quantityRange(valueSize : Int) : String{
        when(valueSize){
            in 15000 .. 20000 -> return "High"
            in 10000 .. 14999 -> return "Medium"
            in 5000  .. 9999  -> "Low"
            else -> return "very Low"
        }
        return "very Low"

    }

    private fun intializeView() {
        val currentapiVersion = Build.VERSION.SDK_INT
        if (BUILD_VERSION <= currentapiVersion) {
            Toast.makeText(this, "Connected to Whirlpool Dispenser !! ", Toast.LENGTH_SHORT).show()
        } else {
            connectToWIFI()
            Toast.makeText(this, "Connected to Whirlpool Dispenser !!  ", Toast.LENGTH_SHORT).show()
        }
        textView = findViewById(R.id.inst_multi)
        textView?.setText("1. Turn off mobile Data.\n2. Select the type [hot,normal,cold].\n2. Select the quantity of water.\n3. Click on dispense button.")
        buttonHot = findViewById(R.id.button_hot)
        buttonNormal = findViewById(R.id.button_normal)
        buttonCold = findViewById(R.id.button_cold)
        button250 = findViewById(R.id.button_250)
        button500 = findViewById(R.id.button_500)
        button750 = findViewById(R.id.button_750)
        dispense = findViewById(R.id.button_on)
        levelSize = findViewById(R.id.levelSize)
        buttonHot?.setOnClickListener(this)
        buttonNormal?.setOnClickListener(this)
        buttonCold?.setOnClickListener(this)
        button250?.setOnClickListener(this)
        button500?.setOnClickListener(this)
        button750?.setOnClickListener(this)
        dispense?.setOnClickListener(this)
        levelSize?.setOnClickListener(this)
    }

    private fun resetButtonAction() {
        mTypeWater = null
        mQTYWater = null
        val drawable = resources.getDrawable(R.drawable.gradient_normal_orange)
        dispense!!.background = drawable
        countValueEnd += 0
    }

    private fun alertDialogFirstPopUp() {
        val alertDialogBuilderTimer = AlertDialog.Builder(this).setCancelable(false)
        val inflater = this.layoutInflater
        val dialogView = inflater.inflate(R.layout.alert_box_timer, null)
        alertDialogBuilderTimer.setView(dialogView)
        val tvCount = dialogView.findViewById<View>(R.id.textViewID) as TextView
        val btnStop = dialogView.findViewById<View>(R.id.btn_stop) as Button
        alertDialog = alertDialogBuilderTimer.create()
        alertDialog!!.show()
        btnStop.setOnClickListener {
            clickStopButton = false
            Toast.makeText(this@MainActivity, "User Stopped, Resetting Selected Changes !!!", Toast.LENGTH_SHORT).show()
            resetButtonAction()
            if (alertDialog != null) {
                alertDialog!!.dismiss()
            }
        }
        object : CountDownTimer(6000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                tvCount.text = "" + millisUntilFinished / 1000
            }

            override fun onFinish() {
                if (alertDialog != null) {
                    alertDialog!!.dismiss()
                }
                displayGIFPopUP()
            }

            private fun displayGIFPopUP() {
                if (clickStopButton) {
                    try {
                        val thread = Thread(Runnable {
                            try {
                                if (mTypeWater != null && mQTYWater != null) {
                                    val firstTypeLetterType = mTypeWater!![0]
                                    val strQty = "0" + mQTYWater!!.substring(0, 3)
                                    val user = UserConsumptionDetails()
                                    val timestamp = Timestamp(System.currentTimeMillis())
                                    user.DISPENSERNAME = if( ssId != null) ssId else ""
                                    user.DISPENSETIME = "014"
                                    user.TIMESTAMP = timestamp.time.toString().substring(0,10)
                                    user.WATERQTY = mQTYWater?.substring(0,3)
                                    user.WATERTYPE = mTypeWater?.toLowerCase()
                                    user.USERNAME = "supervisor_2"
                                    user.PLATFORM = "AOS"
                                    viewModel.addUserConsumptionDetails(user)
                                    SendCommandToAppliance(firstTypeLetterType, strQty)
                                    clickStopButton = true
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        })
                        thread.start()
                        val handler = Handler()
                        val runnable = Runnable { loadPopUpGIF() }
                        handler.postDelayed(runnable, 0)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }.start()
    }

    public override fun onPause() {
        super.onPause()
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        if (alertDialog != null) {
            alertDialog!!.dismiss()
        }
        if (wifiManager != null && wifiManager!!.isWifiEnabled) {
            wifiManager!!.isWifiEnabled = false
        }
    }

    private fun displayExitCommand() {
        if (clickStopFromGif) {
            val alertDialogBuilderTimer = AlertDialog.Builder(this).setCancelable(false)
            val inflater = this.layoutInflater
            val dialogView = inflater.inflate(R.layout.alert_box_exit, null)
            alertDialogBuilderTimer.setView(dialogView)
            val btnStop = dialogView.findViewById<Button>(R.id.btn_stop)
            alertDialog = alertDialogBuilderTimer.create()
            alertDialog!!.show()
            btnStop.setOnClickListener {
                if (wifiManager != null && wifiManager!!.isWifiEnabled) {
                    wifiManager!!.isWifiEnabled = false
                }
                if (alertDialog != null) {
                    alertDialog!!.dismiss()
                }
                val currentapiVersion = Build.VERSION.SDK_INT
                if (BUILD_VERSION <= currentapiVersion) {
                    startActivity(Intent(this@MainActivity, OpenWifiNetworkAdd::class.java))
                } else {
                    startActivity(Intent(this@MainActivity, ScanQRCode::class.java))
                }
            }
        }
    }

    private fun loadPopUpGIF() {
        alertDialogBuilder = Dialog(
                this)
        alertDialogBuilder!!.setContentView(R.layout.alert_box)
        alertDialogBuilder!!.setCancelable(false)
        Glide.with(this).load(R.drawable.animation)
                .into((alertDialogBuilder!!.findViewById<View>(R.id.drawable_conn) as ImageView))
        val btnStop = alertDialogBuilder!!.findViewById<Button>(R.id.btn_stop)
        alertDialogBuilder!!.show()
        btnStop.setOnClickListener {
            clickStopFromGif = false
            resetButtonAction()
            Toast.makeText(this@MainActivity, "Stopped command sent to dispenser !!!", Toast.LENGTH_SHORT).show()
            val thread = Thread(Runnable {
                try {
                    SendCommandToAppliance('S', "STOP")
                } catch (e: Exception) {
                    e.printStackTrace()
                    clickStopFromGif = false
                }
            })
            thread.start()
            if (alertDialogBuilder != null) {
                alertDialogBuilder!!.dismiss()
            }
        }
        if (clickStopFromGif) {
            val handler = Handler()
            val runnable = Runnable {
                if (alertDialogBuilder != null) {
                    alertDialogBuilder!!.dismiss()
                }
                displayExitCommand()
                clickStopFromGif = true
            }
            handler.postDelayed(runnable, countValueEnd.toLong())
        }
    }

    private fun connectToWIFI() {
        conf = WifiConfiguration()
        wifiManager = this.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        Thread(Runnable {
            if (ssId != null && mPassword != null) {
                ConnectPhoneWIFIToHotspot(wifiManager, mPassword!!, ssId)
            }
        }).start()
    }

    private fun ConnectPhoneWIFIToHotspot(wifiManager: WifiManager?, password: String, selectedWifiNetwork: String?) {
        var wifiManager = wifiManager
        if (wifiManager == null) {
            wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        }
        if (wifiManager != null) {
            if (!wifiManager.isWifiEnabled) {
                wifiManager.isWifiEnabled = true
            }
            wifiManager.disconnect()
            if (selectedWifiNetwork != null) {
                val config = getProfileForConnection(selectedWifiNetwork, password, wifiManager)
                if (config != null) {
                    if (config.networkId < 0) {
                        val config1 = getProfileToRemove(selectedWifiNetwork, wifiManager)
                        wifiManager.disconnect()
                        wifiManager.enableNetwork(config1!!.networkId, true)
                        wifiManager.reconnect()
                        wifiManager.enableNetwork(config1.networkId, true)
                    } else {
                        wifiManager.disconnect()
                        wifiManager.enableNetwork(config.networkId, true)
                        wifiManager.reconnect()
                        wifiManager.enableNetwork(config.networkId, true)
                    }
                }
            }
        }
    }

    fun getProfileForConnection(macAddress: String, password: String, wifiManager: WifiManager?): WifiConfiguration {
        var wifiManager = wifiManager
        if (wifiManager == null) {
            wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        }
        if (wifiManager != null) {
            if (wifiManager.configuredNetworks != null) {
                for (wifi in wifiManager.configuredNetworks) {
                    if (wifi.SSID != null && wifi.SSID.contains(macAddress)) return wifi
                }
            }
        }
        return createPhoneProfileInWIFIConf(macAddress, password, wifiManager)
    }

    fun createPhoneProfileInWIFIConf(ssid: String, password: String, wifiManager: WifiManager?): WifiConfiguration {
        var wifiManager = wifiManager
        wifiManager = initWifiManager(wifiManager)
        var wfc = getProfileToRemove(ssid, wifiManager)
        val list = wifiManager!!.configuredNetworks
        var isNewProfile = false
        if (wfc == null) {
            wfc = WifiConfiguration()
            isNewProfile = true
            wfc.SSID = "\"" + ssid + "\""
            wfc.status = WifiConfiguration.Status.ENABLED
            var highest_int = 0
            if (list != null) {
                for (i in list) {
                    if (i.priority > highest_int) {
                        highest_int = i.priority
                    }
                }
                wfc.priority = highest_int + 1
            } else {
                wfc.priority = 40
            }
            wfc.status = WifiConfiguration.Status.ENABLED
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.RSN)
            wfc.allowedProtocols.set(WifiConfiguration.Protocol.WPA)
            wfc.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK)
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP)
            wfc.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP)
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP)
            wfc.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP)
            wfc.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN)
        }
        wfc.preSharedKey = "\"" + password + "\""
        if (wifiManager != null) {
            if (isNewProfile) {
                wifiManager.addNetwork(wfc)
            } else {
                wifiManager.updateNetwork(wfc)
            }
        }
        return wfc
    }

    fun getProfileToRemove(macAddress: String?, wifiManager: WifiManager?): WifiConfiguration? {
        var wifiManager = wifiManager
        wifiManager = initWifiManager(wifiManager)
        if (!TextUtils.isEmpty(macAddress) && wifiManager != null) {
            if (wifiManager.configuredNetworks != null && !wifiManager.configuredNetworks.isEmpty()) {
                for (wifi in wifiManager.configuredNetworks) {
                    if (wifi != null && wifi.SSID != null) {
                        if (wifi.SSID.contains(macAddress!!)) {
                            return wifi
                        }
                    }
                }
            }
        }
        return null
    }

    private fun initWifiManager(wifiManager: WifiManager?): WifiManager? {
        var wifiManager = wifiManager
        return if (wifiManager == null) {
            wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
            wifiManager
        } else {
            wifiManager
        }
    }

    override fun onClick(v: View) {
        if (v.id == R.id.button_hot) {
            buttonHot!!.isEnabled = true
            buttonHot!!.background = resources.getDrawable(R.drawable.gradient_clicked_button_hot)
            buttonNormal!!.background = resources.getDrawable(R.drawable.gradient_normal_normal)
            buttonCold!!.background = resources.getDrawable(R.drawable.gradient_normal_cold)
            mTypeWater = buttonHot!!.text.toString()
        } else if (v.id == R.id.button_normal) {
            buttonNormal!!.isEnabled = true
            buttonHot!!.background = resources.getDrawable(R.drawable.gradient_normal_hot)
            buttonNormal!!.background = resources.getDrawable(R.drawable.gradient_clicked_button_normal)
            buttonCold!!.background = resources.getDrawable(R.drawable.gradient_normal_cold)
            mTypeWater = buttonNormal!!.text.toString()
        } else if (v.id == R.id.button_cold) {
            buttonCold!!.isEnabled = true
            buttonHot!!.background = resources.getDrawable(R.drawable.gradient_normal_hot)
            buttonNormal!!.background = resources.getDrawable(R.drawable.gradient_normal_normal)
            buttonCold!!.background = resources.getDrawable(R.drawable.gradient_clicked_button_cold)
            mTypeWater = buttonCold!!.text.toString()
        } else if (v.id == R.id.button_250) {
            button250!!.isEnabled = true
            button250!!.background = resources.getDrawable(R.drawable.gradient_clicked_button_oneml)
            button500!!.background = resources.getDrawable(R.drawable.gradient_normal_button)
            button750!!.background = resources.getDrawable(R.drawable.gradient_normal_button)
            mQTYWater = button250!!.text.toString()
            countValueEnd = countValueEnd + 0
        } else if (v.id == R.id.button_500) {
            button500!!.isEnabled = true
            button500!!.background = resources.getDrawable(R.drawable.gradient_clicked_button_twoml)
            button250!!.background = resources.getDrawable(R.drawable.gradient_normal_button)
            button750!!.background = resources.getDrawable(R.drawable.gradient_normal_button)
            mQTYWater = button500!!.text.toString()
            countValueEnd = countValueEnd + 14000
        } else if (v.id == R.id.button_750) {
            button750!!.isEnabled = true
            button750!!.background = resources.getDrawable(R.drawable.gradient_clicked_button_threeml)
            button500!!.background = resources.getDrawable(R.drawable.gradient_normal_button)
            button250!!.background = resources.getDrawable(R.drawable.gradient_normal_button)
            mQTYWater = button750!!.text.toString()
            countValueEnd = countValueEnd + 26000
        } else if (v.id == R.id.button_on) {
            if (mQTYWater != null && mTypeWater == null) {
                Toast.makeText(this, "Please select water type.", Toast.LENGTH_SHORT).show()
            } else if (mQTYWater == null && mTypeWater != null) {
                Toast.makeText(this, "Please select  water quantity..", Toast.LENGTH_SHORT).show()
            } else if (mQTYWater != null && mTypeWater != null) {
                if (wifiManager == null) {
                    wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
                }
                if (wifiManager!!.isWifiEnabled) {
                    clickStopButton = true
                    dispense!!.setBackgroundColor(resources.getColor(R.color.green))
                    alertDialogFirstPopUp()
                } else {
                    if (ssId != null && mPassword != null && wifiManager != null) {
                        Toast.makeText(this, "WIFI connection lost, Please scan again!!!!!", Toast.LENGTH_LONG).show()
                        ConnectPhoneWIFIToHotspot(wifiManager, mPassword!!, ssId)
                    } else {
                        Toast.makeText(this, "No Wifi connection.Please scan QR code again !!", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Please select water type and quantity to proceed.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @Throws(UnsupportedEncodingException::class)
    private fun SendCommandToAppliance(type: Char, qty: String?) {
        var text = ""
        if (qty != null) {
            val path = "?TYPE=$type&QTY=$qty"
            var connToESP32: HttpURLConnection? = null
            try {
                // Defined URL  where to send data
                val url = URL("http://192.168.4.1/$path")
                connToESP32 = url.openConnection() as HttpURLConnection
                connToESP32!!.requestMethod = "GET"
                connToESP32.setRequestProperty("Accept", "text/plain")
                connToESP32.setRequestProperty("Content-type", "application/x-www-form-urlencoded")
                connToESP32.connectTimeout = 3000
                val responseCode = connToESP32.responseCode
                // Get the server response
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    Toast.makeText(this, "Sent request to appliance!!!", Toast.LENGTH_LONG).show()
                }

                // Get the server response
            } catch (e: MalformedURLException) {
                text = e.toString()
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            } catch (e: IOException) {
                text = e.toString()
                //clickStopFromGif = false;
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            } catch (e: Exception) {
                text = e.toString()
                Toast.makeText(this, text, Toast.LENGTH_LONG).show()
            } finally {
                connToESP32?.disconnect()
            }
        }
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {}

    companion object {
        private const val BUILD_VERSION = 29
    }
}