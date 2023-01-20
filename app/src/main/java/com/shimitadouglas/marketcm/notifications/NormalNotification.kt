package com.shimitadouglas.marketcm.notifications

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class NormalNotification(
    var context: Context,
    var title: String,
    var message: String,
    var icon: Int
) : Application() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        //call function to create notification on a coroutine
        GlobalScope.launch(Dispatchers.Default) {
            funCreateNotification()

        }
        //
    }

    fun funCreateNotification() {
        //code begins
        var notChannelID = "CHANNEL_ID_MARKET_CM"
        var notChannelName = "MARKET_CM_CHANNEL"
        val notImportance = NotificationManager.IMPORTANCE_HIGH
        //check if the version of the OS is Oreo and above and evaluate accordingly
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notChannel = NotificationChannel(notChannelID, notChannelName, notImportance)

            //create a builder for the notification
            val notificationBuilder = Notification.Builder(context, notChannelID)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(Notification.PRIORITY_DEFAULT)
            //
            //creating a notification manager to manage creating notify channel and notify
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //creating the notification channels
            notificationManager.createNotificationChannel(notChannel)
            notificationManager.notify(0, notificationBuilder.build())
            //

        } else {
            //android OS Version <Oreo
            val notBuilderBelowOreo: NotificationCompat.Builder =
                NotificationCompat.Builder(context, notChannelID)
            notBuilderBelowOreo.priority = NotificationCompat.PRIORITY_HIGH
            notBuilderBelowOreo.setSmallIcon(icon)
            notBuilderBelowOreo.setContentTitle(title)
            notBuilderBelowOreo.setContentText(message)
            //creating a notification manager
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //creating notification notify from notification manager since android <O are no having
            //notification channel creation
            notificationManager.notify(0, notBuilderBelowOreo.build())
            //
            //
        }
        //
        //code ends
    }
}