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
import android.os.Bundle
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
            title = context.getString(R.string.ignore),
            pendingIntent = actionPendingIntent
        )

        notificationBuilder.build().show(notificationId)
    }

    fun notify(title: String, details: String, notificationId: Int) {

        val notificationBuilder = MyNotificationManager.Builder(context)
            .setDefaultsId(Notification.DEFAULT_ALL)
            .enableColorized(true)
            .setColorId(R.color.purple_700)
            .setTitle(title)
            .setText(details)
            .setSmallIconId(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setDefaultSound()
            .enableAutoCanceling(true)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setGroup(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(details)
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
            title = context.getString(R.string.ignore),
            pendingIntent = actionPendingIntent
        )

        notificationBuilder.build().show(notificationId)
    }

    fun createNotificationService(title: String, details: String): Notification {

        val notificationBuilder = MyNotificationManager.Builder(
            context = context,
            channelId = "YAQEEN_APP_Notify_Service",
            channelName = "YAQEEN_APP Service",
            mute = true
        )
            .setDefaultsId(Notification.DEFAULT_ALL)
            .enableColorized(true)
            .setColorId(R.color.crow_black_blue)
            .setTitle(title)
            .setText(details)
            .setSmallIconId(R.mipmap.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setSilent()
            .enableAutoCanceling(false)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setGroup(title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(details)
                    .setBigContentTitle(title)
            )

        return  notificationBuilder.createNotification()
    }

    fun cancelNotification(id: Int) {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(id)
    }

    fun cancelAllNotification() {
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancelAll()
    }
}