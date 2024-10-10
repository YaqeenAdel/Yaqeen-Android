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
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.databinding.FragmentSymptomConfirmationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.SymptomsViewModel
import com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters.PhotosAdapter
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.disableTouch
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.enableStoragePermissions
import com.cancer.yaqeen.presentation.util.enableTouch
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.ERROR
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.SYMPTOM
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.SYMPTOMS_TYPES
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvent
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.CONFIRM_SYMPTOM
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.LOGIN_FAILED
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.SELECT_SYMPTOM_TYPE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.SYMPTOM_CONFIRM_FAILED
import com.cancer.yaqeen.presentation.util.recyclerview.HorizontalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.storagePermissionsAreGranted
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class SymptomConfirmationFragment : BaseFragment() {

    private var binding: FragmentSymptomConfirmationBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var photosAdapter: PhotosAdapter

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

        setupPhotosAdapter()

        setListener()

        updateUI()
        observeStates()

    }

    private fun updateUI() {
        val symptomTrack = symptomsViewModel.getSymptomTrack()

        symptomTrack?.run {
            val isReminder = doctorName?.isNotEmpty() == true
            binding.tvNotesVal.text = details ?: ""
            binding.tvSymptomsVal.text = symptomTypes?.joinToString(separator = "\n"){ it.name } ?: ""
            binding.tvReminderVal.text = doctorName ?: ""
            binding.tvReminder.changeVisibility(show = isReminder, isGone = true)
            binding.tvReminderVal.changeVisibility(show = isReminder, isGone = true)
            binding.tvDateTimeVal.text = "${reminderTime2?.timeUI.toString()} - ${startDateUI ?: ""}"

            val isMoreThanPhoto = (photosList?.size ?: 0) > 1

            binding.ivSymptom.changeVisibility(!isMoreThanPhoto)
            binding.rvSymptomPhotos.changeVisibility(isMoreThanPhoto)

            photosList?.firstOrNull()?.run {
                uri?.let {
                    updateUI(it)
                }
                url?.let {
                    updateUI(it)
                }
            }

            photosAdapter.submitList(photosList)
        }
    }


    private fun updateUI(url: String) {
        bindImage(binding.ivSymptom, url)
    }

    private fun updateUI(uri: Uri) {
        binding.ivSymptom.setImageURI(uri)
    }

    private fun setupPhotosAdapter() {
        photosAdapter = PhotosAdapter {

        }

        binding.rvSymptomPhotos.apply {
            adapter = photosAdapter
            addItemDecoration(
                HorizontalMarginItemDecoration(
                    dpToPx(18f, requireContext())
                )
            )
        }

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
        val symptomTrack = symptomsViewModel.getSymptomTrack()
        if (!storagePermissionsAreGranted(requireContext())&& symptomTrack?.photosList?.any { it.uri != null } == true) {
            enableStoragePermissions(requestMultiplePermissionsLauncher, requireContext())
            return
        }

        symptomsViewModel.logEvent(
            GoogleAnalyticsEvent(
                eventName = CONFIRM_SYMPTOM,
                eventParams = arrayOf(
                    SYMPTOM to symptomTrack.toJson(),
                )
            )
        )

        if (symptomTrack?.editable == true)
            symptomsViewModel.editSymptom()
        else
            symptomsViewModel.addSymptom()
    }

    private fun observeStates() {
        lifecycleScope {
            symptomsViewModel.viewStateLoading.collectLatest {
                if (it)
                    disableTouch()
                else
                    enableTouch()
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
                response?.let { (added, destinationId) ->
                    if(added){
                        Toast.makeText(requireContext(),
                            getString(R.string.symptom_added_successfully), Toast.LENGTH_SHORT).show()
                        navController.tryPopBackStack(
                            destinationId ?: R.id.homeFragment,
                            false
                        )
                    }
                }
            }
        }

        lifecycleScope {
            symptomsViewModel.viewStateEditSymptom.observe(viewLifecycleOwner) { response ->
                response?.let { (edited, destinationId) ->
                    if(edited){
                        Toast.makeText(requireContext(),
                            getString(R.string.symptom_edited_successfully), Toast.LENGTH_SHORT).show()
                        navController.tryPopBackStack(
                            destinationId ?: R.id.homeFragment,
                            false
                        )
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
            symptomsViewModel.logEvent(
                GoogleAnalyticsEvent(
                    eventName = SYMPTOM_CONFIRM_FAILED,
                    eventParams = arrayOf(
                        ERROR to errorMessage
                    )
                )
            )
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}