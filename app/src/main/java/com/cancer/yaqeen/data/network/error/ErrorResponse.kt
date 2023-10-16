package com.cancer.yaqeen.data.network.error

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@kotlinx.android.parcel.Parcelize
data class ErrorResponse(
    val error: String?,
    val message: String?,
    @SerializedName("status")
    val statusCode: Int,
    private val errorsList: List<String>? = listOf(),
    @SerializedName("meta") val meta: Meta? = null
): Parcelable {
    var errorsStr: String?
        get() = errorsList?.joinToString("\n") ?: error ?: message
        set(value) {""}
}

@kotlinx.android.parcel.Parcelize
data class Meta(
    @SerializedName("error_message") val errorMessage: String,
    @SerializedName("error_type") val errorType: String,
    @SerializedName("code") val errorCode: Int
): Parcelable