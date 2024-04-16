package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Day
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationTrack
import com.cancer.yaqeen.data.features.home.schedule.medication.models.MedicationType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time
import com.cancer.yaqeen.data.features.home.schedule.medication.models.UnitType
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.schedule.medication.AddMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.EditMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetMedicationRemindersUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.RemoveLocalMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.SaveLocalMedicationUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import com.cancer.yaqeen.presentation.util.timestampToDay
import com.cancer.yaqeen.presentation.util.timestampToMonth
import com.cancer.yaqeen.presentation.util.timestampToYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class MedicationsViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val addMedicationUseCase: AddMedicationUseCase,
    private val editMedicationUseCase: EditMedicationUseCase,
    private val getMedicationRemindersUseCase: GetMedicationRemindersUseCase,
    private val saveLocalMedicationUseCase: SaveLocalMedicationUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateAddMedication = SingleLiveEvent<Pair<Boolean, MedicationDB>?>()
    val viewStateAddMedication: LiveData<Pair<Boolean, MedicationDB>?> = _viewStateAddMedication

    private val _viewStateEditMedication = SingleLiveEvent<Boolean?>()
    val viewStateEditMedication: LiveData<Boolean?> = _viewStateEditMedication

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()

    private var medicationTrackField: ObservableField<MedicationTrack> = ObservableField(
        MedicationTrack()
    )

    fun selectMedicationType(medicationType: MedicationType, medicationName: String) =
        medicationTrackField.get()?.also {
            it.medicationType = medicationType
            it.medicationName = medicationName
        }

    fun selectUnitType(unitType: UnitType, strengthAmount: String) =
        medicationTrackField.get()?.also {
            it.unitType = unitType
            it.strengthAmount = strengthAmount
        }

    fun selectPeriodTime(periodTime: Time, specificDays: List<Day>? = null, notes: String? = null, dosageAmount: String? = null) =
        medicationTrackField.get()?.also {
            it.periodTime = periodTime
            it.specificDays = specificDays
            it.notes = notes
            it.dosageAmount = dosageAmount
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

    fun selectNotesAndDosageAmount(notes: String, dosageAmount: String) =
        medicationTrackField.get()?.also {
            it.notes = notes
            it.dosageAmount = dosageAmount
        }

    fun getMedicationTrack(): MedicationTrack? =
        medicationTrackField.get()

    fun resetMedicationTrack() {
        medicationTrackField.set(MedicationTrack())
    }

    fun addMedication() {
        viewModelJob = viewModelScope.launch {
            val medicationTrackField = getMedicationTrack()
            medicationTrackField?.run {
                val requestBuilder = AddMedicationRequestBuilder(
                    medicationName = medicationName ?: "",
                    medicationTypeName = medicationType?.nameEn ?: "",
                    unitTypeName = unitType?.name ?: "",
                    strengthAmount = strengthAmount ?: "",
                    dosageAmount = dosageAmount ?: "",
                    cronExpression = createCronExpression(
                        minutes = reminderTime?.minute ?: "0",
                        startingHour = reminderTime?.hour24 ?: "0",
                        time = periodTime,
                        startingDate = startDate,
                        specificDays = specificDays
                    ),
                    notes = notes ?: ""
                )
                addMedicationUseCase(
                    requestBuilder.buildRequestBody()
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                val medicationDB =
                                    createMedicationDB(
                                        requestBuilder,
                                        startDate,
                                        reminderTime,
                                        periodTime?.id,
                                        it
                                    )
                                resetMedicationTrack()
                                _viewStateAddMedication.postValue(true to medicationDB)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun createMedicationDB(
        builder: AddMedicationRequestBuilder,
        startDate: Long?,
        reminderTime: ReminderTime?,
        periodTimeId: Int?,
        medicationId: Int,
    ): MedicationDB =
        builder.run {
            MedicationDB(
                medicationId = medicationId,
                medicationName = medicationName,
                medicationType = medicationTypeName,
                strengthAmount = strengthAmount,
                unitType = unitTypeName,
                dosageAmount = dosageAmount,
                notes = notes,
                scheduleType = ScheduleType.MEDICATION.scheduleType,
                cronExpression = cronExpression,
                startDate = startDate ?: 0L,
                hour24 = reminderTime?.hour24?.toIntOrNull() ?: 0,
                minute = reminderTime?.minute?.toIntOrNull() ?: 0,
                periodTimeId = periodTimeId
            )
        }

    fun saveLocalMedication(medication: MedicationDB, uuid: UUID) {
        viewModelJob = viewModelScope.launch {
            saveLocalMedicationUseCase(
                medication.apply {
                    workID = uuid
                }
            ).collect()
        }
    }

    fun editMedication(){
        viewModelJob = viewModelScope.launch {
            val medicationTrackField = getMedicationTrack()
            medicationTrackField?.run {
                editMedicationUseCase(
                    scheduleId = medicationId ?: 0,
                    request = AddMedicationRequestBuilder(
                        medicationName = medicationName ?: "",
                        medicationTypeName = medicationType?.nameEn ?: "",
                        unitTypeName = unitType?.name ?: "",
                        strengthAmount = strengthAmount ?: "",
                        dosageAmount = dosageAmount ?: "",
                        cronExpression = createCronExpression(
                            minutes = reminderTime?.minute ?: "0",
                            startingHour = reminderTime?.hour24 ?: "0",
                            time = periodTime,
                            startingDate = startDate,
                            specificDays = specificDays
                        ),
                        notes = notes ?: ""
                    ).buildRequestBody()
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            if (response.data == true) {
                                resetMedicationTrack()
                                _viewStateEditMedication.postValue(true)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun createCronExpression(
        minutes: String,
        startingHour: String,
        time: Time?,
        startingDate: Long?,
        specificDays: List<Day>?
    ): String {
        val seconds = "0"
        val hours = "$startingHour${time?.cronExpression ?: ""}"
        val startingDay = startingDate?.timestampToDay() ?: "0"
        val startingMonth = startingDate?.timestampToMonth() ?: "0"
        val startingYear = startingDate?.timestampToYear() ?: "0"
        val (dayOfMonth, dayOfWeek) = when (time?.id) {
            PeriodTimeEnum.DAY_AFTER_DAY.id -> "$startingDay/2" to "*"
            PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id -> "*" to (specificDays?.map { it.id }?.joinToString(separator = ",") { it.toString() } ?: "")
            else -> "$startingDay/1" to "*"
        }
        val month = "$startingMonth/1"
        val year = "$startingYear/1"

        return "$minutes $hours $dayOfMonth $month $dayOfWeek"
    }

    fun setMedicationTrack(medicationTrack: MedicationTrack) {
        medicationTrackField.set(medicationTrack)
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