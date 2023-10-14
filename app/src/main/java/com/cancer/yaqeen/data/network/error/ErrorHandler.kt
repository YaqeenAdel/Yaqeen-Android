package com.cancer.yaqeen.data.network.error

import com.cancer.yaqeen.data.base.BaseResponse
import okhttp3.ResponseBody
import java.io.Reader

interface ErrorHandler {
    fun getError(throwable: Throwable): ErrorEntity

    fun getErrorResponseServer(errorBody: ResponseBody?, errorCode: Int): ErrorEntity

    fun<T> getErrorResponseServer(baseResponse: BaseResponse<T>): ErrorEntity

    fun getErrorResponseServer(charStream: Reader?): ErrorResponse
}