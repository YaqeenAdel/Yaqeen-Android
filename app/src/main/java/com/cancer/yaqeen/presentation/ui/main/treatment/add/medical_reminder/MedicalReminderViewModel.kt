package com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder

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
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.AddMedicalReminderUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.EditLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.EditMedicalReminderUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.GetLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.SaveLocalMedicalAppointmentUseCase
import com.cancer.yaqeen.presentation.ui.main.treatment.getReminderTimeFromTime
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import com.cancer.yaqeen.presentation.util.convertDateToMilliSeconds
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class MedicalReminderViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val addMedicalReminderUseCase: AddMedicalReminderUseCase,
    private val editMedicalReminderUseCase: EditMedicalReminderUseCase,
    private val saveLocalMedicalAppointmentUseCase: SaveLocalMedicalAppointmentUseCase,
    private val editLocalMedicalAppointmentUseCase: EditLocalMedicalAppointmentUseCase,
    private val getLocalMedicalAppointmentUseCase: GetLocalMedicalAppointmentUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateAddMedicalReminder =
        SingleLiveEvent<Pair<Boolean, MedicalAppointmentDB>?>()
    val viewStateAddMedicalReminder: LiveData<Pair<Boolean, MedicalAppointmentDB>?> =
        _viewStateAddMedicalReminder

    private val _viewStateEditMedicalReminder = SingleLiveEvent<Pair<Boolean, MedicalAppointmentDB>?>()
    val viewStateEditMedicalReminder: LiveData<Pair<Boolean, MedicalAppointmentDB>?> = _viewStateEditMedicalReminder

    private val _viewStateWorkIds = SingleLiveEvent<Pair<UUID, UUID?>?>()
    val viewStateWorkIds: LiveData<Pair<UUID, UUID?>?> = _viewStateWorkIds

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

    fun selectStartDate(startDate: String) =
        medicalReminderTrackField.get()?.also {
            it.startDate = startDate
        }

    fun selectReminderTime(time: String?) =
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
        viewModelJob = viewModelScope.launch {
            val medicalReminderTrackField = getMedicalReminderTrack()
            medicalReminderTrackField?.run {
                val requestBuilder = AddMedicalReminderRequestBuilder(
                    doctorName = doctorName ?: "",
                    location = location ?: "",
                    phoneNumber = phoneNumber ?: "",
                    startDate = startDate,
                    time = reminderTime,
                    notifyBeforeMinutes = reminderBefore.timeInMinutes,
                    notes = notes ?: "",
                )
                addMedicalReminderUseCase(
                    requestBuilder.buildRequestBody(),
                    symptom?.id
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                val medicalAppointmentDB =
                                    createMedicalAppointmentDB(
                                        requestBuilder,
                                        convertDateToMilliSeconds(startDate ?: ""),
                                        getReminderTimeFromTime(reminderTime ?: ""),
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
        viewModelJob = viewModelScope.launch {
            val medicalReminderTrackField = getMedicalReminderTrack()
            medicalReminderTrackField?.run {
                val requestBuilder = AddMedicalReminderRequestBuilder(
                    doctorName = doctorName ?: "",
                    location = location ?: "",
                    phoneNumber = phoneNumber ?: "",
                    startDate = startDate,
                    time = reminderTime,
                    notifyBeforeMinutes = reminderBefore.timeInMinutes,
                    notes = notes ?: "",
                )
                editMedicalReminderUseCase(
                    medicalReminderId ?: 0,
                    requestBuilder.buildRequestBody(),
                    symptom?.id
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                val medicalAppointmentDB =
                                    createMedicalAppointmentDB(
                                        requestBuilder,
                                        convertDateToMilliSeconds(startDate ?: ""),
                                        getReminderTimeFromTime(reminderTime ?: ""),
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
        viewModelJob = viewModelScope.launch {
            getLocalMedicalAppointmentUseCase(
                medicalAppointmentId = medicalAppointmentId
            ).collect { response ->
                when (response.status) {
                    Status.ERROR -> {
                        _viewStateEditMedicalReminder.postValue(true to medicalAppointmentDB)
                    }
                    Status.SUCCESS -> {
                        response.data?.workID?.let {
                            _viewStateEditMedicalReminder.postValue(true to medicalAppointmentDB)
                            _viewStateWorkIds.postValue(it to response.data.workBeforeID)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun saveLocalMedicalAppointment(
        medicalAppointment: MedicalAppointmentDB,
        workID: UUID,
        workBeforeID: UUID?
    ) {
        viewModelJob = viewModelScope.launch {
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
        workID: UUID,
        workBeforeID: UUID?
    ) {
        viewModelJob = viewModelScope.launch {
            editLocalMedicalAppointmentUseCase(
                medicalAppointment.apply {
                    this.workID = workID
                    this.workBeforeID = workBeforeID
                }
            ).collect()
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