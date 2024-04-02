package com.cancer.yaqeen.presentation.ui.main.treatment.add.routine_test.choose_time.confirmation

import android.Manifest
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
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
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.databinding.FragmentRoutineTestConfirmationBinding
import com.cancer.yaqeen.databinding.FragmentSymptomConfirmationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.ui.main.treatment.add.routine_test.RoutineTestViewModel
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImageURI
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.enableStoragePermissions
import com.cancer.yaqeen.presentation.util.storagePermissionsAreGranted
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest


@AndroidEntryPoint
class RoutineTestConfirmationFragment : BaseFragment() {

    private var binding: FragmentRoutineTestConfirmationBinding by autoCleared()

    private lateinit var navController: NavController

    private val routineTestViewModel: RoutineTestViewModel by activityViewModels()


    private val requestMultiplePermissionsLauncher: ActivityResultLauncher<Array<String>?> =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            permissions?.run {
                if (get(Manifest.permission.READ_EXTERNAL_STORAGE) == true || get(Manifest.permission.READ_MEDIA_IMAGES) == true)
                    confirmRoutineTest()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentRoutineTestConfirmationBinding.inflate(inflater, container, false)
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
        val routineTestTrack = routineTestViewModel.getRoutineTestTrack()

        routineTestTrack?.run {
            binding.tvRoutineTestName.text = routineTestName ?: ""
            binding.tvNotesVal.text = notes ?: ""
            binding.tvDaysVal.text = if (specificDays.isNullOrEmpty()) periodTime?.time ?: "" else specificDays!!.joinToString { it.name }
            binding.tvStartFromVal.text = startDate?.let { convertMilliSecondsToDate(it) } ?: ""
            val reminderBeforeTime = getReminderBeforeTime(reminderBefore)
            binding.tvReminderVal.text = reminderBeforeTime
            binding.tvTimeVal.text = reminderTime?.run {
                val timing = if (isAM) getString(R.string.am) else getString(R.string.pm)
                "$text $timing"
            } ?: ""

            photo?.run {
                uri?.let {
                    updateUI(it)
                }
                url?.let {
                    updateUI(it)
                }
            }
        }
    }

    private fun getReminderBeforeTime(reminderBefore: ReminderBefore): String =
        if (reminderBefore.isMoreThanOrEqualHour)
            getString(R.string.before_time_hour, reminderBefore.timeDigits)
        else
            getString(R.string.before_time_min, reminderBefore.timeDigits)

    private fun updateUI(url: String) {
        bindImage(binding.ivRoutine, url)
    }

    private fun updateUI(uri: Uri) {
        bindImageURI(binding.ivRoutine, uri)
    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btnConfirm.setOnClickListener {
            confirmRoutineTest()
        }
    }

    private fun confirmRoutineTest() {
        if (!storagePermissionsAreGranted(requireContext())) {
            enableStoragePermissions(requestMultiplePermissionsLauncher)
            return
        }

        routineTestViewModel.modifyRoutineTest()
    }

    private fun observeStates() {
        lifecycleScope {
            routineTestViewModel.viewStateLoading.collectLatest {
                onLoading(it)
            }
        }
        lifecycleScope {
            routineTestViewModel.viewStateError.collectLatest {
                handleResponseError(it)
            }
        }

        lifecycleScope {
            routineTestViewModel.viewStateAddRoutineTest.observe(viewLifecycleOwner) { response ->
                if(response == true){
                    Toast.makeText(requireContext(),
                        getString(R.string.routine_test_added_successfully), Toast.LENGTH_SHORT).show()
                    navController.tryPopBackStack(
                        R.id.treatmentHistoryFragment,
                        false
                    )
                }
            }
        }

        lifecycleScope {
            routineTestViewModel.viewStateEditRoutineTest.observe(viewLifecycleOwner) { response ->
                if(response == true){
                    Toast.makeText(requireContext(),
                        getString(R.string.routine_test_edited_successfully), Toast.LENGTH_SHORT).show()
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