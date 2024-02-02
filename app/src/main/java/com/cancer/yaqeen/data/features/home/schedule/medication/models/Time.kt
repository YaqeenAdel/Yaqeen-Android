package com.cancer.yaqeen.data.features.home.schedule.medication.models

data class Time(
    val id: Int,
    val time: String,
    val cronExpression: String = ""
)
