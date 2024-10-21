package com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder

import android.content.Context
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminderTrack
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.requests.AddMedicalReminderRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.AddMedicalReminderUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.DeleteSymptomFromScheduleUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.EditLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.EditMedicalReminderUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.GetLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.SaveLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.presentation.base.BaseViewModel
import com.cancer.yaqeen.presentation.ui.main.treatment.getReminderTimeFromTime
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import com.cancer.yaqeen.presentation.util.convertDateToMilliSeconds
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.DOCTOR_LOCATION
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.DOCTOR_NAME
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.DOCTOR_PHONE_NUMBER
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.MEDICAL_REMINDER
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.NOTES
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.REMINDER_TIME
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.REMINDER_TIME_BEFORE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.START_DATE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.SYMPTOM
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvent
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.CONFIRM_MEDICAL_REMINDER
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.MEDICAL_REMINDER_CONFIRMED
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.SET_DOCTOR_INFO
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class MedicalReminderViewModel @Inject constructor(
    @ApplicationContext val _context: Context,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val addMedicalReminderUseCase: AddMedicalReminderUseCase,
    private val editMedicalReminderUseCase: EditMedicalReminderUseCase,
    private val saveLocalMedicalAppointmentUseCase: SaveLocalMedicalAppointmentUseCase,
    private val editLocalMedicalAppointmentUseCase: EditLocalMedicalAppointmentUseCase,
    private val getLocalMedicalAppointmentUseCase: GetLocalMedicalAppointmentUseCase,
    private val deleteSymptomFromScheduleUseCase: DeleteSymptomFromScheduleUseCase,
) : BaseViewModel(context = _context, prefEncryptionUtil = prefEncryptionUtil) {

    private var viewModelJob: Job? = null

    private val _viewStateAddMedicalReminder =
        SingleLiveEvent<Pair<Boolean, MedicalAppointmentDB>?>()
    val viewStateAddMedicalReminder: LiveData<Pair<Boolean, MedicalAppointmentDB>?> =
        _viewStateAddMedicalReminder

    private val _viewStateEditMedicalReminder = SingleLiveEvent<Pair<Boolean, MedicalAppointmentDB>?>()
    val viewStateEditMedicalReminder: LiveData<Pair<Boolean, MedicalAppointmentDB>?> = _viewStateEditMedicalReminder

    private val _viewStateOldMedicalReminder = SingleLiveEvent<MedicalAppointmentDB?>()
    val viewStateOldMedicalReminder: LiveData<MedicalAppointmentDB?> = _viewStateOldMedicalReminder

    private val _viewStateDeleteSymptomFromSchedule = SingleLiveEvent<Boolean?>()
    val viewStateDeleteSymptomFromSchedule: LiveData<Boolean?> = _viewStateDeleteSymptomFromSchedule

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()

    private var medicalReminderTrackField: ObservableField<MedicalReminderTrack> = ObservableField(
        MedicalReminderTrack()
    )


    fun setMedicalReminderInfo(doctorName: String, location: String, phoneNumber: String) =
        medicalReminderTrackField.get()?.also {
            it.doctorName = doctorName
            it.location = location
            it.phoneNumber = phoneNumber
        }

    fun selectStartDate(startDate: Long) =
        medicalReminderTrackField.get()?.also {
            it.startDate = startDate
        }

    fun selectReminderTime(time: ReminderTime?) =
        medicalReminderTrackField.get()?.also {
            it.reminderTime = time
        }

    fun setNotes(notes: String) =
        medicalReminderTrackField.get()?.also {
            it.notes = notes
        }

    fun setSymptom(symptom: Symptom?) =
        medicalReminderTrackField.get()?.also {
            it.symptom = symptom
        }

    fun getMedicalReminderTrack(): MedicalReminderTrack? =
        medicalReminderTrackField.get()

    fun resetMedicalReminderTrack() =
        medicalReminderTrackField.set(MedicalReminderTrack())

    fun setMedicalReminderTrack(medicalReminderTrack: MedicalReminderTrack) =
        medicalReminderTrackField.set(medicalReminderTrack)


    fun modifyMedicalReminder() {
        if (medicalReminderTrackField.get()?.editable == true)
            editMedicalReminder()
        else
            addMedicalReminder()

    }

    fun increaseReminderBefore(): ReminderBefore? {
        medicalReminderTrackField.get()?.also { track ->
            ReminderBefore.getReminderBefore(track.reminderBefore.id + 1)?.let {
                track.reminderBefore = it
            }
        }

        return getMedicalReminderTrack()?.reminderBefore
    }

    fun decreaseReminderBefore(): ReminderBefore? {
        medicalReminderTrackField.get()?.also { track ->
            ReminderBefore.getReminderBefore(track.reminderBefore.id - 1)?.let {
                track.reminderBefore = it
            }
        }

        return getMedicalReminderTrack()?.reminderBefore
    }

    private fun addMedicalReminder() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val medicalReminderTrackField = getMedicalReminderTrack()
            logMedicalReminderEvent(medicalReminderTrackField)
            medicalReminderTrackField?.run {
                val requestBuilder = AddMedicalReminderRequestBuilder(
                    doctorName = doctorName ?: "",
                    location = location ?: "",
                    phoneNumber = phoneNumber ?: "",
                    startDate = convertMilliSecondsToDate(startDate ?: 0L),
                    time = reminderTime?.run { "${getAccurateHour24()}:$minute" },
                    notifyBeforeMinutes = reminderBefore.timeInMinutes,
                    notes = notes ?: "",
                )
                addMedicalReminderUseCase(
                    requestBuilder.buildRequestBody(),
                    symptom?.id
                ).collect { response ->
                    emitLoading(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                logEvent(
                                    GoogleAnalyticsEvent(
                                        eventName = MEDICAL_REMINDER_CONFIRMED,
                                    )
                                )
                                val medicalAppointmentDB =
                                    createMedicalAppointmentDB(
                                        requestBuilder,
                                        startDate,
                                        reminderTime,
                                        reminderBefore.timeInMinutes,
                                        it.scheduleID
                                    )
                                resetMedicalReminderTrack()
                                _viewStateAddMedicalReminder.postValue(true to medicalAppointmentDB)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun logMedicalReminderEvent(medicalReminderTrackField: MedicalReminderTrack?) {
        medicalReminderTrackField?.run {
            logEvent(
                GoogleAnalyticsEvent(
                    eventName = CONFIRM_MEDICAL_REMINDER,
                    eventParams = arrayOf(
                        DOCTOR_NAME to doctorName.toString(),
                        DOCTOR_LOCATION to location.toString(),
                        DOCTOR_PHONE_NUMBER to phoneNumber.toString(),
                        START_DATE to startDate?.let { convertMilliSecondsToDate(startDate!!) }.toString(),
                        REMINDER_TIME to reminderTime?.run { "${hour24}:${minute}" }.toString(),
                        REMINDER_TIME_BEFORE to reminderBefore.toString(),
                        NOTES to notes.toString(),
                        SYMPTOM to symptom?.id.toString(),
                    )
                )
            )
        }
    }

    private fun createMedicalAppointmentDB(
        builder: AddMedicalReminderRequestBuilder,
        startDate: Long?,
        reminderTime: ReminderTime?,
        reminderBeforeInMinutes: Int,
        medicalReminderId: Int,
    ): MedicalAppointmentDB =
        builder.run {
            MedicalAppointmentDB(
                medicalAppointmentId = medicalReminderId,
                doctorName = doctorName,
                location = location,
                phoneNumber = phoneNumber,
                notes = notes,
                scheduleType = ScheduleType.MEDICAL_REMINDER.scheduleType,
                startDate = startDate ?: 0L,
                hour24 = reminderTime?.hour24?.toIntOrNull() ?: 0,
                minute = reminderTime?.minute?.toIntOrNull() ?: 0,
                isAM = reminderTime?.isAM ?: false,
                time = reminderTime?.text.toString(),
                reminderBeforeInMinutes = reminderBeforeInMinutes
            )
        }

    private fun editMedicalReminder() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val medicalReminderTrackField = getMedicalReminderTrack()
            logMedicalReminderEvent(medicalReminderTrackField)
            medicalReminderTrackField?.run {
                val requestBuilder = AddMedicalReminderRequestBuilder(
                    doctorName = doctorName ?: "",
                    location = location ?: "",
                    phoneNumber = phoneNumber ?: "",
                    startDate = convertMilliSecondsToDate(startDate ?: 0L),
                    time = reminderTime?.run { "${getAccurateHour24()}:$minute" },
                    notifyBeforeMinutes = reminderBefore.timeInMinutes,
                    notes = notes ?: "",
                )
                editMedicalReminderUseCase(
                    medicalReminderId ?: 0,
                    requestBuilder.buildRequestBody(),

                    if (symptom?.id == oldSymptomId) null else symptom?.id
                ).collect { response ->
                    emitLoading(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                logEvent(
                                    GoogleAnalyticsEvent(
                                        eventName = MEDICAL_REMINDER_CONFIRMED,
                                    )
                                )
                                val medicalAppointmentDB =
                                    createMedicalAppointmentDB(
                                        requestBuilder,
                                        startDate,
                                        reminderTime,
                                        reminderBefore.timeInMinutes,
                                        medicalReminderId ?: 0
                                    )
                                getLocalMedicalReminder(it.scheduleID, medicalAppointmentDB)
                                resetMedicalReminderTrack()
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun getLocalMedicalReminder(
        medicalAppointmentId: Int,
        medicalAppointmentDB: MedicalAppointmentDB
    ) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            getLocalMedicalAppointmentUseCase(
                medicalAppointmentId = medicalAppointmentId
            ).collect { response ->
                emitLoading(response.loading)
                when (response.status) {
                    Status.ERROR -> {
                        _viewStateEditMedicalReminder.postValue(false to medicalAppointmentDB)
                    }
                    Status.SUCCESS -> {
                        val workID = response.data?.workID
                        workID?.let {
                            _viewStateOldMedicalReminder.postValue(response.data)
                        }
                        _viewStateEditMedicalReminder.postValue((workID != null) to medicalAppointmentDB)
                    }

                    else -> {}
                }
            }
        }
    }

    fun saveLocalMedicalAppointment(
        medicalAppointment: MedicalAppointmentDB,
        workID: String,
        workBeforeID: String?
    ) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            saveLocalMedicalAppointmentUseCase(
                medicalAppointment.apply {
                    this.workID = workID
                    this.workBeforeID = workBeforeID
                }
            ).collect()
        }
    }

    fun editLocalMedicalAppointment(
        medicalAppointment: MedicalAppointmentDB,
        workID: String,
        workBeforeID: String?
    ) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            editLocalMedicalAppointmentUseCase(
                medicalAppointment.apply {
                    this.workID = workID
                    this.workBeforeID = workBeforeID
                }
            ).collect()
        }
    }

    fun deleteSymptomFromSchedule() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            medicalReminderTrackField.get()?.let { medicalReminder ->
                if (medicalReminder.editable && medicalReminder.symptom?.id == medicalReminder.oldSymptomId){
                    deleteSymptomFromScheduleUseCase(
                        scheduleId = medicalReminder.medicalReminderId ?: 0,
                        symptomId = medicalReminder.symptom?.id ?: 0
                    ).collect { response ->
                        emitLoading(response.loading)
                        when (response.status) {
                            Status.ERROR -> emitError(response.errorEntity)
                            Status.SUCCESS -> {
                                setSymptom(null)
                                _viewStateDeleteSymptomFromSchedule.postValue(response.data)
                            }
                            else -> {}
                        }
                    }
                } else {
                    setSymptom(null)
                    _viewStateDeleteSymptomFromSchedule.postValue(true)
                }

            }

        }
    }

    fun userIsLoggedIn() =
        prefEncryptionUtil.isLogged

    private suspend fun emitLoading(isLoading: Boolean) {
        withContext(Dispatchers.Main) {
            _viewStateLoading.emit(isLoading)
        }
    }

    private suspend fun emitError(errorEntity: ErrorEntity?) {
        withContext(Dispatchers.Main) {
            _viewStateError.emit(errorEntity)
            _viewStateError.emit(null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob = null
    }

}