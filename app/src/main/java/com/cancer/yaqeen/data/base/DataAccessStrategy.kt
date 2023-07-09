package com.cancer.yaqeen.data.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> flowResponseAPI(networkCall: suspend () -> Resource<T>): Flow<Resource<T>> =
    flow {
        emit(Resource.Loading())
        val responseStatus = networkCall.invoke()
        emit(responseStatus)
    }
