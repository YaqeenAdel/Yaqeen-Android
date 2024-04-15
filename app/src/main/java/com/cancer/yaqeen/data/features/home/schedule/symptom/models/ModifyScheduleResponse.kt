package com.cancer.yaqeen.data.features.home.schedule.symptom.models

data class ModifyScheduleResponse(
    val signedURL: String? = null,
    val path: String? = null,
    var photoIsUploaded: Boolean = false,
    var scheduleIsModified: Boolean = false,
    val routineTestId: Int? = null
)
