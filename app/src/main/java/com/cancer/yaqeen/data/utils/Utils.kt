package com.cancer.yaqeen.data.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.presentation.util.generateFileName
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


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
            getString(R.string.error_occurred)
        }

        is ErrorEntity.ApiError.ServerErrorResponse -> {
            errorEntity.error.run {
                if (errorsStr?.contains("Expire") == true){
                    onAccessDenied()
                    null
                }else{
                    errorsStr ?: getString(R.string.error_occurred)
                }
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

fun <A> String.fromJson(type: Class<A>): A? =
    try {
        Gson().fromJson(this, type)
    }catch (_: Exception){
        null
    }

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

fun String.getMultipartBodyFromString(key: String): MultipartBody.Part {
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

fun String.tryToLong(): Long {
    return try {
        toLong()
    } catch (_: Exception) {
        0
    }
}

fun compressImage(
    context: Context,
    uri: Uri,
    maxWidth: Int,
    maxHeight: Int,
    quality: Int
): ByteArray? {
    try {
        // Load the original bitmap from the URI
        val originalBitmap =
            BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))

        // Calculate the scaling factor to maintain aspect ratio
        val scaleFactor = calculateScaleFactor(
            originalBitmap.width,
            originalBitmap.height,
            maxWidth,
            maxHeight
        )

        // Calculate the new dimensions
        val newWidth = (originalBitmap.width * scaleFactor).roundToInt()
        val newHeight = (originalBitmap.height * scaleFactor).roundToInt()

        // Create a matrix for the scaling transformation
        val matrix = Matrix()
        matrix.postScale(scaleFactor, scaleFactor)
        matrix.postRotate(90f)

        // Create a new scaled bitmap
        val scaledBitmap = Bitmap.createBitmap(
            originalBitmap,
            0,
            0,
            originalBitmap.width,
            originalBitmap.height,
            matrix,
            true
        )

        // Create a ByteArrayOutputStream to compress the bitmap
        val outputStream = ByteArrayOutputStream()

        // Compress the bitmap to the ByteArrayOutputStream
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)

        // Create a File to save the compressed image
        val outputDir = context.cacheDir // or getExternalFilesDir(null) for external storage
        val outputFile = File.createTempFile("compressed_image", ".jpg", outputDir)

        // Write the compressed bitmap to the File
        val fileOutputStream = FileOutputStream(outputFile)
        fileOutputStream.write(outputStream.toByteArray())
        fileOutputStream.close()

        // Return the compressed File
        return outputFile.readBytes()
    } catch (e: IOException) {
        e.printStackTrace()
    }

    // Return null if compression fails
    return null
}
fun calculateScaleFactor(
    originalWidth: Int,
    originalHeight: Int,
    maxWidth: Int,
    maxHeight: Int
): Float {
    return try {
        val widthRatio = maxWidth.toFloat() / originalWidth
        val heightRatio = maxHeight.toFloat() / originalHeight
        if (widthRatio < heightRatio) widthRatio else heightRatio
    } catch (e: Exception) {
        0F
    }
}


fun createPhotosList(photoPath: String?, url: String?, urls: List<String?>?): List<Photo> {
    val photos: MutableList<Photo> = arrayListOf()
    val photosPaths = photoPath?.split(",") ?: listOf()
//        val photosUrls = url?.split(",") ?: listOf()

    val photosUrls = urls ?: listOf()

    val pathsSize = photosPaths.size
    val urlsSize = photosUrls.size

    val size = if(pathsSize < urlsSize) pathsSize else urlsSize

    var pathURL = ""
    var imageName = ""

    for (index in 0 until size){
        pathURL = photosPaths[index]
        imageName = pathURL.substringAfterLast("/")
        imageName = imageName.ifEmpty { generateFileName() }

        photos.add(
            Photo(
                id = getCurrentTimeMillis(),
                url = photosUrls[index],
                pathURL = pathURL,
                imageName = imageName
            )
        )
    }

    return photos
}