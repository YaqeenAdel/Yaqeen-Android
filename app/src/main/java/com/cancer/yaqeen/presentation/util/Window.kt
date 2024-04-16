package com.cancer.yaqeen.presentation.util

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
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.databinding.LayoutReminderBinding
import com.cancer.yaqeen.presentation.ui.main.treatment.getMedicationType
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage


class Window(private val context: Context) {
    private var mView: View? = null
    private var mParams: WindowManager.LayoutParams? = null
    private var mWindowManager: WindowManager? = null
    private var layoutReminderBinding: LayoutReminderBinding? = null
    private var ringtone: Ringtone? = null
    private var vibrator: Vibrator? = null

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mParams = WindowManager.LayoutParams(
                // than filling the screen
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,  // Display it on top of other application windows
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,  // Don't let it grab the input focus
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,  // Make the underlying application window visible
                // through any transparent parts
                PixelFormat.TRANSLUCENT
            )
        }
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutReminderBinding = LayoutReminderBinding.inflate(layoutInflater)
        mView = layoutReminderBinding?.root
        layoutReminderBinding?.ivSetting?.setOnClickListener { close() }
        mParams?.gravity = Gravity.TOP
        mWindowManager = context.getSystemService(WINDOW_SERVICE) as WindowManager
    }

    fun setMedication(medication: MedicationDB?) {
        layoutReminderBinding?.run {
            medication?.let {
                val (timing, hour12) = if(it.hour24 < 12) context.getString(R.string.am) to it.hour24 else context.getString(
                    R.string.pm) to (it.hour24 - 12)
                tvTime.text = "$hour12:${it.minute} $timing"
                tvMedicationDetails.text = "${it.medicationName} ${it.strengthAmount} ${it.unitType}"
                tvNotes.text = it.notes
                tvDosageAmount.text = "${it.dosageAmount} ${it.medicationType}"
                getMedicationType(context, it.medicationType)?.run { iconResId
                    bindResourceImage(ivMedicationIcon, iconResId)
                }
            }
        }
    }

    fun setRoutineTestDB(routineTest: RoutineTestDB?) {

    }

    fun setMedicalAppointmentDB(medicalAppointment: MedicalAppointmentDB?) {

    }

    fun open() {
        try {
            if (mView?.windowToken == null) {
                if (mView?.parent == null) {
                    mWindowManager?.addView(mView, mParams)
                    playDefaultAlarmSound()
                }
            }
        } catch (e: Exception) {
            Log.d("Error1", e.toString())
        }
    }

    private fun close() {
        try {
            ringtone?.stop()
            vibrator?.cancel()
            (context.getSystemService(WINDOW_SERVICE) as WindowManager).removeView(mView)
            mView?.invalidate()
            (mView?.parent as ViewGroup).removeAllViews()
        } catch (e: Exception) {
            Log.d("Error2", e.toString())
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

    private fun playVibration(){
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
        }catch (e: Exception){

        }
    }
}