package com.cancer.yaqeen.data.network.error

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ErrorResponse(
    private val error: String?,
    private val message: String?,
    private val statusCode: Int = 0,
    private val isSuccess: Boolean = false,
    val errorMessage: String? = null
): Parcelable