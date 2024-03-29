package com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder.choose_time.confirmation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentMedicalReminderConfirmationBinding
import com.cancer.yaqeen.databinding.FragmentSymptomConfirmationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder.MedicalReminderViewModel
import com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.SymptomsViewModel
import com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters.PhotosAdapter
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class MedicalReminderConfirmationFragment : BaseFragment() {

    private var binding: FragmentMedicalReminderConfirmationBinding by autoCleared()

    private lateinit var navController: NavController

    private val medicalReminderViewModel: MedicalReminderViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            medicalReminderViewModel.modifyMedicalReminder()
        }
    }

    private fun updateUI() {
        val medicalReminderTrack = medicalReminderViewModel.getMedicalReminderTrack()

        medicalReminderTrack?.run {
            binding.tvDoctorName.text = doctorName ?: ""
            binding.tvNotesVal.text = notes ?: ""
            binding.tvLocationVal.text = location ?: ""
            binding.tvStartingDateVal.text = startDate ?: ""
            binding.tvTimeVal.text = reminderTime ?: ""
            binding.tvReminderVal.text = reminderBeforeTime ?: ""


            val symptomIsSelected = symptom != null

            binding.groupSymptom.changeVisibility(show = symptomIsSelected, isGone = true)

            symptom?.run {
                val types = symptomTypes?.joinToString(separator = "\n"){ it.name } ?: ""

                bindImage(binding.ivSymptom, photosList?.firstOrNull()?.url)
                binding.tvSymptomTypes.text = types

            }

        }
    }

    private fun observeStates() {
        lifecycleScope {
            medicalReminderViewModel.viewStateLoading.collectLatest {
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
                if(response == true){
                    Toast.makeText(requireContext(),
                        getString(R.string.appointment_added_successfully), Toast.LENGTH_SHORT).show()
                    navController.tryPopBackStack(
                        R.id.treatmentHistoryFragment,
                        false
                    )
                }
            }
        }

        lifecycleScope {
            medicalReminderViewModel.viewStateEditMedicalReminder.observe(viewLifecycleOwner) { response ->
                if(response == true){
                    Toast.makeText(requireContext(),
                        getString(R.string.appointment_edited_successfully), Toast.LENGTH_SHORT).show()
                    navController.tryPopBackStack(
                        R.id.treatmentHistoryFragment,
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