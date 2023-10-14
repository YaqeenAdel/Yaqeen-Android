package com.cancer.yaqeen.data.network.base

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

fun <T> flowStatus(call: suspend () -> DataState<T>): Flow<DataState<T>> =
    flow {
        emit(DataState.Loading())
        val responseStatus = call.invoke()
        emit(responseStatus)
    }
