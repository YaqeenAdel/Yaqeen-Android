package com.cancer.yaqeen.presentation.ui.main.treatment.add.routine_test.choose_time.confirmation

import android.Manifest
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
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
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.databinding.FragmentRoutineTestConfirmationBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.service.AlarmReminder
import com.cancer.yaqeen.presentation.service.ReminderManager
import com.cancer.yaqeen.presentation.service.WorkerReminder
import com.cancer.yaqeen.presentation.ui.main.treatment.add.routine_test.RoutineTestViewModel
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.Constants.OPEN_ROUTINE_TEST_BEFORE_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION
import com.cancer.yaqeen.presentation.util.Constants.UPDATE_LOCAL_REMINDED_SCHEDULES_ACTION
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImageURI
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.disableTouch
import com.cancer.yaqeen.presentation.util.enableStoragePermissions
import com.cancer.yaqeen.presentation.util.enableTouch
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.DAYS
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.ERROR
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.ROUTINE_TEST
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.ROUTINE_TEST_NAME
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.TIME
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvent
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.CONFIRM_ROUTINE_TEST
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.ROUTINE_TEST_CONFIRM_FAILED
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.SET_ROUTINE_TEST_INFO
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.SYMPTOM_CONFIRM_FAILED
import com.cancer.yaqeen.presentation.util.scheduleJobServicePeriodically
import com.cancer.yaqeen.presentation.util.schedulingPermissionsAreGranted
import com.cancer.yaqeen.presentation.util.storagePermissionsAreGranted
import com.cancer.yaqeen.presentation.util.tryPopBackStack
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.concurrent.TimeUnit


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

    private val requestPermissionLauncher: ActivityResultLauncher<String?> = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->

    }

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
            binding.tvDaysVal.text = if (specificDays.isNullOrEmpty()) periodTime?.timeEn ?: "" else specificDays!!.joinToString { it.name }
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
        val routineTestTrack = routineTestViewModel.getRoutineTestTrack()
        if (!storagePermissionsAreGranted(requireContext()) && routineTestTrack?.photo?.uri != null) {
            enableStoragePermissions(requestMultiplePermissionsLauncher, requireContext())
            return
        }

        if (schedulingPermissionsAreGranted(requireActivity(), requireContext(), requestPermissionLauncher)) {
            routineTestViewModel.logEvent(
                GoogleAnalyticsEvent(
                    eventName = CONFIRM_ROUTINE_TEST,
                    eventParams = arrayOf(
                        ROUTINE_TEST to routineTestTrack.toJson(),
                    )
                )
            )
            routineTestViewModel.modifyRoutineTest()
        }
    }

    private fun addWorkerReminderPeriodically() {
        scheduleJobServicePeriodically(requireContext(), TimeUnit.MINUTES.toMillis(15), PersistableBundle().apply {
            putString(
                Constants.ACTION_KEY,
                UPDATE_LOCAL_REMINDED_SCHEDULES_ACTION
            )
        })
//        if (!routineTestViewModel.hasWorker()) {
//            val timeDelayInMilliSeconds = TimeUnit.MINUTES.toMillis(5L)
//            val currentTimeInMilliSeconds = System.currentTimeMillis()
//            val workRunningInMilliSeconds = currentTimeInMilliSeconds + timeDelayInMilliSeconds
//
//            val periodReminderId = workerReminderPeriodically.setPeriodReminder(
//                timeDelayInMilliSeconds,
//                PeriodTimeEnum.EVERY_3_HOURS.id,
//                UPDATE_LOCAL_PENDING_SCHEDULES_ACTION
//            )
//
//            routineTestViewModel.saveWorkerReminderPeriodicallyInfo(periodReminderId, workRunningInMilliSeconds)
//        }
    }

    private fun observeStates() {
        lifecycleScope {
            routineTestViewModel.viewStateLoading.collectLatest {
                if (it)
                    disableTouch()
                else
                    enableTouch()
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
                response?.let { (added, routineTest) ->
                    if (added) {
                        addWorkerReminderPeriodically()
                        if (routineTest.periodTimeId == PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id){
                            val (uuids, workBeforeID) = workerReminder.setReminderDays(routineTest.apply { json = toJson() })
                            routineTestViewModel.saveLocalRoutineTest(routineTest, uuids, workBeforeID)
                        }else {
                            val (periodicWorkID, workBeforeID) = workerReminder.setReminder(
                                routineTest.apply { json = toJson() })
                            routineTestViewModel.saveLocalRoutineTest(
                                routineTest,
                                periodicWorkID,
                                workBeforeID
                            )
                        }
                        Toast.makeText(requireContext(),
                            getString(R.string.routine_test_added_successfully), Toast.LENGTH_SHORT).show()
                        navController.tryPopBackStack(
                            R.id.treatmentHistoryFragment,
                            false
                        )
                    }
                }
            }
        }

        lifecycleScope {
            routineTestViewModel.viewStateEditRoutineTest.observe(viewLifecycleOwner) { response ->
                response?.let { (edited, routineTest) ->

                    addWorkerReminderPeriodically()

                    var uuids = listOf<String>()
                    var periodicWorkID = ""
                    var workBeforeID: String? = null
                    if (routineTest.periodTimeId == PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id){
                        val reminderInfo = workerReminder.setReminderDays(routineTest.apply { json = toJson() })

                        uuids = reminderInfo.first
                        workBeforeID = reminderInfo.second

                        modifyLocalRoutineTest(
                            edited,
                            routineTest,
                            uuids,
                            workBeforeID
                        )
                    }else {
                        val reminderInfo = workerReminder.setReminder(routineTest.apply { json = toJson() })

                        periodicWorkID = reminderInfo.first
                        workBeforeID = reminderInfo.second

                        modifyLocalRoutineTest(
                            edited,
                            routineTest,
                            periodicWorkID,
                            workBeforeID
                        )
                    }


                    Toast.makeText(requireContext(),
                        getString(R.string.routine_test_edited_successfully), Toast.LENGTH_SHORT).show()
                    navController.tryPopBackStack(
                        R.id.treatmentHistoryFragment,
                        false
                    )
                }
            }
        }

        lifecycleScope {
            routineTestViewModel.viewStateOldRoutineTest.observe(viewLifecycleOwner) { routineTest ->
                routineTest?.run {
                    val actionName = OPEN_ROUTINE_TEST_WINDOW_ACTION
                    val objectJsonValue = routineTest.json.toString()
                    workerReminder.cancelReminder(workID.toString(), actionName, objectJsonValue)
                    workerReminder.cancelReminder(workID.toString())
                    workBeforeID?.let {
                        val actionNameBefore = OPEN_ROUTINE_TEST_BEFORE_WINDOW_ACTION
                        workerReminder.cancelReminder(workID.toString(), actionNameBefore, objectJsonValue)
                        workerReminder.cancelReminder(it)
                    }
                }
            }
        }
    }

    private fun modifyLocalRoutineTest(
        edited: Boolean,
        routineTest: RoutineTestDB,
        periodicWorkID: String,
        workBeforeID: String?
    ) {
        if (edited) {
            routineTestViewModel.editLocalRoutineTest(
                routineTest,
                periodicWorkID,
                workBeforeID
            )
        }else{
            routineTestViewModel.saveLocalRoutineTest(routineTest, periodicWorkID, workBeforeID)
        }
    }

    private fun modifyLocalRoutineTest(
        edited: Boolean,
        routineTest: RoutineTestDB,
        uuids: List<String>,
        workBeforeID: String?
    ) {
        if (edited) {
            routineTestViewModel.editLocalRoutineTest(
                routineTest,
                uuids,
                workBeforeID
            )
        }else{
            routineTestViewModel.saveLocalRoutineTest(routineTest, uuids, workBeforeID)
        }
    }

    private fun handleResponseError(errorEntity: ErrorEntity?) {
        val errorMessage = handleError(errorEntity)
        displayErrorMessage(errorMessage)
    }

    private fun displayErrorMessage(errorMessage: String?) {
        errorMessage?.let {
            routineTestViewModel.logEvent(
                GoogleAnalyticsEvent(
                    eventName = ROUTINE_TEST_CONFIRM_FAILED,
                    eventParams = arrayOf(
                        ERROR to errorMessage
                    )
                )
            )
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
        }
    }
}