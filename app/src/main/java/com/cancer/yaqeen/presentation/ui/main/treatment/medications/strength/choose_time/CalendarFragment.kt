package com.cancer.yaqeen.presentation.ui.main.treatment.medications.strength.choose_time

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.cancer.yaqeen.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment


class CalendarFragment : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }
}