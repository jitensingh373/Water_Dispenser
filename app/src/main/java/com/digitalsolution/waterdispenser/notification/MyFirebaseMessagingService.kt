package com.digitalsolution.waterdispenser.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.app.NotificationCompat
import com.digitalsolution.waterdispenser.R
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

import java.util.*


class MyFirebaseMessagingService : FirebaseMessagingService() {

    private val TAG = "MyFirebaseToken"
    private lateinit var notificationManager: NotificationManager
    private val ADMIN_CHANNEL_ID = "Android4Dev"


    override fun onNewToken(token: String?) {
        super.onNewToken(token)
        Log.i(TAG, token)
        val token = FirebaseInstanceId.getInstance().token
        Log.d(TAG, "Token perangkat ini: ${token}")

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)
        remoteMessage?.let { message ->
            Log.i(TAG, message.getData().get("message"))

            notificationManager = getSystemService(this) as NotificationManager

            //Setting up Notification channels for android O and above
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                setupNotificationChannels()
            }
            val notificationId = Random().nextInt(60000)

            val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(this, ADMIN_CHANNEL_ID)
                    .setSmallIcon(R.mipmap.ic_launcher)  //a resource for your custom small icon
                    .setContentTitle(message.data["title"]) //the "title" value you sent in your notification
                    .setContentText(message.data["message"]) //ditto
                    .setAutoCancel(true)  //dismisses the notification on click
                    .setSound(defaultSoundUri)

            val notificationManager = getSystemService(this) as NotificationManager
            notificationManager.notify(notificationId /* ID of notification */, notificationBuilder.build())

        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupNotificationChannels() {
        val adminChannelName = "tjitu"
        val adminChannelDescription = "jitu22"
        val adminChannel: NotificationChannel
        adminChannel = NotificationChannel(ADMIN_CHANNEL_ID, adminChannelName, NotificationManager.IMPORTANCE_LOW)
        adminChannel.description = adminChannelDescription
        adminChannel.enableLights(true)
        adminChannel.lightColor = Color.RED
        adminChannel.enableVibration(true)
        notificationManager.createNotificationChannel(adminChannel)
    }
}