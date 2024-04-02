package com.cancer.yaqeen.presentation.service

import android.content.Context
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.cancer.yaqeen.presentation.util.NotificationUtils
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject

@HiltWorker
class ReminderWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted val params: WorkerParameters
) : Worker(context, params) {

    private val myNotificationManager: NotificationUtils = NotificationUtils(context)

    override fun doWork(): Result {

        Log.d("TAG", "doWork ReminderWorker: $myNotificationManager")
        myNotificationManager.notify(
            1,
            inputData.getString("title").toString(),
            inputData.getString("message").toString()
        )

        return Result.success()
    }
}