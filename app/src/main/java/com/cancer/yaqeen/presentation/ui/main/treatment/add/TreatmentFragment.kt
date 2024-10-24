package com.cancer.yaqeen.presentation.ui.main.treatment.add

import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time.Companion.getHours24
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Timing
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.utils.getTodayDate
import com.cancer.yaqeen.databinding.FragmentTreatmentBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.TimesAdapter
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.timestampToDay
import com.cancer.yaqeen.presentation.util.timestampToHour
import com.cancer.yaqeen.presentation.util.timestampToTiming
import com.cancer.yaqeen.presentation.util.tryNavigate
import dagger.hilt.android.AndroidEntryPoint
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class TreatmentFragment : BaseFragment(showBottomMenu = true), View.OnClickListener {

    private var binding: FragmentTreatmentBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var timesAdapter: TimesAdapter

    @Inject
    lateinit var sharedPrefUtil: SharedPrefEncryptionUtil

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTreatmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()
        updateUI()

        setupTimesAdapter()

        selectItem(getCurrentHour())
    }

    override fun onResume() {
        super.onResume()

        binding.tvCurrentDayDate.text = getTodayDate()

    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btnMedications.setOnClickListener(this)
        binding.btnSymptoms.setOnClickListener(this)
        binding.btnRoutineTests.setOnClickListener(this)
        binding.btnMedicalAppointment.setOnClickListener(this)

    }

    private fun updateUI() {
        val spannable = SpannableStringBuilder("1/3")
        spannable.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.primary_color
                )
            ), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        binding.tvPageNumber.text = spannable

    }

    private fun setupTimesAdapter() {
        timesAdapter = TimesAdapter(sharedPrefUtil.selectedLanguageIsArabic()) {

        }

        binding.rvTimes.adapter = timesAdapter


        timesAdapter.submitList(
            getHours24()
        )

    }

    private fun getCurrentHour(): Int {
        val currentDate = Calendar.getInstance()
        val timing = (currentDate.timeInMillis.timestampToTiming())

        return (currentDate.timeInMillis.timestampToHour().toIntOrNull()
            ?: 0) + if (timing == Timing.PM.id) 12 else 0
    }

    private fun selectItem(itemId: Int) {
        val selectItemPosition = timesAdapter.selectItem(itemId)
        if (selectItemPosition >= Constants.MAX_POSITION_TO_SCROLL) {
            val position = when (selectItemPosition) {
                timesAdapter.itemCount - Constants.MAX_POSITION_TO_SCROLL -> selectItemPosition + 1
                timesAdapter.itemCount - 1 -> selectItemPosition
                else -> selectItemPosition - Constants.MAX_POSITION_TO_SCROLL
            }

            binding.rvTimes.scrollToPosition(position)

        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_medications -> {
                navController.tryNavigate(
                    TreatmentFragmentDirections.actionTreatmentFragmentToMedicationsFragment(
                        null,
                        R.id.treatmentHistoryFragment
                    )
                )
            }

            R.id.btn_symptoms -> {
                navController.tryNavigate(
                    TreatmentFragmentDirections.actionTreatmentFragmentToSymptomsTypesFragment(
                        null,
                        R.id.treatmentHistoryFragment
                    )
                )
            }

            R.id.btn_routine_tests -> {
                navController.tryNavigate(
                    TreatmentFragmentDirections.actionTreatmentFragmentToRoutineTestInfoFragment(
                        null
                    )
                )
            }

            R.id.btn_medical_appointment -> {
                navController.tryNavigate(
                    TreatmentFragmentDirections.actionTreatmentFragmentToMedicalReminderInfoFragment(
                        null
                    )
                )
            }
        }
    }
}