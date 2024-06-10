package com.cancer.yaqeen.presentation.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.cancer.yaqeen.R


private fun checkSelfPermission(context: Context, permission: String): Boolean = ContextCompat.checkSelfPermission(
    context,
    permission
) == PackageManager.PERMISSION_GRANTED


fun storagePermissionsAreGranted(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES
        ) && checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
        )
    }
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        checkSelfPermission(
            context,
            Manifest.permission.READ_MEDIA_IMAGES,
        )
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


fun enableStoragePermissions(requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>?>, context: Context? = null) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        context?.let {
            Toast.makeText(context,
                context.getString(R.string.please_select_the_allow_all_option), Toast.LENGTH_LONG).show()
        }
        requestMultiplePermissions(
            requestMultiplePermissionsLauncher,
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
            )
        )
    }
    else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requestMultiplePermissions(
            requestMultiplePermissionsLauncher,
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
            )
        )
    }
    else if(Build.VERSION.SDK_INT == Build.VERSION_CODES.Q) {
        requestMultiplePermissions(
            requestMultiplePermissionsLauncher,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            )
        )
    }
    else {
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


fun enableNotificationPermissions(requestPermissionLauncher: ActivityResultLauncher<String?>) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        requestPermission(
            requestPermissionLauncher,
            Manifest.permission.POST_NOTIFICATIONS
        )
    }
}

fun drawOverlaysPermissionAreGranted(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        Settings.canDrawOverlays(context)
    }else{
        true
    }

fun enableDrawOverlaysPermission(context: Context): Boolean {
    if (!drawOverlaysPermissionAreGranted(context)) {
        Toast.makeText(context,
            context.getString(R.string.please_enable_permission_to_draw_over_other_apps), Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:${context.packageName}"))
        context.startActivity(intent)
        return false
    }
    return true
}

fun requestExactAlarmPermission(activity: Activity, context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (exactAlarmPermissionIsGranted(context))
            return true

        val alarmManager = activity.getSystemService(AppCompatActivity.ALARM_SERVICE) as AlarmManager

        if (alarmManager.canScheduleExactAlarms()) {
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            intent.data = Uri.Builder()
                .scheme("package")
                .opaquePart(activity.packageName)
                .build()
            activity.registerReceiver(
                null, IntentFilter(
                    AlarmManager.ACTION_SCHEDULE_EXACT_ALARM_PERMISSION_STATE_CHANGED
                )
            )
            activity.startActivity(intent)

            Toast.makeText(context, "Allow the permission", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    return true
}


fun exactAlarmPermissionIsGranted(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
        return true

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // For Android Q (API level 29) and above, you can directly check the permission status
        val permissionStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.SCHEDULE_EXACT_ALARM
            )
        } else {
            -1
        }
        return permissionStatus == PackageManager.PERMISSION_GRANTED
    } else {
        // For versions below Android Q, SCHEDULE_EXACT_ALARM is a hidden permission,
        // so it's granted automatically if your app holds the FOREGROUND_SERVICE permission.
        val foregroundServicePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.FOREGROUND_SERVICE
            )
        } else {
            -1
        }
        return foregroundServicePermission == PackageManager.PERMISSION_GRANTED
    }
}


@SuppressLint("BatteryLife")
fun requestBatteryPermission(activity: Activity): Boolean{
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
        val powerManager: PowerManager = activity.getSystemService(Context.POWER_SERVICE) as PowerManager
        if (!powerManager.isIgnoringBatteryOptimizations(activity.packageName)){
            val intent = Intent().apply {
                action = Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS
                data = Uri.parse("package:${activity.packageName}")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            activity.startActivity(intent)
            return false
        }
    }
    return true
}

fun requestNotificationPolicyPermission(activity: Activity): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

        val notificationManager = activity.getSystemService(
            AppCompatActivity.NOTIFICATION_SERVICE
        ) as NotificationManager

        if (!notificationManager.isNotificationPolicyAccessGranted) {
            val intent = Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)
            activity.registerReceiver(
                null, IntentFilter(
                    NotificationManager.ACTION_NOTIFICATION_POLICY_ACCESS_GRANTED_CHANGED
                )
            )
            activity.startActivity(intent)

            Toast.makeText(activity, "Select YAQEEN App and allow the permission", Toast.LENGTH_SHORT).show()
            return false
        }
    }
    return true
}

fun alarmPermissionsAreGranted(context: Context) =
    checkSelfPermission(
        context,
        Manifest.permission.SCHEDULE_EXACT_ALARM
    )

fun enableAlarmPermission(requestPermissionLauncher: ActivityResultLauncher<String?>) {
    Log.d("TAG", "enableAlarmPermission: SCHEDULE_EXACT_ALARM")

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        requestPermission(
            requestPermissionLauncher,
            Manifest.permission.SCHEDULE_EXACT_ALARM
        )
    }
}

fun schedulingPermissionsAreGranted(activity: Activity, context: Context): Boolean {
    val exactAlarmPermissionIsGranted =
        requestExactAlarmPermission(activity, context)
    if (!exactAlarmPermissionIsGranted)
        return false
    val batteryPermissionIsGranted =
        requestBatteryPermission(activity)
    if (!batteryPermissionIsGranted)
        return false
    val notificationPolicyPermissionIsGranted =
        requestNotificationPolicyPermission(activity)
    if (!notificationPolicyPermissionIsGranted)
        return false
    val drawOverlaysPermissionIsGranted =
        enableDrawOverlaysPermission(context)
    if (!drawOverlaysPermissionIsGranted)
        return false

    return true
}