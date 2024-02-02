package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time.select_time.confirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentMedicationConfirmationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.MedicationsViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MedicationConfirmationFragment : BaseFragment() {

    private var binding: FragmentMedicationConfirmationBinding by autoCleared()

    private lateinit var navController: NavController

    private val medicationsViewModel: MedicationsViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMedicationConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        updateUI()

        observeStates()
    }

    private fun updateUI() {
        val medicationTrack = medicationsViewModel.getMedicationTrack()

        medicationTrack?.run {
            binding.tvMedicationName.text = medicationName ?: ""
            binding.tvMedicationDetails.text = "${medicationType?.name ?: ""}, ${strengthAmount ?: ""} ${unitType?.name ?: ""}"
            medicationType?.apply { bindResourceImage(binding.ivMedicationType, iconResId) }
            binding.tvNotesVal.text = notes ?: ""
            binding.tvAmountVal.text = "${dosageAmount ?: ""} ${medicationType?.name ?: ""}"
            binding.tvDaysVal.text = if (specificDays.isNullOrEmpty()) periodTime?.time ?: "" else specificDays!!.joinToString { it.name }
            binding.tvStartFromVal.text = startDate?.let { convertMilliSecondsToDate(it) } ?: ""
            binding.tvTimeVal.text = reminderTime?.run { text } ?: ""

        }
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnConfirm.setOnClickListener {
            medicationsViewModel.addMedication()
        }
    }

    private fun observeStates() {
        lifecycleScope {
            medicationsViewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            medicationsViewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }

        lifecycleScope {
            medicationsViewModel.viewStateAddMedication.observe(viewLifecycleOwner) { response ->
                if(response == true){
                    Toast.makeText(requireContext(),
                        getString(R.string.medication_added_successfully), Toast.LENGTH_SHORT).show()
                    navController.tryPopBackStack(
                        R.id.homeFragment,
                        false
                    )
                }
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

}