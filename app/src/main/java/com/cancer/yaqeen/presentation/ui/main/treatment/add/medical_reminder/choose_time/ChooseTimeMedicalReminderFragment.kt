package com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder.choose_time

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cancer.yaqeen.R


class ChooseTimeMedicalReminderFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_choose_time_medical_reminder, container, false)
    }

}