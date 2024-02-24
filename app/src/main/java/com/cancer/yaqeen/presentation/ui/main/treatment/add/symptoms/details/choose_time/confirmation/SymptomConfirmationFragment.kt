package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.details.choose_time.confirmation

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentSymptomConfirmationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.SymptomsViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.enableStoragePermissions
import com.cancer.yaqeen.presentation.util.storagePermissionsAreGranted
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class SymptomConfirmationFragment : BaseFragment() {

    private var binding: FragmentSymptomConfirmationBinding by autoCleared()

    private lateinit var navController: NavController

    private val symptomsViewModel: SymptomsViewModel by activityViewModels()


    private val requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>?> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions?.run {
                if (get(Manifest.permission.READ_EXTERNAL_STORAGE) == true || get(Manifest.permission.READ_MEDIA_IMAGES) == true)
                    confirmSymptom()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSymptomConfirmationBinding.inflate(inflater, container, false)
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
        val medicationTrack = symptomsViewModel.getSymptomTrack()

        medicationTrack?.run {
            val isReminder = doctorName?.isNotEmpty() == true
            binding.tvNotesVal.text = details ?: ""
            binding.tvSymptomsVal.text = symptomTypes?.joinToString(separator = "\n"){ it.name } ?: ""
            binding.tvReminderVal.text = doctorName ?: ""
            binding.tvReminder.changeVisibility(show = isReminder, isGone = true)
            binding.tvReminderVal.changeVisibility(show = isReminder, isGone = true)
            binding.tvDateTimeVal.text = "$reminderTime - ${startDate ?: ""}"

            imageUri?.let {
                updateUI(imageUri)
            }
            imageDownloadUrl?.let {
                updateUI(imageDownloadUrl)
            }
        }
    }

    private fun updateUI(url: String?) {
        bindImage(binding.ivSymptom, url)
    }

    private fun updateUI(uri: Uri?) {
        binding.ivSymptom.setImageURI(uri)
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btnConfirm.setOnClickListener {
            confirmSymptom()

        }
    }

    private fun confirmSymptom() {
        if (!storagePermissionsAreGranted(requireContext())) {
            enableStoragePermissions(requestMultiplePermissionsLauncher)
            return
        }

        val medicationTrack = symptomsViewModel.getSymptomTrack()
        if (medicationTrack?.editable == true)
            symptomsViewModel.editSymptom()
        else
            symptomsViewModel.addSymptom()
    }

    private fun observeStates() {
        lifecycleScope {
            symptomsViewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            symptomsViewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }

        lifecycleScope {
            symptomsViewModel.viewStateAddSymptom.observe(viewLifecycleOwner) { response ->
                if(response == true){
                    Toast.makeText(requireContext(),
                        getString(R.string.symptom_added_successfully), Toast.LENGTH_SHORT).show()
                    navController.tryPopBackStack(
                        R.id.treatmentHistoryFragment,
                        false
                    )
                }
            }
        }

        lifecycleScope {
            symptomsViewModel.viewStateEditSymptom.observe(viewLifecycleOwner) { response ->
                if(response == true){
                    Toast.makeText(requireContext(),
                        getString(R.string.symptom_edited_successfully), Toast.LENGTH_SHORT).show()
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