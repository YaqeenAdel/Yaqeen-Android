package com.cancer.yaqeen.data.features.home.schedule.medication.models

import java.util.Calendar

enum class DayEnum(val id: Int, val cronExpression: String, val dayId: Int) {
    SUN(0, "SUN", Calendar.SUNDAY),
    MON(1, "MON", Calendar.MONDAY),
    TUE(2, "TUE", Calendar.TUESDAY),
    WED(3, "WED", Calendar.WEDNESDAY),
    THU(4, "THU", Calendar.THURSDAY),
    FRI(5, "FRI", Calendar.FRIDAY),
    SAT(6, "SAT", Calendar.SATURDAY);

    companion object {
        fun getDay(id: Int): DayEnum{
            return when(id){
                SUN.id -> SUN
                MON.id -> MON
                TUE.id -> TUE
                WED.id -> WED
                THU.id -> THU
                FRI.id -> FRI
                else -> SAT
            }
        }
    }

}