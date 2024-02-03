package com.cancer.yaqeen.data.features.home.schedule.medication.models

enum class DayEnum(val id: Int, val cronExpression: String) {
    SUN(0, "SUN"),
    MON(1, "MON"),
    TUE(2, "TUE"),
    WED(3, "WED"),
    THU(4, "THU"),
    FRI(5, "FRI"),
    SAT(6, "SAT")
}