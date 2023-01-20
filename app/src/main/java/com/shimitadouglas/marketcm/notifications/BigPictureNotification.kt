package com.shimitadouglas.marketcm.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.shimitadouglas.marketcm.mains.ProductsHome
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BigPictureNotification(
    var context: Context,
    var iconBitmap: Bitmap,
    var title: String,
    var text: String,
    var smallIcon: Int,
    var summary: String,
    var titleBig: String

) : Application() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        //call function create big picture notification
        GlobalScope.launch(Dispatchers.Default)
        {
            funCreateBigPictureNotification()
        }
        //
    }

    fun funCreateBigPictureNotification() {

        //code begins
        var notChannelID = "CHANNEL_ID_MARKET_CM"
        var notChannelName = "MARKET_CM_CHANNEL"
        val notImportance = NotificationManager.IMPORTANCE_HIGH
        //check if android is greater than Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //create the notification channel
            val notificationChannel =
                NotificationChannel(notChannelID, notChannelName, notImportance)
            //creating a big picture style notification
            val bigPictureStyle = Notification.BigPictureStyle()
            //customize the bigPictureStyle
            bigPictureStyle.bigPicture(iconBitmap)
            bigPictureStyle.setSummaryText(summary)
            bigPictureStyle.setBigContentTitle(titleBig)

            // creating an intent when clicked an action should be triggered
            val intent = Intent(context, ProductsHome::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or (Intent.FLAG_ACTIVITY_NEW_TASK)
            //creating a pending intent
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            //creating action
            val notificationAction: Notification.Action =
                Notification.Action(smallIcon, "view", pendingIntent)
            //creating a notification Builder
            val notificationBuilder: Notification.Builder =
                Notification.Builder(context, notChannelID)
            notificationBuilder.style = bigPictureStyle
            notificationBuilder.addAction(notificationAction)
            notificationBuilder.setContentTitle(title)
            notificationBuilder.setSmallIcon(smallIcon)
            notificationBuilder.setContentText(text)
            notificationBuilder.setContentIntent(pendingIntent)
            //creating a notification Manager
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //creating channels from the notification
            notificationManager.createNotificationChannel(notificationChannel)
            //create notify
            notificationManager.notify(0, notificationBuilder.build())
            //
        } else {
            //android is below Oreo
            val bigPicture = NotificationCompat.BigPictureStyle()
            bigPicture.bigPicture(iconBitmap)
            bigPicture.setSummaryText(summary)
            bigPicture.setBigContentTitle(titleBig)

            val notBuilder = NotificationCompat.Builder(context, notChannelID)
            notBuilder.setSmallIcon(smallIcon)
            notBuilder.setStyle(bigPicture)
            notBuilder.setContentTitle(title)
            notBuilder.setContentText(text)
            notBuilder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            val manager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(0, notBuilder.build())
        }
        //code ends
    }


}