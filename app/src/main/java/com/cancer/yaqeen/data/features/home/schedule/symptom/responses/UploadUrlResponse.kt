package com.cancer.yaqeen.data.features.home.schedule.symptom.responses

import com.google.gson.annotations.SerializedName

data class UploadUrlResponse(
    @SerializedName("getUploadUrl")
    val getUploadURL: GetUploadURLResponse?
)
data class GetUploadURLResponse (
    @SerializedName("signedUrl")
    val signedURL: String?,

    val path: String?
)