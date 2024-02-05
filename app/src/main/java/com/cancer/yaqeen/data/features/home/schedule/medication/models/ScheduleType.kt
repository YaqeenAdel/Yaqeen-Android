package com.cancer.yaqeen.data.features.home.schedule.medication.models

enum class ScheduleType(val id: Int, val scheduleType: String) {
    MEDICATION(1, "medication"),
    SYMPTOMS(2, "symptoms"),
    ROUTINE_TESTS(3, "Routine Tests"),
    MEDICAL_REMINDER(4, "Medical Reminder")
}