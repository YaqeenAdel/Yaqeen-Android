package com.cancer.yaqeen.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.presentation.ui.MainActivity
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.NotificationUtils
import com.cancer.yaqeen.presentation.util.runWorker
import java.util.concurrent.TimeUnit

class AlarmService: JobService() {

    private val workerReminderPeriodically: ReminderManager by lazy {
        WorkerReminder(this)
    }

    private val notificationUtils: NotificationUtils by lazy {
        NotificationUtils(this)
    }

    override fun onStartJob(p0: JobParameters?): Boolean {
        Log.d("NotificationReceiver", "AlarmService: onStartJob")


        workerReminderPeriodically.runWorker()
        sendNotification()
        return true
    }


    override fun onStopJob(p0: JobParameters?): Boolean {
        Log.d("NotificationReceiver", "AlarmService: onStopJob")

        return false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        notificationUtils.notify("title", "detailsMedication", 13453)
        Log.d("NotificationReceiver", "AlarmService: onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    private fun sendNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannelId = "YOUR_NOTIFICATION_CHANNEL_ID"
            val channel = NotificationChannel(
                notificationChannelId,
                "My Service",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            val notification = NotificationCompat.Builder(this, notificationChannelId)
                .setContentTitle("My Service")
                .setContentText("Running...")
                .setSmallIcon(R.drawable.ic_notification)
                .build()

            startForeground(1, notification)
        }
    }

}