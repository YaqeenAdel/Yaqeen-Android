package com.cancer.yaqeen.presentation.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.core.content.ContextCompat


private fun checkSelfPermission(context: Context, permission: String): Boolean = ContextCompat.checkSelfPermission(
    context,
    permission
) == PackageManager.PERMISSION_GRANTED


fun storagePermissionsAreGranted(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        Log.d("TAG", "openStorage: storagePermissionsAreGranted")
        checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES,
        )
//                && checkSelfPermission(
//            context,
//            Manifest.permission.READ_MEDIA_VIDEO
//        )
    } else {
        checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )
}

fun cameraPermissionsAreGranted(context: Context) =
    checkSelfPermission(
        context,
        Manifest.permission.CAMERA
    )

private fun requestMultiplePermissions(requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>?>,
                               permissions: Array<String>) =
    requestMultiplePermissionsLauncher.launch(
        permissions
    )

private fun requestPermission(requestPermissionsLauncher: ActivityResultLauncher<String?>, permission: String) =
    requestPermissionsLauncher.launch(
        permission
    )


fun enableStoragePermissions(requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>?>) {
    if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
        requestMultiplePermissions(
            requestMultiplePermissionsLauncher,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        )
    }
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requestMultiplePermissions(
            requestMultiplePermissionsLauncher,
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES
            )
        )
    } else {
        requestMultiplePermissions(
            requestMultiplePermissionsLauncher,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }
}


fun enableCameraPermissions(requestPermissionLauncher: ActivityResultLauncher<String?>) {
    requestPermission(
        requestPermissionLauncher,
        Manifest.permission.CAMERA
    )
}