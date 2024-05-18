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
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.databinding.FragmentMedicationConfirmationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.service.AlarmReminder
import com.cancer.yaqeen.presentation.service.ReminderManager
import com.cancer.yaqeen.presentation.service.WorkerReminder
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.MedicationsViewModel
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICATION_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_SCHEDULES_ACTION
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.schedulingPermissionsAreGranted
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.TimeUnit


@AndroidEntryPoint
class MedicationConfirmationFragment : BaseFragment() {

    private var binding: FragmentMedicationConfirmationBinding by autoCleared()

    private lateinit var navController: NavController

    private val medicationsViewModel: MedicationsViewModel by activityViewModels()

    private val workerReminder: ReminderManager by lazy {
        AlarmReminder(requireContext())
    }

    private val workerReminderPeriodically: ReminderManager by lazy {
        WorkerReminder(requireContext())
    }

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
            binding.tvTimeVal.text = reminderTime?.run {
                val timing = if (isAM) getString(R.string.am) else getString(R.string.pm)
                "$text $timing"
            } ?: ""

        }
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnConfirm.setOnClickListener {
            if (schedulingPermissionsAreGranted(requireActivity(), requireContext())) {
                val medicationTrack = medicationsViewModel.getMedicationTrack()
                if (medicationTrack?.editable == true)
                    medicationsViewModel.editMedication()
                else
                    medicationsViewModel.addMedication()
            }
        }
    }

    private fun addWorkerReminderPeriodically() {
        if (!medicationsViewModel.hasWorker()) {
            val timeDelayInMilliSeconds = TimeUnit.MINUTES.toMillis(5L)
            val currentTimeInMilliSeconds = System.currentTimeMillis()
            val workRunningInMilliSeconds = currentTimeInMilliSeconds + timeDelayInMilliSeconds

            val periodReminderId = workerReminderPeriodically.setPeriodReminder(
                timeDelayInMilliSeconds,
                PeriodTimeEnum.EVERY_3_HOURS.id,
                UPDATE_LOCAL_SCHEDULES_ACTION
            )

            medicationsViewModel.saveWorkerReminderPeriodicallyInfo(periodReminderId, workRunningInMilliSeconds)
        }
    }

    private fun observeStates() {
        lifecycleScope {
            medicationsViewModel.viewStateLoading.collectLatest {
                //TODO: Enable an Disable the UI depend on the it
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
                response?.let { (added, medication) ->
                    if(added){
                        addWorkerReminderPeriodically()
                        if (medication.periodTimeId == PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id){
                            val uuids = workerReminder.setReminderDays(medication.apply { json = toJson() })
                            medicationsViewModel.saveLocalMedication(medication, uuids)
                        }else {
                            val uuid = workerReminder.setReminder(medication.apply { json = toJson() })
                            medicationsViewModel.saveLocalMedication(medication, uuid)
                        }
                        Toast.makeText(requireContext(),
                            getString(R.string.medication_added_successfully), Toast.LENGTH_SHORT).show()
                        navController.tryPopBackStack(
                            R.id.treatmentHistoryFragment,
                            false
                        )
                    }
                }
            }
        }

        lifecycleScope {
            medicationsViewModel.viewStateEditMedication.observe(viewLifecycleOwner) { response ->
                response?.let { (edited, medication) ->
                    addWorkerReminderPeriodically()

                    var uuids = listOf<String>()
                    var uuid = ""
                    if (medication.periodTimeId == PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id){
                        uuids = workerReminder.setReminderDays(medication.apply { json = toJson() })

                        modifyLocalMedication(edited, medication, uuids)
                    }else {
                        uuid = workerReminder.setReminder(medication.apply { json = toJson() })

                        modifyLocalMedication(edited, medication, uuid)
                    }

                    Toast.makeText(requireContext(),
                        getString(R.string.medication_edited_successfully), Toast.LENGTH_SHORT).show()
                    navController.tryPopBackStack(
                        R.id.treatmentHistoryFragment,
                        false
                    )
                }
            }
        }

        lifecycleScope {
            medicationsViewModel.viewStateOldMedication.observe(viewLifecycleOwner) { medication ->
                medication?.run {
                    val actionName = OPEN_MEDICATION_WINDOW_ACTION
                    val objectJsonValue = medication.json.toString()
                    workerReminder.cancelReminder(workID.toString(), actionName, objectJsonValue)
                    workerReminder.cancelReminder(workID.toString())
                }
            }
        }
    }

    private fun modifyLocalMedication(
        edited: Boolean,
        medication: MedicationDB,
        uuid: String
    ) {
        if (edited) {
            medicationsViewModel.editLocalMedication(medication, uuid)
        } else {
            medicationsViewModel.saveLocalMedication(medication, uuid)
        }
    }

    private fun modifyLocalMedication(
        edited: Boolean,
        medication: MedicationDB,
        uuids: List<String>
    ) {
        if (edited) {
            medicationsViewModel.editLocalMedication(medication, uuids)
        } else {
            medicationsViewModel.saveLocalMedication(medication, uuids)
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