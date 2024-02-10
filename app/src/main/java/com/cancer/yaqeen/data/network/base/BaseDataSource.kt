package com.cancer.yaqeen.data.network.base

import android.util.Log
import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import retrofit2.Response

abstract class BaseDataSource(
    private val errorHandler: ErrorHandlerImpl
) {
    protected suspend fun <T> getResultRestAPI(call: suspend () -> Response<T>): DataState<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("TAG", "getResultRestAPI body: $body")
                return if (body != null)
                    DataState.Success(body)
                else
                    DataState.Failed()
            }

            Log.d("TAG", "getResultRestAPI error: ${response.errorBody()}")
            return error(errorHandler.getErrorResponseServer(response.errorBody(), response.code()))

        } catch (e: Exception) {
            Log.d("TAG", "getResultRestAPI Exception: $e")
            return error(errorHandler.getError(e))
        }
    }

    protected suspend fun <M, T> getResultRestAPI(mapper: Mapper<T, M>, call: suspend () -> Response<T>): DataState<M> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                Log.d("TAG", "getResultRestAPI body: $body")
                return if (body != null)
                    DataState.Success(mapper.map(body))
                else
                    DataState.Failed()
            }
            Log.d("TAG", "getResultRestAPI error: ${response.errorBody()}")

            return error(errorHandler.getErrorResponseServer(response.errorBody(), response.code()))

        } catch (e: Exception) {
            Log.d("TAG", "getResultRestAPI Exception: $e")
            return error(errorHandler.getError(e))
        }
    }


    protected suspend fun <T> getResultDao(call: suspend () -> T): DataState<T> {
        return try {
            val response = call()
            DataState.Success(response)

        } catch (e: Exception) {
            error(errorHandler.getError(e))
        }
    }

    protected suspend fun <M, T> getResultDao(mapper: Mapper<T, M>, call: suspend () -> T): DataState<M> {
        return try {
            val result = call()
            DataState.Success(mapper.map(result))
        } catch (e: Exception) {
            DataState.Failed()
        }
    }

    private fun <T> error(
        error: ErrorEntity? = null
    ): DataState<T> {
        return DataState.Error(error)
    }
}