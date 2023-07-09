package com.cancer.yaqeen.data.base

import com.google.gson.annotations.SerializedName
import javax.inject.Inject

class BaseResponse<T> @Inject constructor() {
    val isSuccess: Boolean = false
    val successMessage: String? = null
    val statusCode: Int? = null
    var data: T? = null
    val errorMessage: String? = null
}