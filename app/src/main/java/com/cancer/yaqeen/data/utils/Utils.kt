package com.cancer.yaqeen.data.utils

import android.content.Context
import android.net.Uri
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.*



fun Context.handleError(errorEntity: ErrorEntity?, onAccessDenied: () -> Unit = {}): String? {
    return when (errorEntity) {
        is ErrorEntity.ApiError.InternetConnection -> {
            getString(R.string.no_internet_connection)
        }
        is ErrorEntity.ApiError.Network -> {
            getString(R.string.no_internet_connection)
        }
        is ErrorEntity.ApiError.TimeOutNetwork -> {
            getString(R.string.timeout_exceeded)
        }
        is ErrorEntity.ApiError.AccessDenied -> {
            onAccessDenied()
            null
        }
        is ErrorEntity.ApiError.ServerErrorResponse -> {
            errorEntity.error.run {
                errorsStr ?: getString(R.string.error_occurred)
            }
        }
        is ErrorEntity.GraphQlError.ApolloErrorResponse -> {
            errorEntity.error
        }
        null -> null
        else -> {
            getString(R.string.error_occurred)
        }
    }
}

fun <A> String.fromJson(type: Class<A>): A =
    Gson().fromJson(this, type)

fun <A> A.toJson(): String = Gson().toJson(this)

fun <A> A.toRequestBody(): RequestBody =
    toJson().toRequestBody("application/json".toMediaTypeOrNull())

fun getFilePart(context: Context, uri: Uri, key: String, file: File): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        key,
        file.name,
        RequestBody.create(
            (context.contentResolver.getType(uri) ?: ""
                    ).toMediaTypeOrNull(), file
        )
    )
}
fun <T> T.getMultipartBody(key: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        key,
        null,
        getRequestBodyPart()
    )
}
fun String.getMultipartBody(key: String): MultipartBody.Part {
    return MultipartBody.Part.createFormData(
        key,
        null,
        getRequestBodyPart()
    )
}
fun <T> T.getRequestBodyPart(): RequestBody {
    return toJson()
        .toRequestBody("application/json; text/plain; charset=utf-8".toMediaTypeOrNull())
}
fun String.getRequestBodyPart(): RequestBody {
    return toRequestBody("application/json; text/plain; charset=utf-8".toMediaTypeOrNull())
}

fun <A> A.toJsonString(): String {
    val gson5: Gson = GsonBuilder().disableHtmlEscaping().create()
    return gson5.toJson(this).toString()
}

fun Float.toPriceWithDiscount(discountPercentage: Float): Float =
    minus(times(discountPercentage).div(100))

fun Float.toPriceWithoutDiscount(discountPercentage: Float): Float =
    times(100.0f.div(100 - discountPercentage))


fun Float.calculatePriceAfterDiscount(discountPercentage: Float): Float {
    val perc = discountPercentage / 100
    val discountAmount = this * perc
    return this - discountAmount
}

fun Long.formatDate(pattern: String = "dd MMM yyyy"): String {
    val dateFormat = SimpleDateFormat(pattern)
    val date = Date(this)
    return dateFormat.format(date)
}

fun String.formatDate(): String {
    val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
    val outputDateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    val inputDate = inputDateFormat.parse(this)
    return outputDateFormat.format(inputDate)

}
fun convertMillisecondsToTime(milliseconds: Long, pattern: String = "hh:mm a"): String {
    val currentTimestamp = System.currentTimeMillis()
    val timestamp = Date(milliseconds)

    val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())
    return dateFormat.format(timestamp)
}

fun getTodayDate(): String {
    val currentTimestamp = System.currentTimeMillis()

    return currentTimestamp.formatDate()
}
