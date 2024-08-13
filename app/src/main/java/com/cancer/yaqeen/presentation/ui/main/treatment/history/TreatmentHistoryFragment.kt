package com.cancer.yaqeen.presentation.ui.main.treatment.history

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time.Companion.getHours24
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Timing
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.getTodayDate
import com.cancer.yaqeen.databinding.FragmentTreatmentHistoryBinding
import com.cancer.yaqeen.presentation.base.BaseFragment
import com.cancer.yaqeen.presentation.service.AlarmReminder
import com.cancer.yaqeen.presentation.service.ReminderManager
import com.cancer.yaqeen.presentation.ui.main.treatment.TimesAdapter
import com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters.MedicalRemindersAdapter
import com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters.MedicationsAdapter
import com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters.RoutineTestsAdapter
import com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters.SymptomsAdapter
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.autoCleared
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.VerticalMarginItemDecoration
import com.cancer.yaqeen.presentation.util.schedulingPermissionsAreGranted
import com.cancer.yaqeen.presentation.util.timestampToHour
import com.cancer.yaqeen.presentation.util.timestampToTiming
import com.cancer.yaqeen.presentation.util.tryNavigate
import com.google.android.material.button.MaterialButton
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Calendar
import javax.inject.Inject

@AndroidEntryPoint
class TreatmentHistoryFragment : BaseFragment(showBottomMenu = true), View.OnClickListener {

    private var binding: FragmentTreatmentHistoryBinding by autoCleared()

    private lateinit var navController: NavController

    private lateinit var timesAdapter: TimesAdapter
    private lateinit var medicationsAdapter: MedicationsAdapter
    private lateinit var symptomsAdapter: SymptomsAdapter
    private lateinit var medicalRemindersAdapter: MedicalRemindersAdapter
    private lateinit var routineTestsAdapter: RoutineTestsAdapter

    private val viewModel: SchedulesHistoryViewModel by viewModels()

    private var scheduledType: ScheduleType = ScheduleType.MEDICATION

    private val workerReminder: ReminderManager by lazy {
        AlarmReminder(requireContext())
    }

    private val requestPermissionLauncher: ActivityResultLauncher<String?> =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

        }

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

        setFragmentResultListener(Constants.REQUEST_MEDICATION_ID_KEY) { requestKey, bundle ->
            if (requestKey == Constants.REQUEST_MEDICATION_ID_KEY) {
                val medicationId = bundle.getInt(Constants.MEDICATION_ID_KEY)

                medicationsAdapter.deleteMedication(medicationId)
            }
        }

        navController = findNavController()

        setListener()

        setupAdapters()

        observeStates()

        when (savedInstanceState?.getInt("scheduledType") ?: ScheduleType.MEDICATION.id) {
            ScheduleType.MEDICATION.id -> enableMedications()
            ScheduleType.SYMPTOMS.id -> enableSymptoms()
            ScheduleType.MEDICAL_REMINDER.id -> enableMedicalReminders()
        }

        selectItem(getCurrentHour())
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("scheduledType", scheduledType.id)
        super.onSaveInstanceState(outState)


    }

    private fun setupAdapters() {
        setupTimesAdapter()
        setupMedicationsAdapter()
        setupSymptomsAdapter()
        setupMedicalRemindersAdapter()
        setupRoutineTestsAdapter()
    }

    override fun onResume() {
        super.onResume()

        binding.tvCurrentDayDate.text = getTodayDate()

    }

    private fun setListener() {
        binding.toolbar.setNavigationOnClickListener {
            navController.popBackStack()
        }
        binding.btnAdd.setOnClickListener(this)
        binding.btnMedications.setOnClickListener(this)
        binding.btnSymptoms.setOnClickListener(this)
        binding.btnRoutineTests.setOnClickListener(this)
        binding.btnMedicalReminder.setOnClickListener(this)

    }

    private fun enableMedications() {
        binding.btnMedications.updateUI()
        binding.tvScheduleHistory.updateUI(getString(R.string.history_s_medications))
        binding.rvMedicationsHistory.updateUI()
        scheduledType = ScheduleType.MEDICATION
        getMedications()
    }

    private fun enableSymptoms() {
        binding.btnSymptoms.updateUI()
        binding.tvScheduleHistory.updateUI(getString(R.string.history_s_symptoms))
        binding.rvSymptomsHistory.updateUI()
        scheduledType = ScheduleType.SYMPTOMS
        getSymptoms()
    }

    private fun enableMedicalReminders() {
        binding.btnMedicalReminder.updateUI()
        binding.tvScheduleHistory.updateUI(getString(R.string.history_s_medical_reminder))
        binding.rvMedicalAppointmentsHistory.updateUI()
        scheduledType = ScheduleType.MEDICAL_REMINDER
        getMedicalReminders()
    }

    private fun enableRoutineTests() {
        binding.btnRoutineTests.updateUI()
        binding.tvScheduleHistory.updateUI(getString(R.string.history_s_routine_tests))
        binding.rvRoutineTestsHistory.updateUI()
        scheduledType = ScheduleType.ROUTINE_TESTS
        getRoutineTests()
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
                medicationsAdapter.setList(medications)
            }
        }

        lifecycleScope {
            viewModel.viewStateSymptoms.collect { symptoms ->
                symptomsAdapter.setList(symptoms)
            }
        }

        lifecycleScope {
            viewModel.viewStateDeleteSymptom.observe(viewLifecycleOwner) { symptomId ->
                symptomId?.let {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.symptom_deleted_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    symptomsAdapter.deleteSymptom(symptomId)
                }
            }
        }

        lifecycleScope {
            viewModel.viewStateDeleteMedicalReminder.observe(viewLifecycleOwner) { medicalReminderId ->
                medicalReminderId?.let {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.appointment_deleted_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    medicalRemindersAdapter.deleteMedicalReminder(medicalReminderId)
                }
            }
        }

        lifecycleScope {
            viewModel.viewStateMedicalReminders.collect { medicalReminders ->
                medicalRemindersAdapter.setList(medicalReminders)
            }
        }

        lifecycleScope {
            viewModel.viewStateRoutineTests.collect { routineTests ->
                routineTestsAdapter.setList(routineTests)
            }
        }

        lifecycleScope {
            viewModel.viewStateDeleteRoutineTest.observe(viewLifecycleOwner) { routineTestId ->
                routineTestId?.let {
                    Toast.makeText(
                        requireContext(),
                        getString(R.string.routine_test_deleted_successfully),
                        Toast.LENGTH_SHORT
                    ).show()
                    routineTestsAdapter.deleteRoutineTest(routineTestId)
                }
            }
        }

        lifecycleScope {
            viewModel.viewStateOldRoutineTest.observe(viewLifecycleOwner) { routineTest ->
                routineTest?.run {
                    val actionName = Constants.OPEN_ROUTINE_TEST_WINDOW_ACTION
                    val objectJsonValue = routineTest.json.toString()
                    workerReminder.cancelReminder(workID.toString(), actionName, objectJsonValue)
                    workerReminder.cancelReminder(workID.toString())
                    workBeforeID?.let {
                        workerReminder.cancelReminder(it)
                    }
                }
            }
        }

        lifecycleScope {
            viewModel.viewStateOldMedicalReminder.observe(viewLifecycleOwner) { appointment ->
                appointment?.run {
                    val actionName = Constants.OPEN_MEDICAL_APPOINTMENT_WINDOW_ACTION
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

    private fun setupTimesAdapter() {
        timesAdapter = TimesAdapter(viewModel.selectedLanguageIsArabic()) {

        }

        binding.rvTimes.adapter = timesAdapter


        timesAdapter.submitList(
            getHours24()
        )

    }


    private fun setupMedicationsAdapter() {
        medicationsAdapter = MedicationsAdapter(
            onItemClick = {
                navController.tryNavigate(
                    TreatmentHistoryFragmentDirections.actionTreatmentHistoryFragmentToMedicationDialogFragment(
                        it
                    )
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

    private fun setupSymptomsAdapter() {
        symptomsAdapter = SymptomsAdapter(
            onEditClick = {
                navController.tryNavigate(
                    TreatmentHistoryFragmentDirections.actionTreatmentHistoryFragmentToSymptomsTypesFragment(
                        it,
                        R.id.treatmentHistoryFragment
                    )
                )
            },
            onDeleteClick = {
                viewModel.deleteSymptom(it.id)
            }
        )
        binding.rvSymptomsHistory.apply {
            adapter = symptomsAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(24f, requireContext())
                )
            )
        }
    }

    private fun setupMedicalRemindersAdapter() {
        medicalRemindersAdapter = MedicalRemindersAdapter(
            onEditClick = {
                navController.tryNavigate(
                    TreatmentHistoryFragmentDirections.actionTreatmentHistoryFragmentToMedicalReminderInfoFragment(
                        it
                    )
                )
            },
            onDeleteClick = {
                viewModel.deleteMedicalReminder(it.id)
            }
        )
        binding.rvMedicalAppointmentsHistory.apply {
            adapter = medicalRemindersAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(24f, requireContext())
                )
            )
        }
    }

    private fun setupRoutineTestsAdapter() {
        routineTestsAdapter = RoutineTestsAdapter(
            onEditClick = {
                navController.tryNavigate(
                    TreatmentHistoryFragmentDirections.actionTreatmentHistoryFragmentToRoutineTestInfoFragment(
                        it
                    )
                )
            },
            onDeleteClick = {
                viewModel.deleteRoutineTest(it.id)
            }
        )
        binding.rvRoutineTestsHistory.apply {
            adapter = routineTestsAdapter
            addItemDecoration(
                VerticalMarginItemDecoration(
                    dpToPx(24f, requireContext())
                )
            )
        }
    }

    private fun getCurrentHour(): Int {
        val currentDate = Calendar.getInstance()
        val timing = (currentDate.timeInMillis.timestampToTiming())

        return (currentDate.timeInMillis.timestampToHour().toIntOrNull()
            ?: 0) + if (timing == Timing.PM.id) 12 else 0
    }

    private fun selectItem(itemId: Int) {
        val selectItemPosition = timesAdapter.selectItem(itemId)
        if (selectItemPosition >= Constants.MAX_POSITION_TO_SCROLL) {
            val position = when (selectItemPosition) {
                timesAdapter.itemCount - Constants.MAX_POSITION_TO_SCROLL -> selectItemPosition + 1
                timesAdapter.itemCount - 1 -> selectItemPosition
                else -> selectItemPosition - Constants.MAX_POSITION_TO_SCROLL
            }

            binding.rvTimes.scrollToPosition(position)

        }
    }

    private fun getMedications() {
        viewModel.getMedications()
    }

    private fun getSymptoms() {
        viewModel.getSymptoms()
    }

    private fun getMedicalReminders() {
        viewModel.getMedicalReminders()
    }

    private fun getRoutineTests() {
        viewModel.getRoutineTests()
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
        binding.rvRoutineTestsHistory.changeVisibility(show = false, isGone = true)
        binding.rvMedicalAppointmentsHistory.changeVisibility(show = false, isGone = true)
    }

    private fun resetUI() {
        binding.btnMedications.updateButtonUI()
        binding.btnSymptoms.updateButtonUI()
        binding.btnRoutineTests.updateButtonUI()
        binding.btnMedicalReminder.updateButtonUI()
    }

    private fun MaterialButton.updateButtonUI(
        textColorId: Int = R.color.dark_gray,
        backgroundColorId: Int = R.color.light_gray,
        iconColorId: Int = R.color.medium_gray
    ) {
        val context = requireContext()
        setTextColor(ContextCompat.getColor(context, textColorId))
        backgroundTintList = ContextCompat.getColorStateList(context, backgroundColorId)
        iconTint = ContextCompat.getColorStateList(context, iconColorId)
    }

    private fun TextView.updateUI(text: String) {
        this.text = text
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add -> {
                if (viewModel.userIsLoggedIn()) {
                    if (schedulingPermissionsAreGranted(
                            requireActivity(),
                            requireContext(),
                            requestPermissionLauncher
                        )
                    )
                        navController.tryNavigate(
                            TreatmentHistoryFragmentDirections.actionTreatmentHistoryFragmentToTreatmentFragment()
                        )
                } else {
                    navController.tryNavigate(R.id.authFragment)
                }
            }

            R.id.btn_medications -> {
                enableMedications()
            }

            R.id.btn_symptoms -> {
                enableSymptoms()
            }

            R.id.btn_routine_tests -> {
                enableRoutineTests()
            }

            R.id.btn_medical_reminder -> {
                enableMedicalReminders()
            }
        }
    }
}
