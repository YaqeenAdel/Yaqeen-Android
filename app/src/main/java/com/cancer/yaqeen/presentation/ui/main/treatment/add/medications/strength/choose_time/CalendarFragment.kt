package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.databinding.FragmentCalendarBinding
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigateUp
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.Calendar


class CalendarFragment : BottomSheetDialogFragment() {

    private var binding: FragmentCalendarBinding by autoCleared()

    private lateinit var navController: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomSheetDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        val selectedDate = Calendar.getInstance()

        binding.calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            binding.btnNext.setOnClickListener {
                selectedDate.set(year, month, dayOfMonth)
                setFragmentResult(selectedDate.timeInMillis)
            }
        }
        binding.btnNext.setOnClickListener {
            val selectedDate = binding.calendarView.date
            setFragmentResult(selectedDate)

        }
    }

    override fun onResume() {
        super.onResume()

        val currentDate = Calendar.getInstance()

        // If i want to set the minimum date for the CalendarView to tomorrow
//        currentDate.add(Calendar.DAY_OF_MONTH, 1)

        // Set the minimum date for the CalendarView to today
        binding.calendarView.minDate = currentDate.timeInMillis
    }

    private fun setFragmentResult(timeInMillis: Long) {
        setFragmentResult(Constants.REQUEST_DATE_KEY, bundleOf(Constants.DATE_SELECTED_KEY to timeInMillis))
        navController.tryNavigateUp()
    }
}