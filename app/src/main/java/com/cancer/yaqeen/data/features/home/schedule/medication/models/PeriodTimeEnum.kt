package com.cancer.yaqeen.data.features.home.schedule.medication.models

import java.util.concurrent.TimeUnit

enum class PeriodTimeEnum(val id: Int, val cronExpression: String, val timeInMillis: Long = 0L) {
    EVERY_DAY(1, "", TimeUnit.DAYS.toMillis(1)),
    EVERY_8_HOURS(2, "/8", TimeUnit.HOURS.toMillis(8)),
    EVERY_12_HOURS(3, "/12", TimeUnit.HOURS.toMillis(12)),
    DAY_AFTER_DAY(4, "", TimeUnit.DAYS.toMillis(2)),
    SPECIFIC_DAYS_OF_THE_WEEK(5, ""),
    EVERY_WEEK(6, "", TimeUnit.DAYS.toMillis(7)),
    EVERY_MONTH(7, "", TimeUnit.DAYS.toMillis(30)),
    EVERY_3_HOURS(8, "/3", TimeUnit.HOURS.toMillis(3)),
}