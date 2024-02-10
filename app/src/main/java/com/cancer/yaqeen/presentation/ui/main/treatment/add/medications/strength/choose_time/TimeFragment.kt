package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time.Companion.getHours12
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time.Companion.getMinutes
import com.cancer.yaqeen.databinding.FragmentTimeBinding
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigateUp
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class TimeFragment : DialogFragment() {

    private var binding: FragmentTimeBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var hoursAdapter: TimesAdapter
    private lateinit var minutesAdapter: TimesAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupTimesAdapter()

        setListener()
    }

    private fun setListener() {
        binding.btnNext.setOnClickListener {
            val hourSelected = hoursAdapter.getItemSelected().time
            val minuteSelected = minutesAdapter.getItemSelected().time
            val isAM = binding.btnAm.isChecked
            val timing = if (isAM) getString(R.string.am) else getString(R.string.pm)
            setFragmentResult(
                Constants.REQUEST_REMINDER_TIME_KEY,
                bundleOf(
                    Constants.REMINDER_TIME_KEY to ReminderTime(
                        hour12 = hourSelected,
                        hour24 = if (isAM) {
                            if (hourSelected == "12")
                                "00"
                            else hourSelected
                        } else {
                            if (hourSelected == "12")
                                "12"
                            else
                                (hourSelected.toInt() + 12).toString()
                        },
                        minute = minuteSelected,
                        timing = timing,
                        isAM = isAM,
                        text = "$hourSelected:$minuteSelected"
                    )
                )
            )
            navController.tryNavigateUp()
        }
        binding.toggleGroup.addOnButtonCheckedListener { _, _, _ ->
            checkTimeData()
        }
    }

    private fun setupTimesAdapter() {
        setupHoursAdapter()
        setupMinutesAdapter()
    }

    private fun setupMinutesAdapter() {
        hoursAdapter = TimesAdapter {
            checkTimeData()
        }

        binding.rvHours.apply {
            adapter = hoursAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(8f, requireContext())
                )
            )
        }


        hoursAdapter.submitList(
            getHours12()
        )
    }

    private fun setupHoursAdapter() {
        minutesAdapter = TimesAdapter {
            checkTimeData()
        }

        binding.rvMinutes.apply {
            adapter = minutesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(8f, requireContext())
                )
            )
        }


        minutesAdapter.submitList(
            getMinutes()
        )
    }

    private fun checkTimeData() {
        val hourIsSelected = hoursAdapter.anyItemIsSelected()
        val minuteIsSelected = minutesAdapter.anyItemIsSelected()
        val timingIsSelected = binding.btnAm.isChecked || binding.btnPm.isChecked

        val textColorId: Int
        val backgroundColorId: Int

        if (hourIsSelected && minuteIsSelected && timingIsSelected) {
            binding.btnNext.enable()
            textColorId = R.color.white
            backgroundColorId = R.color.primary_color
        } else {
            binding.btnNext.disable()
            textColorId = R.color.medium_gray
            backgroundColorId = R.color.light_gray
        }

        binding.btnNext.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), backgroundColorId)
        binding.btnNext.setTextColor(ContextCompat.getColorStateList(requireContext(), textColorId))
        binding.btnNext.iconTint = ContextCompat.getColorStateList(requireContext(), textColorId)

    }
}