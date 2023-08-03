package com.example.sarathi.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.sarathi.MainActivity
import com.example.sarathi.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val channelId = "sarthi_channelId"

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val intent = Intent(this@MyFirebaseMessagingService,MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE)
        createNotificationChannel(manager as NotificationManager)
        val intent01 = PendingIntent.getActivities(this@MyFirebaseMessagingService,0, arrayOf(intent),PendingIntent.FLAG_IMMUTABLE)
        val notification = NotificationCompat.Builder(this,channelId)
            .setContentTitle(message.data["title"])
            .setContentText(message.data["message"])
            .setSmallIcon(R.drawable.notifications)
            .setAutoCancel(true)
            .setContentIntent(intent01)
            .build()

        manager.notify(Random.nextInt(),notification)
    }
    private fun createNotificationChannel(manager : NotificationManager){
        val channel = NotificationChannel(channelId,"sarthi_channelId",NotificationManager.IMPORTANCE_HIGH)

        channel.description = "New Chat"
        channel.enableLights(true)
        manager.createNotificationChannel(channel)
    }

}