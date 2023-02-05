package com.shimitadouglas.marketcm.notifications

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.shimitadouglas.marketcm.mains.MainActivity
import com.shimitadouglas.marketcm.mains.ProductsHome
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class BigPictureNotificationMostLogin(
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
        val notChannelID = "CHANNEL_ID_MARKET_CM"
        val notChannelName = "MARKET_CM_CHANNEL"
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

            //creating a pending intent
            val pendingIntent: PendingIntent =
                PendingIntent.getActivity(
                    context,
                    0,
                    funIntentReturned(),
                    PendingIntent.FLAG_IMMUTABLE
                )
            //creating action
            val notificationAction: Notification.Action =
                Notification.Action(smallIcon, funButtonTitle(), pendingIntent)
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

    private fun funButtonTitle(): CharSequence? {
        //code begins
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            //user logged in
            return "continue"
            //
        }
        //user not logged in thus text of the notification button to login
        return "login"
        //

        //code ends
    }

    private fun funIntentReturned(): Intent? {
        //code begins
        //control the intent such that login activity if yes user==null else products home intent should be the one
        //intent products home
        val intentProductsHome = Intent(context, ProductsHome::class.java)
        intentProductsHome.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        //intent login activity
        val intentLogin = Intent(context, MainActivity::class.java)
        intentLogin.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        //
        val currentUser = FirebaseAuth.getInstance().currentUser
        //

        if (currentUser != null) {
            //user logged in take user to the products activity
            return intentProductsHome
            //
        }

        //user not logged in
        return intentLogin
        //code ends
    }


}