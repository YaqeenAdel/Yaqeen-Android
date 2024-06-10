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
import com.cancer.yaqeen.domain.features.home.schedule.medication.EditLocalMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.EditMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetLocalMedicationUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.SaveLocalMedicationUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import com.cancer.yaqeen.presentation.util.calculateStartDateTime
import com.cancer.yaqeen.presentation.util.timestampToDay
import com.cancer.yaqeen.presentation.util.timestampToMonth
import com.cancer.yaqeen.presentation.util.timestampToYear
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MedicationsViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val addMedicationUseCase: AddMedicationUseCase,
    private val editMedicationUseCase: EditMedicationUseCase,
    private val getLocalMedicationUseCase: GetLocalMedicationUseCase,
    private val saveLocalMedicationUseCase: SaveLocalMedicationUseCase,
    private val editLocalMedicationUseCase: EditLocalMedicationUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateAddMedication = SingleLiveEvent<Pair<Boolean, MedicationDB>?>()
    val viewStateAddMedication: LiveData<Pair<Boolean, MedicationDB>?> = _viewStateAddMedication

    private val _viewStateEditMedication = SingleLiveEvent<Pair<Boolean, MedicationDB>?>()
    val viewStateEditMedication: LiveData<Pair<Boolean, MedicationDB>?> = _viewStateEditMedication

    private val _viewStateOldMedication = SingleLiveEvent<MedicationDB?>()
    val viewStateOldMedication: LiveData<MedicationDB?> = _viewStateOldMedication

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
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
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
                                        specificDays?.map { it.id },
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
        specificDaysIds: List<Int>?,
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
                startDateTime = calculateStartDateTime(startDate ?: 0L, reminderTime?.hour24?.toIntOrNull() ?: 0, reminderTime?.minute?.toIntOrNull() ?: 0),
//                startDate = startDate ?: 0L,
//                hour24 = reminderTime?.hour24?.toIntOrNull() ?: 0,
//                minute = reminderTime?.minute?.toIntOrNull() ?: 0,
//                isAM = reminderTime?.isAM ?: false,
//                time = reminderTime?.text.toString(),
                periodTimeId = periodTimeId,
                specificDaysIds = specificDaysIds ?: listOf()
            )
        }

    fun saveLocalMedication(medication: MedicationDB, uuid: String) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            saveLocalMedicationUseCase(
                medication.apply {
                    workID = uuid
                }
            ).collect()
        }
    }

    fun saveLocalMedication(medication: MedicationDB, uuids: List<String>) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            saveLocalMedicationUseCase(
                medication.apply {
                    workSpecificDaysIDs = uuids
                }
            ).collect()
        }
    }

    fun editMedication(){
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
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
                editMedicationUseCase(
                    scheduleId = medicationId ?: 0,
                    request = requestBuilder.buildRequestBody()
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            if (response.data == true) {
                                val medicationDB =
                                    createMedicationDB(
                                        requestBuilder,
                                        startDate,
                                        reminderTime,
                                        periodTime?.id,
                                        specificDays?.map { it.id },
                                        medicationId ?: 0
                                    )
                                getLocalMedication(medicationId ?: 0, medicationDB)
                                resetMedicationTrack()
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
            PeriodTimeEnum.EVERY_WEEK.id -> "$startingDay/7" to "*"
            // Every month and ignore different days of the month (28, 30, 31)
//            PeriodTimeEnum.EVERY_MONTH.id -> startingDay to "*"
            PeriodTimeEnum.EVERY_MONTH.id -> "$startingDay/30" to "*"
            PeriodTimeEnum.SPECIFIC_DAYS_OF_THE_WEEK.id -> "*" to (specificDays?.map { it.id }?.joinToString(separator = ",") { it.toString() } ?: "")
            else -> "$startingDay/1" to "*"
        }

        // Every month and ignore different days of the month (28, 30, 31)
//        val month = if(time?.id == PeriodTimeEnum.EVERY_MONTH.id) "*"
//            else "$startingMonth/1"

        val month = "$startingMonth/1"

        val year = "$startingYear/1"

        return "$minutes $hours $dayOfMonth $month $dayOfWeek"
    }

    private fun getLocalMedication(
        medicationId: Int,
        medicationDB: MedicationDB
    ) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            getLocalMedicationUseCase(
                medicationId = medicationId
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> {
                        _viewStateEditMedication.postValue(false to medicationDB.apply { isReminded = false })
                    }
                    Status.SUCCESS -> {
                        val workID = response.data?.workID
                        workID?.let {
                            _viewStateOldMedication.postValue(response.data)
                        }
                        _viewStateEditMedication.postValue((workID != null) to medicationDB.apply { isReminded = false })
                    }

                    else -> {}
                }
            }
        }
    }

    fun editLocalMedication(medication: MedicationDB, uuid: String) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            editLocalMedicationUseCase(
                medication.apply {
                    workID = uuid
                }
            ).collect()
        }
    }

    fun editLocalMedication(medication: MedicationDB, uuids: List<String>) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            editLocalMedicationUseCase(
                medication.apply {
                    workSpecificDaysIDs = uuids
                }
            ).collect()
        }
    }

    fun setMedicationTrack(medicationTrack: MedicationTrack) {
        medicationTrackField.set(medicationTrack)
    }


    fun userIsLoggedIn() =
        prefEncryptionUtil.isLogged

    fun hasWorker() =
        prefEncryptionUtil.hasWorker

    fun saveWorkerReminderPeriodicallyInfo(
        periodReminderId: String,
        workRunningInMilliSeconds: Long
    ) {
        with(prefEncryptionUtil){
            hasWorker = true
            workId = periodReminderId
            workRunningInMillis = workRunningInMilliSeconds
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