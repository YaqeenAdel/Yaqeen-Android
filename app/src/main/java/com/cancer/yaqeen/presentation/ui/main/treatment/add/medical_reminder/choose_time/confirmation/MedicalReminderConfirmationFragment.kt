package com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder.choose_time.confirmation

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.databinding.FragmentMedicalReminderConfirmationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.service.AlarmReminder
import com.cancer.yaqeen.presentation.service.ReminderManager
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder.MedicalReminderViewModel
import com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters.SmallPhotosAdapter
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.schedulingPermissionsAreGranted
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MedicalReminderConfirmationFragment : BaseFragment() {

    private var binding: FragmentMedicalReminderConfirmationBinding by autoCleared()

    private lateinit var navController: NavController

    private val medicalReminderViewModel: MedicalReminderViewModel by activityViewModels()


    private lateinit var photosAdapter: SmallPhotosAdapter

    private val workerReminder: ReminderManager by lazy {
        AlarmReminder(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentMedicalReminderConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = findNavController()

        setListener()
        updateUI()
        observeStates()
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }

        binding.btnConfirm.setOnClickListener {
            if (schedulingPermissionsAreGranted(requireActivity(), requireContext()))
                medicalReminderViewModel.modifyMedicalReminder()
        }
        binding.tvShowLess.setOnClickListener {
            binding.groupSymptom.changeVisibility(show = true, isGone = true)
            binding.groupSymptomDetails.changeVisibility(show = false, isGone = true)
        }
        binding.tvShowMore.setOnClickListener {
            binding.groupSymptom.changeVisibility(show = false, isGone = true)
            binding.groupSymptomDetails.changeVisibility(show = true, isGone = true)
        }
    }

    private fun updateUI() {
        val medicalReminderTrack = medicalReminderViewModel.getMedicalReminderTrack()

        medicalReminderTrack?.run {
            binding.tvDoctorName.text = doctorName ?: ""
            binding.tvNotesVal.text = notes ?: ""
            binding.tvLocationVal.text = location ?: ""
            binding.tvStartingDateVal.text = startDate?.let { convertMilliSecondsToDate(it) }
            reminderTime?.run {
                val timing = if (isAM) getString(R.string.am) else getString(R.string.pm)
                binding.tvTimeVal.text = "$text $timing"
            }

            val reminderBeforeTime = getReminderBeforeTime(reminderBefore)
            binding.tvReminderVal.text = reminderBeforeTime


            val symptomIsSelected = symptom != null

            binding.groupSymptom.changeVisibility(show = symptomIsSelected, isGone = true)
            binding.groupSymptomDetails.changeVisibility(show = false, isGone = true)

            symptom?.run {
                val types = symptomTypes?.joinToString(separator = "\n"){ it.name } ?: ""
                val isReminder = doctorName?.isNotEmpty() == true

                bindImage(binding.ivSymptom, photosList?.firstOrNull()?.url)
                binding.tvSymptomTypes.text = types

                photosList?.let {
                    setupPhotosAdapter(photosList!!.map { it.url ?: "" })
                }

                binding.tvSymptomsVal.text = types
                binding.tvSymptomNotesVal.text = details
                binding.tvReminderSymptomVal.text = doctorName ?: ""
                binding.tvReminderSymptom.changeVisibility(show = isReminder, isGone = true)
                binding.tvReminderSymptomVal.changeVisibility(show = isReminder, isGone = true)
                binding.tvDateTimeVal.text = "$reminderTime - $startDate"

            }

        }
    }

    private fun setupPhotosAdapter(photosList: List<String>) {
        photosAdapter = SmallPhotosAdapter {

        }

        binding.rvSymptomPhotos.apply {
            adapter = photosAdapter
        }

        photosAdapter.submitList(photosList)

    }

    private fun getReminderBeforeTime(reminderBefore: ReminderBefore): String =
        if (reminderBefore.isMoreThanOrEqualHour)
            getString(R.string.before_time_hour, reminderBefore.timeDigits)
        else
            getString(R.string.before_time_min, reminderBefore.timeDigits)

    private fun observeStates() {
        lifecycleScope {
            medicalReminderViewModel.viewStateLoading.collectLatest {
                //TODO: Enable an Disable the UI depend on the it
                onLoading(it)
            }
        }
        lifecycleScope {
            medicalReminderViewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }

        lifecycleScope {
            medicalReminderViewModel.viewStateAddMedicalReminder.observe(viewLifecycleOwner) { response ->
                response?.let { (added, medicalAppointment) ->
                    if (added) {
                        val (workID, workBeforeID) = workerReminder.setReminder(medicalAppointment.apply { json = toJson() })
                        medicalReminderViewModel.saveLocalMedicalAppointment(medicalAppointment, workID, workBeforeID)
                        Toast.makeText(requireContext(),
                            getString(R.string.appointment_added_successfully), Toast.LENGTH_SHORT).show()
                        navController.tryPopBackStack(
                            R.id.treatmentHistoryFragment,
                            false
                        )
                    }
                }
            }
        }

        lifecycleScope {
            medicalReminderViewModel.viewStateEditMedicalReminder.observe(viewLifecycleOwner) { response ->
                response?.let { (edited, medicalAppointment) ->
                    val (workID, workBeforeID) = workerReminder.setReminder(medicalAppointment.apply { json = toJson() })
                    if (edited) {
                        medicalReminderViewModel.editLocalMedicalAppointment(medicalAppointment, workID, workBeforeID)
                    }else{
                        medicalReminderViewModel.saveLocalMedicalAppointment(medicalAppointment, workID, workBeforeID)
                    }
                    Toast.makeText(requireContext(),
                        getString(R.string.appointment_edited_successfully), Toast.LENGTH_SHORT).show()
                    navController.tryPopBackStack(
                        R.id.treatmentHistoryFragment,
                        false
                    )
                }
            }
        }

        lifecycleScope {
            medicalReminderViewModel.viewStateOldMedicalReminder.observe(viewLifecycleOwner) { appointment ->
                appointment?.run {
                    val actionName = OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION
                    val objectJsonValue = appointment.json.toString()
                    workerReminder.cancelReminder(workID.toString(), actionName, objectJsonValue)
                    workerReminder.cancelReminder(workID.toString())
                    workBeforeID?.let {
                        workerReminder.cancelReminder(it)
                    }
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