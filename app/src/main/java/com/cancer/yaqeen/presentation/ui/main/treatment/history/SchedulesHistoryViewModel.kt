package com.cancer.yaqeen.presentation.ui.main.treatment.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetMedicationRemindersUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.DeleteSymptomUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.GetSymptomsUseCase
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SchedulesHistoryViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val getMedicationRemindersUseCase: GetMedicationRemindersUseCase,
    private val getSymptomsUseCase: GetSymptomsUseCase,
    private val deleteSymptomUseCase: DeleteSymptomUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateMedications = MutableStateFlow<List<Medication>>(listOf())
    val viewStateMedications = _viewStateMedications.asStateFlow()

    private val _viewStateSymptoms = MutableStateFlow<List<Symptom>>(listOf())
    val viewStateSymptoms = _viewStateSymptoms.asStateFlow()

    private val _viewStateDeleteSymptom = SingleLiveEvent<Int?>()
    val viewStateDeleteSymptom: LiveData<Int?> = _viewStateDeleteSymptom

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