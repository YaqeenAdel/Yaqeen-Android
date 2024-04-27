package com.cancer.yaqeen.presentation.service

import android.content.Context
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.workDataOf
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.presentation.util.Constants
import java.util.concurrent.TimeUnit

class WorkerRequest private constructor(){

    class Builder{
        private var startDateTime: Long = 0L
        private var periodTimeId: Int? = null
        private var title: String = ""
        private var body: String = ""
        private var objectJsonKey: String = ""
        private var objectJsonValue: String = ""

        fun setStartDateTime(dateTime: Long): Builder {
            this.startDateTime = dateTime
            return this
        }

        fun setPeriodTime(periodTimeId: Int?): Builder{
            this.periodTimeId = periodTimeId
            return this
        }

        fun setTitle(title: String): Builder{
            this.title = title
            return this
        }

        fun setBody(body: String): Builder{
            this.body = body
            return this
        }

        fun setObjectKey(objectJsonKey: String): Builder{
            this.objectJsonKey = objectJsonKey
            return this
        }

        fun<T> setObject(obj: T): Builder{
            this.objectJsonValue = obj.toJson()
            return this
        }

        fun build(): PeriodicWorkRequest {
            return buildPeriodicWorkRequestBuilder(periodTimeId)
                .setInitialDelay(startDateTime, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        Constants.TITLE_KEY to title,
                        Constants.BODY_KEY to body,
                        objectJsonKey to objectJsonValue
                    )
                ).build()
        }

        fun buildOneTimeWork(): OneTimeWorkRequest {
            return OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(startDateTime, TimeUnit.MILLISECONDS)
                .setInputData(
                    workDataOf(
                        Constants.TITLE_KEY to title,
                        Constants.BODY_KEY to body,
                        objectJsonKey to objectJsonValue
                    )
                ).build()
        }


        private fun buildPeriodicWorkRequestBuilder(periodTimeId: Int?): PeriodicWorkRequest.Builder =
            when (periodTimeId) {
                PeriodTimeEnum.EVERY_DAY.id -> {
                    initPeriodicWorkEveryXDays(1)
                }

                PeriodTimeEnum.EVERY_8_HOURS.id -> {
                    initPeriodicWorkEveryXHours(8)
                }

                PeriodTimeEnum.EVERY_12_HOURS.id -> {
                    initPeriodicWorkEveryXHours(12)
                }

                PeriodTimeEnum.DAY_AFTER_DAY.id -> {
                    initPeriodicWorkEveryXDays(2)
                }

                PeriodTimeEnum.EVERY_WEEK.id -> {
                    initPeriodicWorkEveryXDays(7)
                }

                PeriodTimeEnum.EVERY_MONTH.id -> {
                    initPeriodicWorkEveryXDays(30)
                }

                PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id -> {
                    initPeriodicWorkEveryXDays(7)
                }

                else -> {
                    initPeriodicWorkEveryXDays(1)
                }
            }

        private fun initPeriodicWorkEveryXDays(repeatInterval: Long): PeriodicWorkRequest.Builder =
            PeriodicWorkRequestBuilder<ReminderWorker>(
                repeatInterval,
                TimeUnit.DAYS
            )

        private fun initPeriodicWorkEveryXHours(repeatInterval: Long): PeriodicWorkRequest.Builder =
            PeriodicWorkRequestBuilder<ReminderWorker>(
                repeatInterval,
                TimeUnit.HOURS
            )
    }
}