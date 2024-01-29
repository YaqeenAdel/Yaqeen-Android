package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.models.Day
import com.cancer.yaqeen.data.features.home.models.Time
import com.cancer.yaqeen.databinding.FragmentChooseTimeBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.MedicationsViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.disable
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enable
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChooseTimeFragment : BaseFragment() {

    private var binding: FragmentChooseTimeBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var medicationTimesAdapter: MedicationTimesAdapter

    private lateinit var daysAdapter: DaysAdapter

    private val medicationsViewModel: MedicationsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentChooseTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setupAdapters()

        updateUI()

        setListener()

        binding.toolbar.title = "CINNARIZINE"
        binding.tvMedicationType.text = "Capsule 5 Mg"
    }

    private fun setupAdapters() {
        setupMedicationTimesAdapter()
        setupDaysAdapter()
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnNext.setOnClickListener {
            navController.tryNavigate(
                ChooseTimeFragmentDirections.actionChooseTimeFragmentToSelectTimeFragment()
            )
        }
        binding.editTextStartFrom.setOnClickListener {
            navController.tryNavigate(
                ChooseTimeFragmentDirections.actionChooseTimeFragmentToCalendarFragment()
            )
        }
        binding.editTextTime.setOnClickListener {
            navController.tryNavigate(
                ChooseTimeFragmentDirections.actionChooseTimeFragmentToTimeFragment()
            )
        }

    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("3/3")
        spannable.setSpan(ForegroundColorSpan(ContextCompat.getColor(requireContext(), R.color.primary_color)), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.tvPageNumber.text = spannable

    }

    private fun setupDaysAdapter() {
        daysAdapter = DaysAdapter {

        }

        binding.rvDays.adapter = daysAdapter

        daysAdapter.submitList(
            listOf(
                Day(0, "Sun"),
                Day(1, "Mon"),
                Day(2, "Tues"),
                Day(3, "Wed"),
                Day(4, "Thur"),
                Day(5, "Fri"),
                Day(6, "Sat")
            )
        )
    }

    private fun selectItem(itemId: Int) {
        val selectItemPosition = daysAdapter.selectItem(itemId)
        if(selectItemPosition >= 2)
            binding.rvTimes.scrollToPosition(selectItemPosition - 2)
    }

    private fun setupMedicationTimesAdapter() {
        medicationTimesAdapter = MedicationTimesAdapter {
            val specificDaysIsNotSelected = checkStrengthData(it)
            if (specificDaysIsNotSelected)
                navController.tryNavigate(
                    ChooseTimeFragmentDirections.actionChooseTimeFragmentToSelectTimeFragment()
                )
        }

        binding.rvTimes.apply {
            adapter = medicationTimesAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(12f, requireContext())
                )
            )
        }

        medicationTimesAdapter.submitList(
            listOf(
                Time(
                    id = 1, time = "Every Day"
                ),
                Time(
                    id = 2, time = "Every 8 hours"
                ),
                Time(
                    id = 3, time = "Every 12 hours"
                ),
                Time(
                    id = 4, time = "Day after day"
                ),
                Time(
                    id = 5, time = "Specific Days of The week"
                )
            )
        )
    }

    private fun checkStrengthData(time: Time): Boolean {
        val specificDaysIsNotSelected = time.id != 5
        binding.groupSpecificDays.changeVisibility(show = !specificDaysIsNotSelected, isGone = true)

        return specificDaysIsNotSelected

        val date = binding.editTextStartFrom.text.toString()
        val time = binding.editTextTime.text.toString()

        val textColorId: Int
        val backgroundColorId: Int

        if(date.isNotEmpty() && time.isNotEmpty() && medicationTimesAdapter.selectedPosition() == 4) {
            binding.btnNext.enable()
            textColorId = R.color.white
            backgroundColorId = R.color.primary_color
        }
        else {
            binding.btnNext.disable()
            textColorId = R.color.medium_gray
            backgroundColorId = R.color.light_gray
        }

        binding.btnNext.backgroundTintList =
            ContextCompat.getColorStateList(requireContext(), backgroundColorId)
        binding.btnNext.setTextColor(ContextCompat.getColorStateList(requireContext(), textColorId))
        binding.btnNext.iconTint = ContextCompat.getColorStateList(requireContext(), textColorId)

        return false
    }
}