package com.cancer.yaqeen.presentation.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.presentation.service.AlarmService
import com.cancer.yaqeen.presentation.service.ReminderManager
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_SCHEDULES_ACTION_KEY
import java.util.Random
import java.util.concurrent.TimeUnit


fun scheduleJobService(context: Context, intervalTimeInMillis: Long, bundle: PersistableBundle) {
    val componentName = ComponentName(context, AlarmService::class.java)
    val jobInfo = JobInfo.Builder(Random().nextInt(), componentName)
        .setPersisted(true) // Persist across reboots
        .setPeriodic(intervalTimeInMillis) // Run every 15 minutes (minimum interval allowed)
        .setExtras(bundle)
        .build()

    val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    jobScheduler.schedule(jobInfo)
}