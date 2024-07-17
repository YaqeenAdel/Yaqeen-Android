package com.cancer.yaqeen.presentation.service

import android.app.Notification
import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.util.Log
import androidx.core.app.ServiceCompat
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.presentation.util.Constants.NOTIFICATION_REMINDER_SERVICE_ID
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_SCHEDULES_ACTION_KEY
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_REMINDED_SCHEDULES_ACTION
import com.cancer.yaqeen.presentation.util.NotificationUtils
import com.cancer.yaqeen.presentation.util.getWelcomeMessage
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class AlarmService: JobService() {

    private val workerReminder: ReminderManager by lazy {
        WorkerReminder(this)
    }

    private val notificationUtils: NotificationUtils by lazy {
        NotificationUtils(this)
    }

    @Inject
    lateinit var prefEncryptionUtil: SharedPrefEncryptionUtil

    override fun onStartJob(params: JobParameters?): Boolean {
        val actionName = params?.extras?.getString(UPDATE_LOCAL_SCHEDULES_ACTION_KEY, UPDATE_LOCAL_REMINDED_SCHEDULES_ACTION) ?: UPDATE_LOCAL_REMINDED_SCHEDULES_ACTION

        workerReminder.setReminder(
            TimeUnit.MINUTES.toMillis(5L),
            actionName
        )

        val motivatingPhrases = resources.getStringArray(R.array.motivating_phrases_array).toList()
        val phrase = motivatingPhrases.random() ?: resources.getString(R.string.reminder_running)
        val user = prefEncryptionUtil.getModelData(
            SharedPrefEncryptionUtil.PREF_USER,
            User::class.java
        )
        val userFirstName = user?.name?.split(" ")?.firstOrNull() ?: ""

        val notification = notificationUtils.createNotificationService(
            title = "${getWelcomeMessage(resources)} $userFirstName",
            details = phrase
        )

        startForegroundService(notification)

        return true
    }

    private fun startForegroundService(notification: Notification) {
        try {
            ServiceCompat.startForeground(
                this,
                NOTIFICATION_REMINDER_SERVICE_ID,
                notification,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_SYSTEM_EXEMPTED
                }
                else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
                } else {
                    0
                },
            )
        } catch (e: Exception) {
            try {
                startForeground(NOTIFICATION_REMINDER_SERVICE_ID, notification)
            }catch (_: Exception){

            }
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && e is ForegroundServiceStartNotAllowedException) {
//                // App not in a valid state to start foreground service
//                // (e.g. started from bg)
//            }
            // ...
        }
    }


    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("NotificationReceiver", "AlarmService: onStopJob")
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(NOTIFICATION_REMINDER_SERVICE_ID)
        return false
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("NotificationReceiver", "AlarmService: onCreate")
    }

    override fun onDestroy() {
        super.onDestroy()
    }


}