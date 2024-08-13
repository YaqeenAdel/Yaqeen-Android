package com.cancer.yaqeen.data.features.home.schedule.medication.models

data class MedicationTrack(
    var medicationType: MedicationType? = null,
    var medicationName: String? = null,
    var unitType: UnitType? = null,
    var strengthAmount: String? = null,
    var dosageAmount: String? = null,
    var periodTime: Time? = null,
    var specificDays: List<Day>? = null,
    var startDate: Long? = null,
    var reminderTime: ReminderTime? = null,
    var reminderTime2: ReminderTime2? = null,
    var notes: String? = null,
    val editable: Boolean = false,
    val medicationId: Int? = null,
    var destinationId: Int? = null,
)
