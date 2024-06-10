package com.cancer.yaqeen.presentation.ui.main.treatment.history.dialogs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentMedicationDialogBinding
import com.cancer.yaqeen.presentation.base.BaseDialogFragment
import com.cancer.yaqeen.presentation.service.AlarmReminder
import com.cancer.yaqeen.presentation.service.ReminderManager
import com.cancer.yaqeen.presentation.ui.main.treatment.getMedicationType
import com.cancer.yaqeen.presentation.ui.main.treatment.history.SchedulesHistoryViewModel
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.cancer.yaqeen.presentation.util.tryNavigateUp
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@AndroidEntryPoint
class MedicationDialogFragment : BaseDialogFragment() {

    private var binding: FragmentMedicationDialogBinding by autoCleared()

    private lateinit var navController: NavController

    private val viewModel: SchedulesHistoryViewModel by viewModels()

    private val args: MedicationDialogFragmentArgs by navArgs()

    private val medication by lazy {
        args.medication
    }

    private val workerReminder: ReminderManager by lazy {
        AlarmReminder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMedicationDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun getTheme(): Int {
        return R.style.AppBottomDialogTheme
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()

        updateUI(medication)

        observeStates()
    }

    private fun updateUI(medication: Medication) {
        with(medication){
            val timing = if(reminderTime?.isAM == true) getString(R.string.am) else getString(R.string.pm)
            binding.tvTime.text = "${reminderTime?.text} $timing"
            binding.tvMedicationDetails.text = "$medicationName $strengthAmount $unitType"
            binding.tvDosageAmount.text = "$dosageAmount $medicationType"
            binding.tvNotes.text = notes
            getMedicationType(requireContext(), medicationType)?.run { iconResId
                bindResourceImage(binding.ivMedicationIcon, iconResId)
                bindResourceImage(binding.ivMedicationIcon2, iconResId)
            }

        }
    }

    private fun setListener() {
        binding.ivClose.setOnClickListener {
            navController.tryPopBackStack()
        }
        binding.btnEdit.setOnClickListener {
            navController.tryNavigate(
                MedicationDialogFragmentDirections.actionMedicationDialogFragmentToMedicationsFragment(medication)
            )
        }
        binding.btnDelete.setOnClickListener {
            viewModel.deleteMedication(medication.id)
        }
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
            viewModel.viewStateDeleteMedication.observe(viewLifecycleOwner) { medicationId ->
                medicationId?.let {
                    Toast.makeText(requireContext(), getString(R.string.medication_deleted_successfully), Toast.LENGTH_SHORT).show()

                    setFragmentResult(
                        Constants.REQUEST_MEDICATION_ID_KEY,
                        bundleOf(
                            Constants.MEDICATION_ID_KEY to medication.id
                        )
                    )
                    navController.tryNavigateUp()
                }
            }
        }

        lifecycleScope {
            viewModel.viewStateOldMedication.observe(viewLifecycleOwner) { medication ->
                medication?.run {
                    val actionName = Constants.OPEN_MEDICATION_WINDOW_ACTION
                    val objectJsonValue = medication.json.toString()
                    workerReminder.cancelReminder(workID.toString(), actionName, objectJsonValue)
                    workerReminder.cancelReminder(workID.toString())
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