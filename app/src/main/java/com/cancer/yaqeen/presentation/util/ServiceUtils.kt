package com.cancer.yaqeen.presentation.util

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context
import android.os.PersistableBundle
import com.cancer.yaqeen.presentation.service.AlarmService
import java.util.Random
import java.util.concurrent.TimeUnit


fun scheduleJobServicePeriodically(context: Context, intervalTimeInMillis: Long = TimeUnit.MINUTES.toMillis(15), bundle: PersistableBundle) {
    val componentName = ComponentName(context, AlarmService::class.java)
    val jobInfo = JobInfo.Builder(Random().nextInt(), componentName)
        .setPersisted(true) // Keep the job even after reboot
        .setPeriodic(intervalTimeInMillis) // Run every 15 minutes (minimum interval allowed)
        .setExtras(bundle)
        .build()

    val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    jobScheduler.schedule(jobInfo)
}

fun scheduleJobService(context: Context, bundle: PersistableBundle) {
    val componentName = ComponentName(context, AlarmService::class.java)
    val jobInfo = JobInfo.Builder(Random().nextInt(), componentName)
//        .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
        .setPersisted(false)
        .setMinimumLatency(TimeUnit.MINUTES.toMillis(1)) // 1 minute delay
        .setOverrideDeadline(TimeUnit.MINUTES.toMillis(2)) // Maximum delay of 2 minutes
        .setExtras(bundle)
        .build()

    val jobScheduler = context.getSystemService(Context.JOB_SCHEDULER_SERVICE) as JobScheduler
    jobScheduler.schedule(jobInfo)
}