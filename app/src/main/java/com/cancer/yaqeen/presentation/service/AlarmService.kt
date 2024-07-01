package com.cancer.yaqeen.presentation.service

import android.app.NotificationManager
import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Context
import android.content.Intent
import android.util.Log
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
        startForeground(NOTIFICATION_REMINDER_SERVICE_ID, notification)


        return true
    }


    override fun onStopJob(params: JobParameters?): Boolean {
        Log.d("NotificationReceiver", "AlarmService: onStopJob")
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(1)
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