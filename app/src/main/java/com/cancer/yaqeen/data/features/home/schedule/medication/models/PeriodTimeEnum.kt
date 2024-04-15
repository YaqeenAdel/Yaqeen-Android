package com.cancer.yaqeen.data.features.home.schedule.medication.models

enum class PeriodTimeEnum(val id: Int, val cronExpression: String) {
    EVERY_DAY(1, ""),
    EVERY_8_HOURS(2, "/8"),
    EVERY_12_HOURS(3, "/12"),
    DAY_AFTER_DAY(4, ""),
    SPECIFIC_DAYS_OF_THE_WEEK(5, ""),
    EVERY_WEEK(6, ""),
    EVERY_MONTH(7, "")
}