package com.cancer.yaqeen.data.features.home.schedule.medication.models

import com.cancer.yaqeen.R

enum class MedicationTypeEnum(val id: Int, val iconId: Int) {
    CAPSULE(1, R.drawable.ic_capsule),
    PILLS(2, R.drawable.ic_pills),
    LIQUID(3, R.drawable.ic_liquid),
    INJECTION(4, R.drawable.ic_injection)
}