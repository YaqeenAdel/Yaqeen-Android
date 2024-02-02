package com.cancer.yaqeen.presentation.ui.main.treatment.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time
import com.cancer.yaqeen.data.utils.getTodayDate
import com.cancer.yaqeen.databinding.FragmentTreatmentHistoryBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.TimesAdapter
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TreatmentHistoryFragment : BaseFragment(showBottomMenu = true), View.OnClickListener {

    private var binding: FragmentTreatmentHistoryBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var timesAdapter: TimesAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentTreatmentHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        setupTimesAdapter()

    }

    override fun onResume() {
        super.onResume()

        binding.tvCurrentDayDate.text = getTodayDate()
    }

    private fun setListener(){
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btnAdd.setOnClickListener(this)
        binding.btnMedications.setOnClickListener(this)
        binding.btnSymptoms.setOnClickListener(this)
        binding.btnRoutineTests.setOnClickListener(this)
        binding.btnMedicalReminder.setOnClickListener(this)

    }


    private fun setupTimesAdapter() {
        timesAdapter = TimesAdapter {

        }

        binding.rvTimes.adapter = timesAdapter


        timesAdapter.submitList(
            listOf(
                Time(0, "00:00"),
                Time(1, "1:00"),
                Time(2, "2:00"),
                Time(3, "3:00"),
                Time(4, "4:00"),
                Time(5, "5:00"),
                Time(6, "6:00"),
                Time(7, "7:00"),
                Time(8, "8:00"),
                Time(9, "9:00"),
                Time(10, "10:00"),
                Time(11, "11:00"),
                Time(12, "12:00"),
                Time(13, "13:00"),
                Time(14, "14:00"),
                Time(15, "15:00"),
                Time(16, "16:00"),
                Time(17, "17:00"),
                Time(18, "18:00"),
                Time(19, "19:00"),
                Time(20, "20:00"),
                Time(21, "21:00"),
                Time(22, "22:00"),
                Time(23, "23:00")
            )
        )

        selectItem(12)
    }

    private fun selectItem(itemId: Int) {
        val selectItemPosition = timesAdapter.selectItem(itemId)
        if(selectItemPosition >= 2)
            binding.rvTimes.scrollToPosition(selectItemPosition - 2)
    }

    private fun MaterialButton.updateUI() {
        resetUI()
        updateButtonUI(R.color.light_black, R.color.cold_white, R.color.primary_color)
    }

    private fun resetUI() {
        binding.btnMedications.updateButtonUI()
        binding.btnSymptoms.updateButtonUI()
        binding.btnRoutineTests.updateButtonUI()
        binding.btnMedicalReminder.updateButtonUI()
    }

    private fun MaterialButton.updateButtonUI(textColorId: Int = R.color.dark_gray, backgroundColorId: Int = R.color.light_gray, iconColorId: Int = R.color.medium_gray) {
        val context = requireContext()
        setTextColor(ContextCompat.getColor(context, textColorId))
        backgroundTintList = ContextCompat.getColorStateList(context, backgroundColorId)
        iconTint = ContextCompat.getColorStateList(context, iconColorId)
    }

    private fun TextView.updateUI(text: String) {
        this.text = text
    }

    override fun onClick(v: View?) {
        when(v?.id){
            R.id.btn_add -> {
                navController.tryNavigate(
                    TreatmentHistoryFragmentDirections.actionTreatmentHistoryFragmentToTreatmentFragment()
                )
            }
            R.id.btn_medications -> {
                binding.btnMedications.updateUI()
                binding.tvMedicationsHistory.updateUI(getString(R.string.history_s_medications))
            }
            R.id.btn_symptoms -> {
                binding.btnSymptoms.updateUI()
                binding.tvMedicationsHistory.updateUI(getString(R.string.history_s_medications))
            }
            R.id.btn_routine_tests -> {
                binding.btnRoutineTests.updateUI()
                binding.tvMedicationsHistory.updateUI(getString(R.string.history_s_medications))
            }
            R.id.btn_medical_reminder -> {
                binding.btnMedicalReminder.updateUI()
                binding.tvMedicationsHistory.updateUI(getString(R.string.history_s_medications))
            }
        }
    }
}
