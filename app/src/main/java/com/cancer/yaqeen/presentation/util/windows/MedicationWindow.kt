package com.cancer.yaqeen.presentation.util.windows

import android.content.Context
import android.view.LayoutInflater
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.databinding.LayoutWindowMedicationReminderBinding
import com.cancer.yaqeen.presentation.ui.main.treatment.getMedicationType
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage


class MedicationWindow(private val context: Context): Window(context) {

    private var layoutReminderBinding: LayoutWindowMedicationReminderBinding? = null

    init {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutReminderBinding = LayoutWindowMedicationReminderBinding.inflate(layoutInflater)
        setView(layoutReminderBinding?.root)
        layoutReminderBinding?.btnOk?.setOnClickListener { close() }
    }

    fun setMedication(medication: MedicationDB?) {
        layoutReminderBinding?.run {
            medication?.let {
                val timing = if(it.isAM) context.getString(R.string.am) else context.getString(R.string.pm)
                tvTime.text = "${it.time} $timing"
                tvMedicationDetails.text = "${it.medicationName} ${it.strengthAmount} ${it.unitType}"
                tvNotes.text = it.notes
                tvDosageAmount.text = "${it.dosageAmount} ${it.medicationType}"
                getMedicationType(context, it.medicationType)?.run { iconResId
                    bindResourceImage(ivMedicationIcon, iconResId)
                }
            }
        }
    }
}