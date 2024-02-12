package com.cancer.yaqeen.data.features.home.schedule.medication.models

enum class ScheduleType(val id: Int, val scheduleType: String) {
    MEDICATION(1, "medication"),
    SYMPTOMS(2, "symptoms"),
    ROUTINE_TESTS(3, "routine_test"),
    MEDICAL_REMINDER(4, "appointment");

    companion object {
        fun getTypeId(scheduleType: String): Int =
            when(scheduleType){
                MEDICATION.scheduleType -> MEDICATION.id
                SYMPTOMS.scheduleType -> SYMPTOMS.id
                ROUTINE_TESTS.scheduleType -> ROUTINE_TESTS.id
                else -> MEDICAL_REMINDER.id
            }
    }
}