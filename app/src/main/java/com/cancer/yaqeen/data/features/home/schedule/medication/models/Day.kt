package com.cancer.yaqeen.data.features.home.schedule.medication.models

data class Day(
    val id: Int,
    val name: String,
    var selected: Boolean = false,
    val cronExpression: String = ""
)
