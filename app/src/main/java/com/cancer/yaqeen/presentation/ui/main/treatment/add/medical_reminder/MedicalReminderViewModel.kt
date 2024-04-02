package com.cancer.yaqeen.presentation.ui.main.treatment.add.medical_reminder

import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminderTrack
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.requests.AddMedicalReminderRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.AddMedicalReminderUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medical_reminder.EditMedicalReminderUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class MedicalReminderViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val addMedicalReminderUseCase: AddMedicalReminderUseCase,
    private val editMedicalReminderUseCase: EditMedicalReminderUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateAddMedicalReminder = SingleLiveEvent<Boolean?>()
    val viewStateAddMedicalReminder: LiveData<Boolean?> = _viewStateAddMedicalReminder

    private val _viewStateEditMedicalReminder = SingleLiveEvent<Boolean?>()
    val viewStateEditMedicalReminder: LiveData<Boolean?> = _viewStateEditMedicalReminder

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
                addMedicalReminderUseCase(
                    AddMedicalReminderRequestBuilder(
                        doctorName = doctorName ?: "",
                        location = location ?: "",
                        phoneNumber = phoneNumber ?: "",
                        startDate = startDate,
                        time = reminderTime,
                        notifyBeforeMinutes = reminderBefore.timeInMinutes,
                        notes = notes ?: "",
                    ).buildRequestBody(),
                    symptom?.id
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                resetMedicalReminderTrack()
                                _viewStateAddMedicalReminder.postValue(true)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    private fun editMedicalReminder() {
        viewModelJob = viewModelScope.launch {
            val medicalReminderTrackField = getMedicalReminderTrack()
            medicalReminderTrackField?.run {
                editMedicalReminderUseCase(
                    medicalReminderId ?: 0,
                    AddMedicalReminderRequestBuilder(
                        doctorName = doctorName ?: "",
                        location = location ?: "",
                        phoneNumber = phoneNumber ?: "",
                        startDate = startDate,
                        time = reminderTime,
                        notifyBeforeMinutes = reminderBefore.timeInMinutes,
                        notes = notes ?: "",
                    ).buildRequestBody(),
                    symptom?.id
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                resetMedicalReminderTrack()
                                _viewStateEditMedicalReminder.postValue(true)
                            }
                        }

                        else -> {}
                    }
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