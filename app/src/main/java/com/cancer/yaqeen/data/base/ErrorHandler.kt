package com.cancer.yaqeen.data.base

import com.cancer.yaqeen.data.network.error.ErrorEntity
import okhttp3.ResponseBody
import java.io.Reader

interface ErrorHandler {
    fun getError(throwable: Throwable): ErrorEntity

    fun getErrorResponseServer(errorBody: ResponseBody?, errorCode: Int): ErrorEntity

    fun<T> getErrorResponseServer(baseResponse: BaseResponse<T>): ErrorEntity
}