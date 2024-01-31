package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications

import androidx.databinding.ObservableField
import androidx.lifecycle.ViewModel
import com.cancer.yaqeen.data.features.auth.models.Profile
import com.cancer.yaqeen.data.features.home.models.Day
import com.cancer.yaqeen.data.features.home.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.models.MedicationType
import com.cancer.yaqeen.data.features.home.models.ReminderTime
import com.cancer.yaqeen.data.features.home.models.Time
import com.cancer.yaqeen.data.features.home.models.UnitType
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.error.ErrorEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class MedicationsViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()

    private var medicationTrackField: ObservableField<MedicationTrack> = ObservableField(MedicationTrack())

    fun selectMedicationType(medicationType: MedicationType, medicationName: String) =
        medicationTrackField.get()?.also {
            it.medicationType = medicationType
            it.medicationName = medicationName
        }

    fun selectUnitType(unitType: UnitType, medicationAmount: String) =
        medicationTrackField.get()?.also {
            it.unitType = unitType
            it.medicationAmount = medicationAmount
        }

    fun selectPeriodTime(periodTime: Time, specificDays: List<Day>? = null, notes: String? = null) =
        medicationTrackField.get()?.also {
            it.periodTime = periodTime
            it.specificDays = specificDays
            it.notes = notes
        }

    fun selectSpecificDays(specificDays: List<Day>) =
        medicationTrackField.get()?.also {
            it.specificDays = specificDays
        }

    fun selectStartDate(startDate: Long?) =
        medicationTrackField.get()?.also {
            it.startDate = startDate
        }

    fun selectReminderTime(reminderTime: ReminderTime?) =
        medicationTrackField.get()?.also {
            it.reminderTime = reminderTime
        }

    fun selectNotes(notes: String) =
        medicationTrackField.get()?.also {
            it.notes = notes
        }

    fun getMedicationTrack(): MedicationTrack? =
        medicationTrackField.get()

    fun resetMedicationTrack() {
        medicationTrackField.set(MedicationTrack())
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