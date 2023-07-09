package com.cancer.yaqeen.data.network.error

import android.content.Context
import android.database.StaleDataException
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteException
import android.util.Log
import com.cancer.yaqeen.data.base.BaseResponse
import com.cancer.yaqeen.data.base.ErrorHandler
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

class ErrorHandlerImpl constructor(private val context: Context) : ErrorHandler {

    override fun getError(throwable: Throwable): ErrorEntity {
        return when (throwable) {
            is NoConnectivityException, is UnknownHostException -> ErrorEntity.ApiError.InternetConnection("failedConnection"
//                context.getString(R.string.failedConnection)
            )
            is TimeoutException, is SocketTimeoutException -> ErrorEntity.ApiError.TimeOutNetwork("timeoutExceeded"
//                context.getString(R.string.timeoutExceeded)
            )
            is IllegalArgumentException -> ErrorEntity.ApiError.BadRequestNetwork("missing_data"
//                context.getString(R.string.missing_data)
            )
            is JsonSyntaxException -> ErrorEntity.ApiError.BadResponseNetwork("missing_response"
//                context.getString(R.string.missing_response)
            )
            is IOException -> ErrorEntity.ApiError.Network("errorOccurred"
//                context.getString(R.string.errorOccurred)
            )
            is HttpException -> {
                when (throwable.code()) {
                    // no cache found in case of no network, thrown by retrofit -> treated as network error
//                    DataConstants.Network.HttpStatusCode.UNSATISFIABLE_REQUEST -> ErrorEntity.Network

                    // not found
                    HttpURLConnection.HTTP_NOT_FOUND -> ErrorEntity.ApiError.NotFound("")

                    // access denied
                    HttpURLConnection.HTTP_INTERNAL_ERROR -> ErrorEntity.ApiError.ServiceUnavailable("")

                    // unavailable service
                    HttpURLConnection.HTTP_UNAVAILABLE -> ErrorEntity.ApiError.ServiceUnavailable("")

                    // access denied
                    HttpURLConnection.HTTP_FORBIDDEN -> ErrorEntity.ApiError.AccessDenied("")

                    // unavailable service
                    HttpURLConnection.HTTP_UNAUTHORIZED -> ErrorEntity.ApiError.AccessDenied("")

                    // all the others will be treated as unknown error
                    else -> ErrorEntity.ApiError.Unknown("")
                }
            }
            is FileNotFoundException -> ErrorEntity.FileError.NotFound(""
//                context.getString(R.string.errorOccurred)
            )
            is SQLiteConstraintException -> ErrorEntity.DatabaseError.Unknown("")
            is SQLiteException -> ErrorEntity.DatabaseError.Unknown("")
            is StaleDataException -> ErrorEntity.DatabaseError.Unknown("")
            else -> ErrorEntity.ApiError.Unknown(""
//                context.getString(R.string.errorOccurred)
            )
        }
    }


    override fun getErrorResponseServer(errorBody: ResponseBody?, errorCode: Int): ErrorEntity {
        return when(errorCode){
            HttpURLConnection.HTTP_UNAUTHORIZED ->
                ErrorEntity.ApiError.AccessDenied(""
//                    context.getString(R.string.un_authorized)
                )
            else -> {
                val gson = Gson()

                val type = object : TypeToken<ErrorResponse?>() {}.type
                val errorResponse: ErrorResponse? =
                    gson.fromJson(errorBody?.charStream(), type)

                if (errorResponse != null)
                    ErrorEntity.ApiError.ServerErrorResponse(errorResponse)
                else
                    ErrorEntity.ApiError.ServerErrorResponse(
                        getErrorResponseServer(errorBody?.charStream())
                    )
            }
        }
    }

    override fun <T> getErrorResponseServer(baseResponse: BaseResponse<T>): ErrorEntity =
        ErrorEntity.ApiError.ServerErrorResponse(
            baseResponse.run {
                ErrorResponse(
                    error = null,
                    message = null,
                    statusCode = statusCode ?: 0,
                    isSuccess = isSuccess,
                    errorMessage = errorMessage
                )
            }
        )

    private fun getErrorResponseServer(charStream: Reader?): ErrorResponse {
        val gson = Gson()

        val errorMessage = gson.fromJson(charStream, String::class.java)

        return ErrorResponse(
            error = errorMessage,
            message = errorMessage,
            errorMessage = errorMessage,
        )
    }

}