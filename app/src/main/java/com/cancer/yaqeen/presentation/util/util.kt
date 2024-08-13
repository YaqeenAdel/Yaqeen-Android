package com.cancer.yaqeen.presentation.util

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewTreeObserver
import com.cancer.yaqeen.data.features.onboarding.models.Language
import com.cancer.yaqeen.databinding.DialogImageBinding
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


fun dpToPx(dp: Float, context: Context): Int {
    return (dp * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun languageIsEnglish() =
    Locale.getDefault().language.equals(Language.ENGLISH.lang)


fun String.formatTime12(): String {

    // Create a SimpleDateFormat object with the desired format
    val inputFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    val outputFormat = SimpleDateFormat("h.mm a", Locale.getDefault())

    // Parse the input time string into a Date object
    val date = inputFormat.parse(this)

    // Format the date using the output format
    return outputFormat.format(date)

}


fun updateConfiguration(localeCode: String? = null, resources: Resources, context: Context){
    val contextWrapper: Context =
        MyContextWrapper(context).wrap(localeCode ?: Locale.getDefault().language)
    resources.updateConfiguration(
        contextWrapper.resources.configuration,
        contextWrapper.resources.displayMetrics
    )
}
fun overrideLocale(localeCode: String? = null, resources: Resources) {
    val local = Locale(
        localeCode ?: Locale.getDefault().language
    )
    Locale.setDefault(local)
    val newConfig = Configuration()
    newConfig.setLocale(local)
    resources.updateConfiguration(newConfig, resources.displayMetrics)
}
fun convertMilliSecondsToDate(milliseconds: Long, pattern: String = "dd/MM/yyyy", local: Locale = Locale.getDefault()): String {
    val timestamp = Date(milliseconds)

    val dateFormat = SimpleDateFormat(pattern, local)
    return dateFormat.format(timestamp)
}
fun convertDateToMilliSeconds(date: String, pattern: String = "dd/MM/yyyy"): Long {
    return try {
        val dateFormat = SimpleDateFormat(pattern, Locale.getDefault())

        dateFormat.parse(date)?.time ?: 0L
    }catch (_: Exception){
        0L
    }
}
fun getDateWithSpecificDay(timeInMillis: Long, dayId: Int): Calendar {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    calendar.timeInMillis = timeInMillis
    val nextDate = calendar.clone() as Calendar
    while (nextDate.get(Calendar.DAY_OF_WEEK) != dayId) {
        nextDate.add(Calendar.DAY_OF_MONTH, 1)
    }
    return nextDate
}

fun getDateTime(year: Int, month: Int, day: Int, hour24: Int, minute: Int, second: Int = 0): Calendar {
    val calendar = Calendar.getInstance(TimeZone.getDefault())
    calendar.set(year, month - 1, day, hour24, minute, second)

    return calendar
}

fun getImageUri(context: Context, image: Bitmap): Uri? {
//    val bytes = ByteArrayOutputStream()
//    image.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
    val path =
        MediaStore.Images.Media.insertImage(context.contentResolver, image, "Title", null)
    if(path.isNullOrEmpty()){
        return saveAndConvertBitmap(context, image)
    }
    return Uri.parse(path)
}


fun saveAndConvertBitmap(context: Context, image: Bitmap): Uri? {
    val uuid = UUID.randomUUID().toString()
    val stream = ByteArrayOutputStream()
    image.compress(Bitmap.CompressFormat.JPEG, 100, stream)
    val bytes = stream.toByteArray()


    try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val values = ContentValues()
            values.put(
                MediaStore.MediaColumns.DISPLAY_NAME,
                uuid
            )
            values.put(
                MediaStore.MediaColumns.MIME_TYPE,
                "image/*"
            )
            values.put(
                MediaStore.MediaColumns.RELATIVE_PATH,
                Environment.DIRECTORY_DOCUMENTS + "/Yaqeen/"
            )
            val uri: Uri? = context.contentResolver.insert(
                MediaStore.Files.getContentUri("external"),
                values
            )
            val outputStream: OutputStream? =
                context.contentResolver
                    .openOutputStream(uri!!)
            outputStream?.write(bytes)
            outputStream?.close()
            return uri
        } else {
            val root =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString()
            val mainDirectory = File("$root/Yaqeen")
            if (!mainDirectory.exists()) {
                mainDirectory.mkdirs()
            }
            val pdfFileName = "$uuid.jpeg"
            val file = File(mainDirectory, pdfFileName)
            if (file.exists()) {
                file.delete()
            }
            val out = FileOutputStream(file)
            out.write(bytes)
            out.flush()
            out.close()
            return Uri.fromFile(file)
        }
    } catch (e: Exception) {
        return null
    }
}

fun getBitmap(context: Context, uri: Uri) =
    BitmapFactory.decodeStream(context.contentResolver.openInputStream(uri))


fun cropImageToSquare(bitmap: Bitmap, size: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    var cropWidth = (width - height) / 2
    cropWidth = if (cropWidth < 0) 0 else cropWidth
    var cropHeight = (height - width) / 2
    cropHeight = if (cropHeight < 0) 0 else cropHeight

    return Bitmap.createBitmap(bitmap, cropWidth, cropHeight, size, size)
}

fun getCurrentTimeMillis(): Long =
    System.currentTimeMillis()

fun generateFileName(): String =
SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH)
    .format(getCurrentTimeMillis()) + ".jpg"


fun showImageDialog(layoutInflater: LayoutInflater, context: Context, imageUrl: String? = null, imageUri: Uri? = null) {
    val popUpDialogBinding = DialogImageBinding.inflate(layoutInflater)

    val dialog: androidx.appcompat.app.AlertDialog = MaterialAlertDialogBuilder(context).apply {
        setView(popUpDialogBinding.root)
    }.create()

    val observer = popUpDialogBinding.imageView.viewTreeObserver

    observer.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
        override fun onGlobalLayout() {
            // Remove the listener to prevent multiple calls to this method
            popUpDialogBinding.imageView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            // Get the width of the ViewPager
            val width = popUpDialogBinding.imageView.width
            // Set the height of the ViewPager to be the same as its width
            popUpDialogBinding.imageView.layoutParams.height = width
            // Request a new layout to update the ViewPager's height
            popUpDialogBinding.imageView.requestLayout()
        }
    })

    if(imageUrl != null)
        bindImage(popUpDialogBinding.imageView, imageUrl)
    else
        popUpDialogBinding.imageView.setImageURI(imageUri)

    dialog.show()
}
