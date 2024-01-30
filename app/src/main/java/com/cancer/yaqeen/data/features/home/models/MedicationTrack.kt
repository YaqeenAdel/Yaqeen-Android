package com.cancer.yaqeen.data.features.home.models

data class MedicationTrack(
    var medicationType: MedicationType? = null,
    var medicationName: String? = null,
    var unitType: UnitType? = null,
    var medicationAmount: String? = null,
    var periodTime: Time? = null,
    var specificDays: List<Day>? = null,
    var startDate: Long? = null,
    var reminderTime: ReminderTime? = null,
    var notes: String? = null,
)
