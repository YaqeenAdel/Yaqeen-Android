package com.cancer.yaqeen.presentation.util.windows

import android.content.Context
import android.content.Context.WINDOW_SERVICE
import android.graphics.PixelFormat
import android.media.AudioAttributes
import android.media.Ringtone
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.cancer.yaqeen.presentation.util.drawOverlaysPermissionAreGranted


abstract class Window(private val context: Context) {
    private var mView: View? = null
    private var mParams: WindowManager.LayoutParams? = null
    private var mWindowManager: WindowManager? = null

    //    private var layoutReminderBinding: LayoutReminderBinding? = null
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams = WindowManager.LayoutParams(
                // than filling the screen
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else WindowManager.LayoutParams.TYPE_PHONE,  // Don't let it grab the input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or
                        WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                        WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH or
                        WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                        WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                // Make the underlying application window visible
                // through any transparent parts
                PixelFormat.TRANSLUCENT
            )
        }
        mParams?.gravity = Gravity.TOP
        mWindowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    }

    protected fun setView(view: View?) {
        mView = view
    }

    fun open() {
        try {
            if (mView?.windowToken == null) {
                if (mView?.parent == null) {
                    playDefaultAlarmSound()
                    if (!drawOverlaysPermissionAreGranted(context))
                        return
                    mWindowManager?.addView(mView, mParams)
                }
            }
        } catch (e: Exception) {
        }
    }

    protected fun close() {
        try {
            ringtone?.stop()
            vibrator?.cancel()
            (context.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(mView)
            mView?.invalidate()
            (mView?.parent as ViewGroup).removeAllViews()
        } catch (e: Exception) {
        }
    }

    private fun playDefaultAlarmSound() {
        try {
            val notification: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ringtone = RingtoneManager.getRingtone(context, notification)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ringtone?.isLooping = true
            }
            ringtone?.audioAttributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build()
            ringtone?.play()
            playVibration()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun playVibration() {
        try {
            val vibrationPattern = longArrayOf(0, 500, 500, 500, 500)
            val repeatCount = 3
            vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
            if (vibrator?.hasVibrator() == true) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(
                        VibrationEffect.createWaveform(
                            vibrationPattern,
                            repeatCount
                        )
                    )
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(vibrationPattern, repeatCount)
                }
            }
        } catch (e: Exception) {

        }
    }
}