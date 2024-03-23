package com.cancer.yaqeen.data.network.error

import android.content.Context
import android.database.StaleDataException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import com.cancer.yaqeen.data.base.BaseResponse
import com.cancer.yaqeen.data.network.NoConnectivityException
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import retrofit2.HttpException
import java.io.FileNotFoundException
import java.io.IOException
import java.io.Reader
import java.net.HttpURLConnection
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeoutException

class ErrorHandlerImpl: ErrorHandler {

    override fun getError(throwable: Throwable): ErrorEntity {
        //400
        /*

okhttp.OkHttpClient      I  {"error":"Could not verify JWT: JWTExpired","path":"$","code":"invalid-jwt"}
         */
        return when (throwable) {
            is NoConnectivityException, is UnknownHostException -> ErrorEntity.ApiError.InternetConnection

            is TimeoutException, is SocketTimeoutException -> ErrorEntity.ApiError.TimeOutNetwork

            is IllegalArgumentException -> ErrorEntity.ApiError.BadRequestNetwork

            is JsonSyntaxException -> ErrorEntity.ApiError.BadResponseNetwork

            is IOException -> ErrorEntity.ApiError.Network

            is HttpException -> {
                when (throwable.code()) {
                    // no cache found in case of no network, thrown by retrofit -> treated as network error
//                    DataConstants.Network.HttpStatusCode.UNSATISFIABLE_REQUEST -> ErrorEntity.Network

                    // not found
                    HttpURLConnection.HTTP_NOT_FOUND -> ErrorEntity.ApiError.NotFound(
                        throwable.message()
                    )

                    // access denied
                    HttpURLConnection.HTTP_FORBIDDEN -> ErrorEntity.ApiError.AccessDenied

                    // unavailable service
                    HttpURLConnection.HTTP_UNAVAILABLE -> ErrorEntity.ApiError.ServiceUnavailable

                    // unavailable service
                    HttpURLConnection.HTTP_UNAUTHORIZED -> ErrorEntity.ApiError.AccessDenied

                    // all the others will be treated as unknown error
                    else -> ErrorEntity.ApiError.Unknown(throwable.message())
                }
            }

            is SQLiteConstraintException -> ErrorEntity.DatabaseError.Unknown
            is SQLiteException -> ErrorEntity.DatabaseError.Unknown
            is StaleDataException -> ErrorEntity.DatabaseError.Unknown

            is FileNotFoundException -> ErrorEntity.FileError.NotFound("")

            else -> ErrorEntity.ApiError.Unknown("")
        }
    }


    override fun getErrorResponseServer(errorBody: ResponseBody?, errorCode: Int): ErrorEntity {
        return when(errorCode){
            HttpURLConnection.HTTP_UNAUTHORIZED ->
                ErrorEntity.ApiError.AccessDenied
            else -> {
                val gson = Gson()

                val type = object : TypeToken<ErrorResponse?>() {}.type
                val errorResponse: ErrorResponse? =
                    gson.fromJson(errorBody?.charStream(), type)

                if (errorResponse != null)
                    ErrorEntity.ApiError.ServerErrorResponse(errorResponse)
                else
                    ErrorEntity.ApiError.ServerErrorResponse(getErrorResponseServer(errorBody?.charStream()))
            }
        }
    }

    override fun <T> getErrorResponseServer(baseResponse: BaseResponse<T>): ErrorEntity =
        ErrorEntity.ApiError.ServerErrorResponse(
            baseResponse.run {
                ErrorResponse(
                    error = null,
                    message = null,
                    statusCode = statusCode ?: 0
                )
            }
        )

    override fun getErrorResponseServer(charStream: Reader?): ErrorResponse {
        val gson = Gson()

        val errorMessage = gson.fromJson(charStream, String::class.java)

        return ErrorResponse(
            error = errorMessage,
            message = errorMessage,
            statusCode = 0
        ).apply {
            errorsStr = errorMessage
        }
    }

}