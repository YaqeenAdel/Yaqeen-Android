package com.cancer.yaqeen.data.base

import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import retrofit2.Response

abstract class BaseDataSource(
    private val errorHandler: ErrorHandlerImpl
) {
    protected suspend fun <T> getResultRemoteAPI(call: suspend () -> Response<T>): Resource<T> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                return if (body != null)
                    Resource.Success(body)
                else
                    Resource.Failed()
            }

            return error(errorHandler.getErrorResponseServer(response.errorBody(), response.code()))

        } catch (e: Exception) {
            return error(errorHandler.getError(e))
        }
    }

    protected suspend fun <M, T> getResultRemote(mapper: Mapper<T, M>, call: suspend () -> Response<T>): Resource<M> {
        try {
            val response = call()
            if (response.isSuccessful) {
                val body = response.body()
                return if (body != null)
                    Resource.Success(mapper.map(body))
                else
                    Resource.Failed()
            }

            return error(errorHandler.getErrorResponseServer(response.errorBody(), response.code()))

        } catch (e: Exception) {
            return error(errorHandler.getError(e))
        }
    }


    protected suspend fun <M, T> getResultDao(mapper: Mapper<T, M>, call: suspend () -> T): Resource<M> {
        return try {
            val result = call()
            Resource.Success(mapper.map(result))
        } catch (e: Exception) {
            Resource.Failed()
        }
    }

    private fun <T> error(
        error: ErrorEntity? = null
    ): Resource<T> {
        return Resource.Error(error)
    }
}