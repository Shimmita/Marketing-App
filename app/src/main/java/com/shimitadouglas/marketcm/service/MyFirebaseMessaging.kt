package com.shimitadouglas.marketcm.service

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.shimitadouglas.marketcm.R
import com.shimitadouglas.marketcm.notifications.BigPictureNotificationMostLogin
import com.shimitadouglas.marketcm.notifications.BigTextNotificationGen

class MyFirebaseMessaging : FirebaseMessagingService() {
    companion object {
        private const val TAG = "MyFirebaseMessaging"
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        //get the title of the message
        val title = message.notification?.title
        //getting the body of the message
        val body = message.notification?.body
        //getting the image url passed as a payload
        val imageUrl = message.notification?.imageUrl
        //
        Log.d(TAG, "onMessageReceived: title:$title\nmessage:$body")
        //

        //obtain the image url that will be passed as a bitmap
        //using glide to convert the image uri into the imageBitmap
        //try-catch the bitmap image conversion
        try {
            val bitmapImage =
                Glide.with(this@MyFirebaseMessaging).asBitmap().load(imageUrl).submit().get()
            //call function to propel the showing of the big picture notification
            //display the notification on using the custom notifications already programmed by self
            funShowBigPicNotification(title, body, bitmapImage)
            //
        } catch (e: Exception) {
            Log.d(TAG, "onMessageReceived: error occurred->trigger BigTextGenNotification")
            //the error is mainly missing image thus trigger showing of the bigTextNotificationGen with long text of description
            funShowBigTextNotificationGen(title, body)
            //
        }


    }

    private fun funShowBigTextNotificationGen(title: String?, body: String?) {
        //code begins
        if (title != null) {
            if (body != null) {
                BigTextNotificationGen(
                    this@MyFirebaseMessaging,
                    title,
                    getString(R.string.tittle_small),
                    body,
                    getString(R.string.notif_from_admn),
                    BitmapFactory.decodeResource(resources, R.drawable.cart),
                    R.drawable.ic_cart,
                    getString(R.string.by_me)
                ).funCreateBigTextNotification()
            }
        }
        //code ends
    }

    private fun funShowBigPicNotification(title: String?, body: String?, bitmapImage: Bitmap?) {
        //code begins
        if (bitmapImage != null) {
            if (title != null) {
                if (body != null) {
                    BigPictureNotificationMostLogin(
                        this@MyFirebaseMessaging,
                        bitmapImage,
                        title,
                        body,
                        R.drawable.cart, getString(R.string.by_me),
                        getString(R.string.reminder_big)
                    ).funCreateBigPictureNotification()
                }
            }
        }
        //code ends
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d(TAG, "onNewToken:$token")
    }
}