package com.cancer.yaqeen.presentation.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.presentation.service.AlarmService
import com.cancer.yaqeen.presentation.service.ReminderManager
import java.util.Random
import java.util.concurrent.TimeUnit


fun scheduleJobService(context: Context, intervalTimeInMillis: Long) {
    val componentName = ComponentName(context, AlarmService::class.java)
    val jobInfo = JobInfo.Builder(Random().nextInt(), componentName)
        .setPersisted(true) // Persist across reboots
        .setPeriodic(intervalTimeInMillis) // Run every 15 minutes (minimum interval allowed)
        .build()

    val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    jobScheduler.schedule(jobInfo)
}

fun ReminderManager.runWorker(){
    val timeDelayInMilliSeconds = TimeUnit.MINUTES.toMillis(5L)
    val currentTimeInMilliSeconds = System.currentTimeMillis()
    val workRunningInMilliSeconds = currentTimeInMilliSeconds + timeDelayInMilliSeconds

    val periodReminderId = setPeriodReminder(
        timeDelayInMilliSeconds,
        PeriodTimeEnum.EVERY_3_HOURS.id,
        Constants.UPDATE_LOCAL_SCHEDULES_ACTION
    )
}