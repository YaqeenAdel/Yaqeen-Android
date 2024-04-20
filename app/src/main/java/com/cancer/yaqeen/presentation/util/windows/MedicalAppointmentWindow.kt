package com.cancer.yaqeen.presentation.util.windows

import android.content.Context
import android.view.LayoutInflater
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.utils.convertMillisecondsToTime
import com.cancer.yaqeen.databinding.LayoutWindowMedicalReminderReminderBinding
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis


class MedicalAppointmentWindow(private val context: Context): Window(context) {
    private var layoutReminderBinding: LayoutWindowMedicalReminderReminderBinding? = null

    init {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutReminderBinding = LayoutWindowMedicalReminderReminderBinding.inflate(layoutInflater)
        setView(layoutReminderBinding?.root)
        layoutReminderBinding?.btnOk?.setOnClickListener { close() }
    }

    fun setMedicalAppointment(medicalAppointment: MedicalAppointmentDB?) {
        layoutReminderBinding?.run {
            medicalAppointment?.let {
//                val timing = if(it.isAM) context.getString(R.string.am) else context.getString(R.string.pm)
//                tvTime.text = "${it.time} $timing"
                val currentTimeMillis = getCurrentTimeMillis()
                tvTime.text = convertMillisecondsToTime(currentTimeMillis)
                tvDoctorName.text = it.doctorName
                tvDate.text = convertMilliSecondsToDate(it.startDate)
                tvNotes.text = it.notes
            }
        }
    }
}