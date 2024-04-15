package com.cancer.yaqeen.presentation.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Data
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.utils.fromJson
import com.cancer.yaqeen.presentation.receiver.NotificationReceiver
import com.cancer.yaqeen.presentation.util.Constants.IGNORE_NOTIFICATION_ACTION
import com.cancer.yaqeen.presentation.util.Constants.ACTION_IGNORE_BUTTON_ID
import com.cancer.yaqeen.presentation.util.Constants.BODY_KEY
import com.cancer.yaqeen.presentation.util.Constants.MEDICAL_APPOINTMENT
import com.cancer.yaqeen.presentation.util.Constants.MEDICATION
import com.cancer.yaqeen.presentation.util.Constants.NOTIFICATION_ID
import com.cancer.yaqeen.presentation.util.Constants.ROUTINE_TEST
import com.cancer.yaqeen.presentation.util.Constants.TITLE_KEY
import javax.inject.Inject


class NotificationUtils @Inject constructor(val context: Context) {

    fun notify(data: Data) {
        val title = data.getString(TITLE_KEY).toString()
        val text = data.getString(BODY_KEY).toString()
        val medication = data.getString(MEDICATION).toString().fromJson(MedicationDB::class.java)
        val routineTest = data.getString(ROUTINE_TEST).toString().fromJson(RoutineTestDB::class.java)
        val medicalAppointment = data.getString(MEDICAL_APPOINTMENT).toString().fromJson(MedicalAppointmentDB::class.java)

        val notificationId = medication?.medicationId ?: routineTest?.routineTestId ?: medicalAppointment?.medicalAppointmentId ?: 1

        val notificationBuilder = MyNotificationManager.Builder(context)
            .setDefaultsId(Notification.DEFAULT_ALL)
            .enableColorized(true)
            .setColorId(R.color.purple_700)
            .setTitle(title)
            .setText(text)
            .setSmallIconId(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaultSound()
            .enableAutoCanceling(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setGroup(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(text)
                    .setBigContentTitle(title)
            )

        val actionIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = IGNORE_NOTIFICATION_ACTION
            putExtra(NOTIFICATION_ID, notificationId)
        }

        val actionPendingIntent = PendingIntent.getBroadcast(
            context,
            ACTION_IGNORE_BUTTON_ID,
            actionIntent,
            PendingIntent.FLAG_CANCEL_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        notificationBuilder.addAction(
            title = "Ignore",
            pendingIntent = actionPendingIntent
        )

        notificationBuilder.build().show(notificationId)
    }

    private lateinit var builder: NotificationCompat.Builder
    private val notificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    fun notify(idNotify: Int, title: String?, body: String?, attachmentUrl: String? = null, pendingIntent: PendingIntent? = null) {
        val notificationChannelId = "IMAKE_APP_MALL_Notify"
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val GROUP_KEY_WORK_EMAIL = "com.android.example.WORK_EMAIL"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                notificationChannelId,
                "IMAKE_APP_MALL Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )

            notificationChannel.description = "IMAKE_APP_MALL channel for app"
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = context.getColor(R.color.purple_700)
            notificationChannel.enableVibration(true)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        builder = NotificationCompat.Builder(context, notificationChannelId)
            .setDefaults(Notification.DEFAULT_ALL)
            .setColorized(true)
            .setColor(ContextCompat.getColor(context, R.color.purple_700))
            .setContentTitle(title)
            .setContentText(body)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSound(defaultSoundUri)
            .setAutoCancel(true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setGroup(GROUP_KEY_WORK_EMAIL)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(body)
                    .setBigContentTitle(title)
            )

        pendingIntent?.let {
            builder.setContentIntent(pendingIntent)
        }
        attachmentUrl?.let {
            Glide.with(context)
                .asBitmap()
                .load(attachmentUrl)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                        //largeIcon
                        builder.setLargeIcon(resource)
                        //Big Picture
                        builder.setStyle(
                            NotificationCompat.BigPictureStyle().bigPicture(resource)
                        )
                        val notification = builder.build()
                        notificationManager.notify(idNotify, notification)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }

        builder.addAction("Ignore")


        val notification: Notification =
            builder.build()

        val manager = NotificationManagerCompat.from(context.applicationContext)
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
        manager.notify(idNotify, notification)
        val intent = Intent("NEW_NOTIFICATION")
        intent.putExtra("newNotification", true)
        context.sendBroadcast(intent)
    }

    fun cancelNotification(id: Int) {
        notificationManager.cancel(id)
    }

    fun cancelAllNotification() {
        notificationManager.cancelAll()
    }

    fun NotificationCompat.Builder.addAction(actionName: String): NotificationCompat.Builder{
        return addAction(
            android.R.drawable.ic_menu_close_clear_cancel, actionName, null
        )
    }

    internal interface NotificationListener {
        fun onNotifyReceived()
    }
}