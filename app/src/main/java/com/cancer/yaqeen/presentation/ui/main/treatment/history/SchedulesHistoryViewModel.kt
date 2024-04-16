package com.cancer.yaqeen.presentation.ui.main.treatment.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTest
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.DeleteScheduleUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.GetLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.GetMedicalRemindersUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.RemoveLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetMedicationRemindersUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.GetLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.GetRoutineTestsUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.RemoveLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.DeleteSymptomUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.GetSymptomsUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class SchedulesHistoryViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val getMedicationRemindersUseCase: GetMedicationRemindersUseCase,
    private val getSymptomsUseCase: GetSymptomsUseCase,
    private val getMedicalRemindersUseCase: GetMedicalRemindersUseCase,
    private val getRoutineTestsUseCase: GetRoutineTestsUseCase,
    private val deleteSymptomUseCase: DeleteSymptomUseCase,
    private val deleteScheduleUseCase: DeleteScheduleUseCase,
    private val getLocalRoutineTestUseCase: GetLocalRoutineTestUseCase,
    private val getLocalMedicalAppointmentUseCase: GetLocalMedicalAppointmentUseCase,
    private val removeLocalRoutineTestUseCase: RemoveLocalRoutineTestUseCase,
    private val removeLocalMedicalAppointmentUseCase: RemoveLocalMedicalAppointmentUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateMedications = MutableStateFlow<List<Medication>>(listOf())
    val viewStateMedications = _viewStateMedications.asStateFlow()

    private val _viewStateSymptoms = MutableStateFlow<List<Symptom>>(listOf())
    val viewStateSymptoms = _viewStateSymptoms.asStateFlow()

    private val _viewStateMedicalReminders = MutableStateFlow<List<MedicalReminder>>(listOf())
    val viewStateMedicalReminders = _viewStateMedicalReminders.asStateFlow()

    private val _viewStateRoutineTests = MutableStateFlow<List<RoutineTest>>(listOf())
    val viewStateRoutineTests = _viewStateRoutineTests.asStateFlow()

    private val _viewStateDeleteSymptom = SingleLiveEvent<Int?>()
    val viewStateDeleteSymptom: LiveData<Int?> = _viewStateDeleteSymptom

    private val _viewStateDeleteMedicalReminder = SingleLiveEvent<Int?>()
    val viewStateDeleteMedicalReminder: LiveData<Int?> = _viewStateDeleteMedicalReminder

    private val _viewStateDeleteRoutineTest = SingleLiveEvent<Int?>()
    val viewStateDeleteRoutineTest: LiveData<Int?> = _viewStateDeleteRoutineTest

    private val _viewStateWorkIds = SingleLiveEvent<Pair<UUID, UUID?>?>()
    val viewStateWorkIds: LiveData<Pair<UUID, UUID?>?> = _viewStateWorkIds

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()


    fun getMedications() {
        if (!userIsLoggedIn())
            return
        viewModelJob = viewModelScope.launch {
            getMedicationRemindersUseCase(
                scheduleType = ScheduleType.MEDICATION.scheduleType
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateMedications.emit(it)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun getSymptoms() {
        if (!userIsLoggedIn())
            return
        viewModelJob = viewModelScope.launch {
            getSymptomsUseCase().collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateSymptoms.emit(it)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun getMedicalReminders() {
        if (!userIsLoggedIn())
            return
        viewModelJob = viewModelScope.launch {
            getMedicalRemindersUseCase(scheduleType = ScheduleType.MEDICAL_REMINDER.scheduleType).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateMedicalReminders.emit(it)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun getRoutineTests() {
        if (!userIsLoggedIn())
            return
        viewModelJob = viewModelScope.launch {
            getRoutineTestsUseCase(scheduleType = ScheduleType.ROUTINE_TESTS.scheduleType).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateRoutineTests.emit(it)
                        }
                    }

                    else -> {}
                }
            }
        }
    }
    fun deleteSymptom(symptomId: Int) {
        viewModelJob = viewModelScope.launch {
            deleteSymptomUseCase(
                symptomId = symptomId
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        if (response.data == true) {
                            _viewStateDeleteSymptom.postValue(symptomId)
                        }
                    }
                    else -> {}
                }
            }
        }
    }
    fun deleteMedicalReminder(scheduleId: Int) {
        viewModelJob = viewModelScope.launch {
            deleteScheduleUseCase(
                scheduleId = scheduleId
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        if (response.data == true) {
                            getLocalMedicalReminder(scheduleId)
                            _viewStateDeleteMedicalReminder.postValue(scheduleId)
                        }
                    }
                    else -> {}
                }
            }
        }
    }


    private fun getLocalMedicalReminder(medicalAppointmentId: Int) {
        viewModelJob = viewModelScope.launch {
            getLocalMedicalAppointmentUseCase(
                medicalAppointmentId = medicalAppointmentId
            ).collect { response ->
                when (response.status) {
                    Status.ERROR -> {}
                    Status.SUCCESS -> {
                        response.data?.workID?.let {
                            _viewStateWorkIds.postValue(it to response.data.workBeforeID)
                        }

                        removeLocalMedicalReminder(medicalAppointmentId)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun removeLocalMedicalReminder(medicalAppointmentId: Int) {
        viewModelJob = viewModelScope.launch {
            removeLocalMedicalAppointmentUseCase(
                medicalAppointmentId = medicalAppointmentId
            ).collect {}
        }
    }

    fun deleteRoutineTest(scheduleId: Int) {
        viewModelJob = viewModelScope.launch {
            deleteScheduleUseCase(
                scheduleId = scheduleId
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        if (response.data == true) {
                            getLocalRoutineTest(scheduleId)
                            _viewStateDeleteRoutineTest.postValue(scheduleId)
                        }
                    }
                    else -> {}
                }
            }
        }
    }


    private fun getLocalRoutineTest(routineTestId: Int) {
        viewModelJob = viewModelScope.launch {
            getLocalRoutineTestUseCase(
                routineTestId = routineTestId
            ).collect { response ->
                when (response.status) {
                    Status.ERROR -> {}
                    Status.SUCCESS -> {
                        response.data?.workID?.let {
                            _viewStateWorkIds.postValue(it to response.data.workBeforeID)
                        }

                        removeLocalRoutineTest(routineTestId)
                    }
                    else -> {}
                }
            }
        }
    }


    private fun removeLocalRoutineTest(routineTestId: Int) {
        viewModelJob = viewModelScope.launch {
            removeLocalRoutineTestUseCase(
                routineTestId = routineTestId
            ).collect {}
        }
    }


    fun userIsLoggedIn() =
        prefEncryptionUtil.isLogged


    private suspend fun emitError(errorEntity: ErrorEntity?) {
        _viewStateError.emit(errorEntity)
        _viewStateError.emit(null)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob = null
    }
}