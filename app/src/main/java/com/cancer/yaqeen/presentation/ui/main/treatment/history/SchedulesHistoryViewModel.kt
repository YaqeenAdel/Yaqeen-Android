package com.cancer.yaqeen.presentation.ui.main.treatment.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetMedicationRemindersUseCase
import com.cancer.yaqeen.presentation.util.Constants
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
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateMedications = MutableStateFlow<List<Medication>>(listOf())
    val viewStateMedications = _viewStateMedications.asStateFlow()

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()


    fun getMedications() {
        viewModelJob = viewModelScope.launch {
            getMedicationRemindersUseCase(
                scheduleType = Constants.SCHEDULE_MEDICATION
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

    private suspend fun emitError(errorEntity: ErrorEntity?) {
        _viewStateError.emit(errorEntity)
        _viewStateError.emit(null)
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob = null
    }
}