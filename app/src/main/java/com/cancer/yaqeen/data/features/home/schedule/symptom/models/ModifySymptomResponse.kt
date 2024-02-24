package com.cancer.yaqeen.data.features.home.schedule.symptom.models

data class ModifySymptomResponse(
    val signedURL: String? = null,
    val path: String? = null,
    var photoIsUploaded: Boolean = false,
    var symptomIsAdded: Boolean = false,
)
