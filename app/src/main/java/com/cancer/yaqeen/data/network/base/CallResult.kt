package com.cancer.yaqeen.data.network.base

import android.util.Log
import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.presentation.util.Constants
import retrofit2.Response

suspend fun <T> getResultRestAPI(call: suspend () -> Response<T>): DataState<T> {
    try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null)
                return DataState.Success(body)
        }

        return DataState.Failed()

    } catch (e: Exception) {
        return DataState.Error(null)
    }
}

suspend fun <M, T> getResultRestAPI(mapper: Mapper<T, M>, call: suspend () -> Response<T>): DataState<M> {
    try {
        val response = call()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null)
                return DataState.Success(mapper.map(body))
        }

        return DataState.Failed()

    } catch (e: Exception) {
        return DataState.Error(null)
    }
}

suspend fun <T> getResultDao(call: suspend () -> T): DataState<T> {
    return try {
        val response = call()
        DataState.Success(response)

    } catch (e: Exception) {
        return DataState.Error(null)
    }
}

suspend fun <M, T> getResultDao(mapper: Mapper<T, M>, call: suspend () -> T): DataState<M> {
    return try {
        val result = call()
        DataState.Success(mapper.map(result))
    } catch (e: Exception) {
        DataState.Failed()
    }
}
