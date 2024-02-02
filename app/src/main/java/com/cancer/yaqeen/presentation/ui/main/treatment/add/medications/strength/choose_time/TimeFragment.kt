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
            val timing = if (binding.btnAm.isChecked) "AM" else "PM"
            setFragmentResult(
                Constants.REQUEST_REMINDER_TIME_KEY,
                bundleOf(
                    Constants.REMINDER_TIME_KEY to ReminderTime(
                        hour12 = hourSelected,
                        hour24 = if (binding.btnAm.isChecked) {
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
                        text = "$hourSelected:$minuteSelected $timing"
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
            listOf(
                Time(1, "01"),
                Time(2, "02"),
                Time(3, "03"),
                Time(4, "04"),
                Time(5, "05"),
                Time(6, "06"),
                Time(7, "07"),
                Time(8, "08"),
                Time(9, "09"),
                Time(10, "10"),
                Time(11, "11"),
                Time(12, "12")
            )
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
            listOf(
                Time(0, "00"),
                Time(1, "01"),
                Time(2, "02"),
                Time(3, "03"),
                Time(4, "04"),
                Time(5, "05"),
                Time(6, "06"),
                Time(7, "07"),
                Time(8, "08"),
                Time(9, "09"),
                Time(10, "10"),
                Time(11, "11"),
                Time(12, "12"),
                Time(13, "13"),
                Time(14, "14"),
                Time(15, "15"),
                Time(16, "16"),
                Time(17, "17"),
                Time(18, "18"),
                Time(19, "19"),
                Time(20, "20"),
                Time(21, "21"),
                Time(22, "22"),
                Time(23, "23"),
                Time(24, "24"),
                Time(25, "25"),
                Time(26, "26"),
                Time(27, "27"),
                Time(28, "28"),
                Time(29, "29"),
                Time(30, "30"),
                Time(31, "31"),
                Time(32, "32"),
                Time(33, "33"),
                Time(34, "34"),
                Time(35, "35"),
                Time(36, "36"),
                Time(37, "37"),
                Time(38, "38"),
                Time(39, "39"),
                Time(40, "40"),
                Time(41, "41"),
                Time(42, "42"),
                Time(43, "43"),
                Time(44, "44"),
                Time(45, "45"),
                Time(46, "46"),
                Time(47, "47"),
                Time(48, "48"),
                Time(49, "49"),
                Time(50, "50"),
                Time(51, "51"),
                Time(52, "52"),
                Time(53, "53"),
                Time(54, "54"),
                Time(55, "55"),
                Time(56, "56"),
                Time(57, "57"),
                Time(58, "58"),
                Time(59, "59")
            )
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