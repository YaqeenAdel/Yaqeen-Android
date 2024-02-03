package com.cancer.yaqeen.presentation.ui.main.treatment.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time.Companion.getHours24
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.getTodayDate
import com.cancer.yaqeen.databinding.FragmentTreatmentHistoryBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.TimesAdapter
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class TreatmentHistoryFragment : BaseFragment(showBottomMenu = true), View.OnClickListener {

    private var binding: FragmentTreatmentHistoryBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var timesAdapter: TimesAdapter
    private lateinit var medicationsAdapter: MedicationsAdapter

    private val viewModel: SchedulesHistoryViewModel by viewModels()

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

        setupAdapters()

        observeStates()

    }

    private fun setupAdapters() {
        setupTimesAdapter()
        setupMedicationsAdapter()
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


    private fun observeStates() {
        lifecycleScope {
            viewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            viewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }

        lifecycleScope {
            viewModel.viewStateMedications.collect { medications ->
                medicationsAdapter.submitList(medications)
            }
        }
    }

    private fun handleResponseError(errorEntity: ErrorEntity?) {
        val errorMessage = handleError(errorEntity)
        displayErrorMessage(errorMessage)
    }

    private fun displayErrorMessage(errorMessage: String?) {
        errorMessage?.let {
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupTimesAdapter() {
        timesAdapter = TimesAdapter {

        }

        binding.rvTimes.adapter = timesAdapter


        timesAdapter.submitList(
            getHours24()
        )

        selectItem(12)
    }


    private fun setupMedicationsAdapter() {
        medicationsAdapter = MedicationsAdapter(
            onItemClick = {
                navController.tryNavigate(
                    TreatmentHistoryFragmentDirections.actionTreatmentHistoryFragmentToMedicationDialogFragment(it)
                )
            }
        )
        binding.rvMedicationsHistory.apply {
            adapter = medicationsAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(16f, requireContext())
                )
            )
        }
    }

    private fun selectItem(itemId: Int) {
        val selectItemPosition = timesAdapter.selectItem(itemId)
        if(selectItemPosition >= Constants.MAX_POSITION_TO_SCROLL)
            binding.rvTimes.scrollToPosition(selectItemPosition - Constants.MAX_POSITION_TO_SCROLL)
    }

    private fun getMedications() {
        viewModel.getMedications()
    }

    private fun MaterialButton.updateUI() {
        resetUI()
        updateButtonUI(R.color.light_black, R.color.cold_white, R.color.primary_color)
    }

    private fun RecyclerView.updateUI() {
        resetRecyclerUI()
        changeVisibility(show = true)
    }

    private fun resetRecyclerUI() {
        binding.rvMedicationsHistory.changeVisibility(show = false, isGone = true)
        binding.rvSymptomsHistory.changeVisibility(show = false, isGone = true)
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
                binding.tvScheduleHistory.updateUI(getString(R.string.history_s_medications))
                binding.rvMedicationsHistory.updateUI()
                getMedications()
            }
            R.id.btn_symptoms -> {
                binding.btnSymptoms.updateUI()
                binding.tvScheduleHistory.updateUI(getString(R.string.history_s_symptoms))
                binding.rvSymptomsHistory.updateUI()
            }
            R.id.btn_routine_tests -> {
                binding.btnRoutineTests.updateUI()
                binding.tvScheduleHistory.updateUI(getString(R.string.history_s_routine_tests))
            }
            R.id.btn_medical_reminder -> {
                binding.btnMedicalReminder.updateUI()
                binding.tvScheduleHistory.updateUI(getString(R.string.history_s_medical_reminder))
            }
        }
    }
}
