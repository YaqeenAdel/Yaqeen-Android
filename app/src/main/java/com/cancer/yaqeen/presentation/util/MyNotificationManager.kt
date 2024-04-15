package com.cancer.yaqeen.presentation.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cancer.yaqeen.R

class MyNotificationManager private constructor(private val context: Context) {

    private lateinit var notification: Notification

    private val managerCompat: NotificationManagerCompat by lazy {
        NotificationManagerCompat.from(context.applicationContext)
    }

    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    private fun initNotificationManager(channelId: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                "YAQEEN_APP_Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.description = "YAQEEN_APP channel for app"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = context.getColor(R.color.purple_700)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    class Builder(val context: Context, channelId: String = "YAQEEN_APP_Notify") {

        private var builder: NotificationCompat.Builder

        private val mNotificationManager = MyNotificationManager(context)

        init {
            mNotificationManager.initNotificationManager(channelId)
            builder = NotificationCompat.Builder(context, channelId)
        }

        fun setDefaultsId(defaultsId: Int): Builder {
            builder.setDefaults(defaultsId)
            return this
        }

        fun enableColorized(isColorized: Boolean): Builder {
            builder.setColorized(isColorized)
            return this
        }

        fun setColorId(colorId: Int): Builder {
            builder.color = ContextCompat.getColor(context, colorId)
            return this
        }

        fun setTitle(title: String): Builder {
            builder.setContentTitle(title)
            return this
        }

        fun setText(text: String): Builder {
            builder.setContentText(text)
            return this
        }

        fun setSmallIconId(icon: Int): Builder {
            builder.setSmallIcon(icon)
            return this
        }

        fun setPriority(priority: Int): Builder {
            builder.priority = priority
            return this
        }

        fun setDefaultSound(): Builder {
            builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            return this
        }

        fun setSoundUri(soundUri: Uri): Builder {
            builder.setSound(soundUri)
            return this
        }

        fun enableAutoCanceling(autoCancel: Boolean): Builder {
            builder.setAutoCancel(autoCancel)
            return this
        }

        fun setCategory(categoryName: String): Builder {
            builder.setCategory(categoryName)
            return this
        }

        fun setGroup(groupName: String = "com.android.example.WORK_EMAIL"): Builder {
            builder.setGroup(groupName)
            return this
        }

        fun enableGroupSummary(isEnable: Boolean): Builder {
            builder.setGroupSummary(isEnable)
            return this
        }

        fun setStyle(style: NotificationCompat.Style): Builder {
            builder.setStyle(style)
            return this
        }
        fun setPendingIntent(pendingIntent: PendingIntent): Builder{
            builder.setContentIntent(pendingIntent)
            return this
        }
        fun addAction(iconId: Int = android.R.drawable.ic_menu_close_clear_cancel, title: String, pendingIntent: PendingIntent?): Builder{
            builder.addAction(iconId, title, pendingIntent)
            return this
        }

        fun setLargeIconAndShow(url: String, notificationId: Int) {
            Glide.with(context)
                .asBitmap()
                .load(url)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        builder.setLargeIcon(resource)
                        builder.setStyle(
                            NotificationCompat.BigPictureStyle().bigPicture(resource)
                        )

                        build().show(notificationId)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }

        fun build(): MyNotificationManager {
            val notification = builder.build()
            mNotificationManager.notification = notification
            return mNotificationManager
        }
    }

    fun show(notificationId: Int){
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }

        managerCompat.notify(notificationId, notification)
    }
}