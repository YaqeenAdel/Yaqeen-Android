package com.cancer.yaqeen.data.features.home.schedule.routine_test.models

enum class ReminderBefore(val id: Int, val timeInMinutes: Int, val time: String, val timeDigits: Int, val isMoreThanOrEqualHour: Boolean = false) {
    MINUTES_0(id = 1, timeInMinutes = 0, time = "0", timeDigits = 0),
    MINUTES_3(id = 2, timeInMinutes = 3, time = "03", timeDigits = 3),
    MINUTES_5(id = 3, timeInMinutes = 5, time = "05", timeDigits = 5),
    MINUTES_10(id = 4, timeInMinutes = 10, time = "10", timeDigits = 10),
    MINUTES_15(id = 5, timeInMinutes = 15, time = "15", timeDigits = 15),
    MINUTES_20(id = 6, timeInMinutes = 20, time = "20", timeDigits = 20),
    MINUTES_30(id = 7, timeInMinutes = 30, time = "30", timeDigits = 30),
    MINUTES_45(id = 8, timeInMinutes = 45, time = "45", timeDigits =45),
    HOUR_1(id = 9, timeInMinutes = 60, time = "01", timeDigits = 1, isMoreThanOrEqualHour = true),
    HOUR_4(id = 10, timeInMinutes = 240, time = "04", timeDigits = 4, isMoreThanOrEqualHour = true),
    HOUR_12(id = 11, timeInMinutes = 720, time = "12", timeDigits = 12, isMoreThanOrEqualHour = true),
    HOUR_24(id = 12, timeInMinutes = 1440, time = "24", timeDigits = 24, isMoreThanOrEqualHour = true);

    companion object {
        fun getReminderBefore(id: Int): ReminderBefore? =
            when(id){
                MINUTES_0.id -> MINUTES_0
                MINUTES_3.id -> MINUTES_3
                MINUTES_5.id -> MINUTES_5
                MINUTES_10.id -> MINUTES_10
                MINUTES_15.id -> MINUTES_15
                MINUTES_20.id -> MINUTES_20
                MINUTES_30.id -> MINUTES_30
                MINUTES_45.id -> MINUTES_45
                HOUR_1.id -> HOUR_1
                HOUR_4.id -> HOUR_4
                HOUR_12.id -> HOUR_12
                HOUR_24.id -> HOUR_24
                else -> null
            }

        fun createReminderBefore(timeInMinutes: Int): ReminderBefore =
            when(timeInMinutes){
                MINUTES_0.timeInMinutes -> MINUTES_0
                MINUTES_3.timeInMinutes -> MINUTES_3
                MINUTES_5.timeInMinutes -> MINUTES_5
                MINUTES_10.timeInMinutes -> MINUTES_10
                MINUTES_15.timeInMinutes -> MINUTES_15
                MINUTES_20.timeInMinutes -> MINUTES_20
                MINUTES_30.timeInMinutes -> MINUTES_30
                MINUTES_45.timeInMinutes -> MINUTES_45
                HOUR_1.timeInMinutes -> HOUR_1
                HOUR_4.timeInMinutes -> HOUR_4
                HOUR_12.timeInMinutes -> HOUR_12
                HOUR_24.timeInMinutes -> HOUR_24
                else -> MINUTES_0
            }

    }
}