package com.shimitadouglas.marketcm.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shimitadouglas.marketcm.mains.ProductsHome

class BigTextNotificationGen(
    var context: Context,
    var titleBig: String,
    var titleSmall: String,
    var messageBig: String,
    var messageSmall: String,
    var iconBig: Bitmap,
    var iconSmall: Int,
    var summary: String
) : Application() {
    override fun onCreate() {
        super.onCreate()
        //call the create Notification here in a separate coroutine
        funCreateBigTextNotification()
        //
    }

    fun funCreateBigTextNotification() {
        //code begins
        //code begins
        //creating notification channel name and ID
        val notChannelID = "CHANNEL_ID_MARKET_CM"
        val notChannelName = "MARKET_CM_CHANNEL"
        //creating notification importance
        val notImportance = NotificationManager.IMPORTANCE_HIGH
        //converting the iconBig into bitmap for support large icon for big Style notification
        //
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //creating notification channel
            val notChannel = NotificationChannel(notChannelID, notChannelName, notImportance)
            //creating big style notification variable
            val notBigStyle = Notification.BigTextStyle()
            //defining the variables for the big text style
            notBigStyle.apply {
                bigText(messageBig)
                setBigContentTitle(titleBig)
                setSummaryText(summary)

            }
            //create notification builder
            val notificationBuilder = Notification.Builder(context, notChannelID)
            notificationBuilder.setContentText(messageSmall)
            notificationBuilder.setContentTitle(titleSmall)
            notificationBuilder.style = notBigStyle
            notificationBuilder.setLargeIcon(iconBig)
            notificationBuilder.setSmallIcon(iconSmall)
            notificationBuilder.setPriority(Notification.PRIORITY_HIGH)
            //creating a notification manager
            val managerNotification =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //assign create notification channels on the manager
            managerNotification.createNotificationChannel(notChannel)
            //assign notify on the manager about the builder
            managerNotification.notify(0, notificationBuilder.build())
            //

        } else {
            //android version is below Oreo
            //creating an intent that will be passed to the pending intent for processing
            val intent = Intent(context, ProductsHome::class.java)
            //creating a pendingIntent for Processing of the Intent creating
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            //creating the big style that will be passed onto the builder
            val notBigStyle = NotificationCompat.BigTextStyle()
            notBigStyle.bigText(messageBig)
            notBigStyle.setBigContentTitle(titleBig)
            notBigStyle.setSummaryText(summary)
            //creating the builder for versions below oreo
            val notBuilder = NotificationCompat.Builder(context, notChannelID)
            notBuilder.setStyle(notBigStyle)
            notBuilder.setContentText(messageSmall)
            notBuilder.setContentTitle(titleSmall)
            notBuilder.setLargeIcon(iconBig)
            notBuilder.setContentIntent(pendingIntent)
            notBuilder.setSmallIcon(iconSmall)
            //
            //creating a notification manager
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //notify the manager since android Oreo Downwards don't require creation of notification channels
            notificationManager.notify(0, notBuilder.build())
            //

            //code ends
        }
    }
}