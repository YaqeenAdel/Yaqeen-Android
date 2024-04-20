package com.cancer.yaqeen.presentation.util.windows

import android.content.Context
import android.view.LayoutInflater
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.utils.convertMillisecondsToTime
import com.cancer.yaqeen.databinding.LayoutWindowRoutineTestReminderBinding
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis


class RoutineTestWindow(private val context: Context): Window(context) {
    private var layoutReminderBinding: LayoutWindowRoutineTestReminderBinding? = null

    init {
        val layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        layoutReminderBinding = LayoutWindowRoutineTestReminderBinding.inflate(layoutInflater)
        setView(layoutReminderBinding?.root)
        layoutReminderBinding?.btnOk?.setOnClickListener { close() }
    }

    fun setRoutineTest(routineTest: RoutineTestDB?) {
        layoutReminderBinding?.run {
            routineTest?.let {
//                val timing = if(it.isAM) context.getString(R.string.am) else context.getString(R.string.pm)
//                tvTime.text = "${it.time} $timing"
                val currentTimeMillis = getCurrentTimeMillis()
                tvTime.text = convertMillisecondsToTime(currentTimeMillis)
                tvRoutineTestName.text = it.routineTestName
                tvNotes.text = it.notes
            }
        }
    }

}