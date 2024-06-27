package com.cancer.yaqeen.presentation.ui.main.treatment.add.routine_test

import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Day
import com.cancer.yaqeen.data.features.home.schedule.medication.models.PeriodTimeEnum
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore.Companion.getReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTestTrack
import com.cancer.yaqeen.data.features.home.schedule.routine_test.requests.AddRoutineTestRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.AddRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.AddRoutineTestWithoutPhotoUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.EditLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.EditRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.EditRoutineTestWithoutPhotoUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.GetLocalRoutineTestUseCase
import com.cancer.yaqeen.domain.features.home.schedule.routine_test.SaveLocalRoutineTestUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import com.cancer.yaqeen.presentation.util.calculateStartDateTime
import com.cancer.yaqeen.presentation.util.generateFileName
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis
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
class RoutineTestViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val addRoutineTestUseCase: AddRoutineTestUseCase,
    private val addRoutineTestWithoutPhotoUseCase: AddRoutineTestWithoutPhotoUseCase,
    private val editRoutineTestUseCase: EditRoutineTestUseCase,
    private val editRoutineTestWithoutPhotoUseCase: EditRoutineTestWithoutPhotoUseCase,
    private val saveLocalRoutineTestUseCase: SaveLocalRoutineTestUseCase,
    private val editLocalRoutineTestUseCase: EditLocalRoutineTestUseCase,
    private val getLocalRoutineTestUseCase: GetLocalRoutineTestUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateAddRoutineTest = SingleLiveEvent<Pair<Boolean, RoutineTestDB>?>()
    val viewStateAddRoutineTest: LiveData<Pair<Boolean, RoutineTestDB>?> = _viewStateAddRoutineTest

    private val _viewStateEditRoutineTest = SingleLiveEvent<Pair<Boolean, RoutineTestDB>?>()
    val viewStateEditRoutineTest: LiveData<Pair<Boolean, RoutineTestDB>?> = _viewStateEditRoutineTest

    private val _viewStateOldRoutineTest = SingleLiveEvent<RoutineTestDB?>()
    val viewStateOldRoutineTest: LiveData<RoutineTestDB?> = _viewStateOldRoutineTest

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()

    private var routineTestTrackField: ObservableField<RoutineTestTrack> = ObservableField(
        RoutineTestTrack()
    )

    fun createPhoto(uri: Uri): Photo {
        val timeMillis = getCurrentTimeMillis()
        val fileName = generateFileName()
        return Photo(
            id = timeMillis,
            uri = uri,
            imageName = fileName
        )
    }

    fun deleteRoutinePhoto() =
        routineTestTrackField.get()?.also {
            it.photo = null
        }

    fun addRoutinePhoto(photo: Photo) =
        routineTestTrackField.get()?.also {
            it.photo = photo
        }

    fun getRoutinePhoto():Photo? =
        routineTestTrackField.get()?.photo

    fun setRoutineTestInfo(routineTestName: String, periodTime: Time, specificDays: List<Day>) =
        routineTestTrackField.get()?.also {
            it.routineTestName = routineTestName
            it.periodTime = periodTime
            it.specificDays = specificDays
        }

    fun selectSpecificDays(specificDays: List<Day>) =
        routineTestTrackField.get()?.also {
            it.specificDays = specificDays
        }

    fun selectStartDate(startDate: Long?) =
        routineTestTrackField.get()?.also {
            it.startDate = startDate
        }

    fun selectReminderTime(reminderTime: ReminderTime?) =
        routineTestTrackField.get()?.also {
            it.reminderTime = reminderTime
        }

    fun selectNotes(notes: String) =
        routineTestTrackField.get()?.also {
            it.notes = notes
        }

    fun getRoutineTestTrack(): RoutineTestTrack? =
        routineTestTrackField.get()

    fun resetRoutineTestTrack() {
        routineTestTrackField.set(RoutineTestTrack())
    }

    fun modifyRoutineTest() {
        val routineTestTrack = getRoutineTestTrack()
        if (routineTestTrack?.editable == true)
            editRoutineTest()
        else
            addRoutineTest()
    }

    private fun addRoutineTest() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val routineTestTrackField = getRoutineTestTrack()
            val isReadyToUploading = (routineTestTrackField?.photo  != null) && (routineTestTrackField.photo?.uri != null)
            if (isReadyToUploading)
                addRoutineTestWithPhoto()
            else
                addRoutineTestWithoutPhoto()
        }
    }

    private fun editRoutineTest() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val routineTestTrackField = getRoutineTestTrack()
            val isReadyToUploading = (routineTestTrackField?.photo  != null) && (routineTestTrackField.photo?.uri != null)
            if (isReadyToUploading)
                editRoutineTestWithPhoto()
            else
                editRoutineTestWithoutPhoto()
        }
    }

    private fun addRoutineTestWithPhoto() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val routineTestTrackField = getRoutineTestTrack()
            routineTestTrackField?.run {
                val requestBuilder = AddRoutineTestRequestBuilder(
                    routineTestName = routineTestName ?: "",
                    notifyBeforeMinutes = reminderBefore.timeInMinutes,
                    photo = photo,
                    cronExpression = createCronExpression(
                        minutes = reminderTime?.minute ?: "0",
                        startingHour = reminderTime?.hour24 ?: "0",
                        time = periodTime,
                        startingDate = startDate,
                        specificDays = specificDays
                    ),
                    notes = notes ?: ""
                )
                addRoutineTestUseCase(
                    requestBuilder
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                if (it.scheduleIsModified) {
                                    val routineTestDB =
                                        createRoutineTestDB(
                                            requestBuilder,
                                            startDate,
                                            reminderTime,
                                            periodTime?.id,
                                            reminderBefore.timeInMinutes,
                                            specificDays?.map { it.id },
                                            it.routineTestId ?: 0
                                        )
                                    resetRoutineTestTrack()
                                    _viewStateAddRoutineTest.postValue(true to routineTestDB)
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun createRoutineTestDB(
        builder: AddRoutineTestRequestBuilder,
        startDate: Long?,
        reminderTime: ReminderTime?,
        periodTimeId: Int?,
        reminderBeforeInMinutes: Int,
        specificDaysIds: List<Int>?,
        routineTestId: Int,
    ): RoutineTestDB =
        builder.run {
            RoutineTestDB(
                routineTestId = routineTestId,
                routineTestName = routineTestName,
                notes = notes,
                scheduleType = ScheduleType.ROUTINE_TESTS.scheduleType,
                cronExpression = cronExpression,
                startDateTime = calculateStartDateTime(startDate ?: 0L, reminderTime?.hour24?.toIntOrNull() ?: 0, reminderTime?.minute?.toIntOrNull() ?: 0),
                periodTimeId = periodTimeId,
                reminderBeforeInMinutes = reminderBeforeInMinutes,
                specificDaysIds = specificDaysIds ?: listOf()
            )
        }

    private fun editRoutineTestWithPhoto() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val routineTestTrackField = getRoutineTestTrack()
            routineTestTrackField?.run {
                val requestBuilder = AddRoutineTestRequestBuilder(
                    routineTestName = routineTestName ?: "",
                    notifyBeforeMinutes = reminderBefore.timeInMinutes,
                    photo = photo,
                    cronExpression = createCronExpression(
                        minutes = reminderTime?.minute ?: "0",
                        startingHour = reminderTime?.hour24 ?: "0",
                        time = periodTime,
                        startingDate = startDate,
                        specificDays = specificDays
                    ),
                    notes = notes ?: ""
                )
                editRoutineTestUseCase(
                    routineTestId ?: 0,
                    requestBuilder
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                if (it.scheduleIsModified) {
                                    val routineTestDB =
                                        createRoutineTestDB(
                                            requestBuilder,
                                            startDate,
                                            reminderTime,
                                            periodTime?.id,
                                            reminderBefore.timeInMinutes,
                                            specificDays?.map { it.id },
                                            it.routineTestId ?: 0
                                        )
                                    getLocalRoutineTest(it.routineTestId ?: 0, routineTestDB)
                                    resetRoutineTestTrack()
                                }
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun addRoutineTestWithoutPhoto() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val routineTestTrackField = getRoutineTestTrack()
            routineTestTrackField?.run {
                val requestBuilder = AddRoutineTestRequestBuilder(
                    routineTestName = routineTestName ?: "",
                    notifyBeforeMinutes = reminderBefore.timeInMinutes,
                    cronExpression = createCronExpression(
                        minutes = reminderTime?.minute ?: "0",
                        startingHour = reminderTime?.hour24 ?: "0",
                        time = periodTime,
                        startingDate = startDate,
                        specificDays = specificDays
                    ),
                    notes = notes ?: ""
                )
                addRoutineTestWithoutPhotoUseCase(
                    requestBuilder.buildRequestBody()
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                val routineTestDB =
                                    createRoutineTestDB(
                                        requestBuilder,
                                        startDate,
                                        reminderTime,
                                        periodTime?.id,
                                        reminderBefore.timeInMinutes,
                                        specificDays?.map { it.id },
                                        it
                                    )
                                resetRoutineTestTrack()
                                _viewStateAddRoutineTest.postValue(true to routineTestDB)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun editRoutineTestWithoutPhoto() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val routineTestTrackField = getRoutineTestTrack()
            routineTestTrackField?.run {
                val requestBuilder = AddRoutineTestRequestBuilder(
                    routineTestName = routineTestName ?: "",
                    notifyBeforeMinutes = reminderBefore.timeInMinutes,
                    photo = photo,
                    cronExpression = createCronExpression(
                        minutes = reminderTime?.minute ?: "0",
                        startingHour = reminderTime?.hour24 ?: "0",
                        time = periodTime,
                        startingDate = startDate,
                        specificDays = specificDays
                    ),
                    notes = notes ?: ""
                )
                editRoutineTestWithoutPhotoUseCase(
                    routineTestId ?: 0,
                    requestBuilder.buildRequestBody()
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            if (response.data == true) {
                                val routineTestDB =
                                    createRoutineTestDB(
                                        requestBuilder,
                                        startDate,
                                        reminderTime,
                                        periodTime?.id,
                                        reminderBefore.timeInMinutes,
                                        specificDays?.map { it.id },
                                        routineTestId ?: 0
                                    )
                                getLocalRoutineTest(routineTestId ?: 0, routineTestDB)
                                resetRoutineTestTrack()
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }


    private fun getLocalRoutineTest(routineTestId: Int, routineTestDB: RoutineTestDB) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            getLocalRoutineTestUseCase(
                routineTestId = routineTestId
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> {
                        _viewStateEditRoutineTest.postValue(false to routineTestDB)
                    }
                    Status.SUCCESS -> {
                        val workID = response.data?.workID
                        workID?.let {
                            _viewStateOldRoutineTest.postValue(response.data)
                        }
                        _viewStateEditRoutineTest.postValue((workID != null) to routineTestDB)
                    }
                    else -> {}
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

    fun setRoutineTestTrack(routineTestTrack: RoutineTestTrack) {
        routineTestTrackField.set(routineTestTrack)
    }

    fun increaseReminderBefore(): ReminderBefore? {
        routineTestTrackField.get()?.also { track ->
            getReminderBefore(track.reminderBefore.id + 1)?.let {
                track.reminderBefore = it
            }
        }

        return getRoutineTestTrack()?.reminderBefore
    }

    fun decreaseReminderBefore(): ReminderBefore? {
        routineTestTrackField.get()?.also { track ->
            getReminderBefore(track.reminderBefore.id - 1)?.let {
                track.reminderBefore = it
            }
        }

        return getRoutineTestTrack()?.reminderBefore
    }

    fun saveLocalRoutineTest(routineTest: RoutineTestDB, periodicWorkID: String, workBeforeID: String?) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            saveLocalRoutineTestUseCase(
                routineTest.apply {
                    workID = periodicWorkID
                    this.workBeforeID = workBeforeID
                }
            ).collect()
        }
    }

    fun editLocalRoutineTest(routineTest: RoutineTestDB, periodicWorkID: String, workBeforeID: String?) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            editLocalRoutineTestUseCase(
                routineTest.apply {
                    workID = periodicWorkID
                    this.workBeforeID = workBeforeID
                }
            ).collect()
        }
    }

    fun saveLocalRoutineTest(routineTest: RoutineTestDB, uuids: List<String>, workBeforeID: String?) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            saveLocalRoutineTestUseCase(
                routineTest.apply {
                    workSpecificDaysIDs = uuids
                    this.workBeforeID = workBeforeID
                }
            ).collect()
        }
    }

    fun editLocalRoutineTest(routineTest: RoutineTestDB, uuids: List<String>, workBeforeID: String?) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            editLocalRoutineTestUseCase(
                routineTest.apply {
                    workSpecificDaysIDs = uuids
                    this.workBeforeID = workBeforeID
                }
            ).collect()
        }
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