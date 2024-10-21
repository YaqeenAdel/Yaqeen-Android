package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms

import android.content.Context
import android.net.Uri
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime2
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomTrack
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequestBuilder
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.toJson
import com.cancer.yaqeen.domain.features.home.schedule.symptom.AddSymptomUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.AddSymptomWithoutPhotoUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.EditSymptomUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.EditSymptomWithoutUploadUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.GetSymptomsTypesUseCase
import com.cancer.yaqeen.presentation.base.BaseViewModel
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import com.cancer.yaqeen.presentation.util.generateFileName
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.DAYS
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.DETAILS
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.DOCTOR_NAME
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.PHOTO_ATTACHED
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.REMINDER_TIME
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.START_DATE
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.SYMPTOM
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsAttributes.SYMPTOMS_TYPES
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvent
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.CONFIRM_SYMPTOM
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.ROUTINE_TEST_CONFIRMED
import com.cancer.yaqeen.presentation.util.google_analytics.GoogleAnalyticsEvents.SYMPTOM_CONFIRMED
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SymptomsViewModel @Inject constructor(
    @ApplicationContext val _context: Context,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val getSymptomsTypesUseCase: GetSymptomsTypesUseCase,
    private val addSymptomUseCase: AddSymptomUseCase,
    private val addSymptomWithoutPhotoUseCase: AddSymptomWithoutPhotoUseCase,
    private val editSymptomWithoutUploadUseCase: EditSymptomWithoutUploadUseCase,
    private val editSymptomUseCase: EditSymptomUseCase,
) : BaseViewModel(context = _context, prefEncryptionUtil = prefEncryptionUtil) {

    private var viewModelJob: Job? = null


    private val _viewStateSymptomsTypes = MutableStateFlow<List<SymptomType>>(listOf())
    val viewStateSymptomsTypes = _viewStateSymptomsTypes.asStateFlow()

    private val _viewStateAddSymptom = SingleLiveEvent<Pair<Boolean, Int?>?>()
    val viewStateAddSymptom: LiveData<Pair<Boolean, Int?>?> = _viewStateAddSymptom

    private val _viewStateEditSymptom = SingleLiveEvent<Pair<Boolean, Int?>?>()
    val viewStateEditSymptom: LiveData<Pair<Boolean, Int?>?> = _viewStateEditSymptom

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()

    private var symptomTrackField: ObservableField<SymptomTrack> = ObservableField(
        SymptomTrack()
    )


    fun getSymptomsTypes(){
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            getSymptomsTypesUseCase().collect { response ->
                emitLoading(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            val symptomTypes = symptomTrackField.get()?.symptomTypes
                            symptomTypes?.forEach { type ->
                                it.firstOrNull {
                                    it.id == type.id
                                }?.selected = true
                            }

                            _viewStateSymptomsTypes.emit(it)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun resetSymptomsTypes(){
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            _viewStateSymptomsTypes.emit(listOf())
        }
    }


    fun addSymptom() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val symptomTrackField = getSymptomTrack()
            val isReadyToUploading = symptomTrackField?.photosList?.any { it.uri != null } ?: false
            if (isReadyToUploading)
                addSymptomWithPhoto()
            else
                addSymptomWithoutPhoto()
        }
    }
    private fun addSymptomWithPhoto() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val symptomTrackField = getSymptomTrack()
            logSymptomEvent(symptomTrackField)
            symptomTrackField?.run {
                val destinationId = this.destinationId
                addSymptomUseCase(
                    AddSymptomRequestBuilder(
                        details = details ?: "",
                        symptomLookupIds = symptomTypes?.map { it.id } ?: listOf(),
                        doctorName = doctorName ?: "",
                        startDate = startDateEn,
                        time = reminderTime2?.timeEN,
                        photos = photosList ?: listOf(),
                    )
                ).onEach { response ->
                    emitLoading(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                logEvent(
                                    GoogleAnalyticsEvent(
                                        eventName = SYMPTOM_CONFIRMED,
                                    )
                                )
                                if (it.scheduleIsModified) {
                                    resetSymptomTrack()
                                    _viewStateAddSymptom.postValue(true to destinationId)
                                }
                            }
                        }

                        else -> {}
                    }
                }.catch {
                    _viewStateLoading.emit(false)
                    emitError(ErrorEntity.ApiError.Network)
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun logSymptomEvent(symptomTrackField: SymptomTrack?) {

        symptomTrackField?.run {
            logEvent(
                GoogleAnalyticsEvent(
                    eventName = CONFIRM_SYMPTOM,
                    eventParams = arrayOf(
                        SYMPTOMS_TYPES to (symptomTypes?.map { it.name }?.joinToString(separator = ",") { it } ?: ""),
                        DETAILS to details.toString(),
                        PHOTO_ATTACHED to (photosList?.isNotEmpty() == true).toString(),
                        REMINDER_TIME to reminderTime2?.timeEN.toString(),
                        START_DATE to startDateEn.toString(),
                        DOCTOR_NAME to doctorName.toString(),
                    )
                )
            )
        }
    }

    private fun addSymptomWithoutPhoto() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val symptomTrackField = getSymptomTrack()
            logSymptomEvent(symptomTrackField)
            symptomTrackField?.run {
                val destinationId = this.destinationId
                addSymptomWithoutPhotoUseCase(
                    AddSymptomRequestBuilder(
                        details = details ?: "",
                        symptomLookupIds = symptomTypes?.map { it.id } ?: listOf(),
                        doctorName = doctorName ?: "",
                        startDate = startDateEn,
                        time = reminderTime2?.timeEN,
                        photos = listOf(),
                    ).buildRequestBody()
                ).collect { response ->
                    emitLoading(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                logEvent(
                                    GoogleAnalyticsEvent(
                                        eventName = SYMPTOM_CONFIRMED,
                                    )
                                )
                                resetSymptomTrack()
                                _viewStateAddSymptom.postValue(true to destinationId)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun editSymptom() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val symptomTrackField = getSymptomTrack()
            val isReadyToUploading = symptomTrackField?.photosList?.any { it.uri != null } ?: false
            if (isReadyToUploading)
                editSymptomWithUpload()
            else
                editSymptomWithoutUpload()
        }
    }

    private fun editSymptomWithoutUpload() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val symptomTrackField = getSymptomTrack()
            logSymptomEvent(symptomTrackField)
            symptomTrackField?.run {
                val destinationId = this.destinationId
                editSymptomWithoutUploadUseCase(
                    symptomId = symptomId ?: 0,
                    AddSymptomRequestBuilder(
                        details = details ?: "",
                        symptomLookupIds = symptomTypes?.map { it.id } ?: listOf(),
                        doctorName = doctorName ?: "",
                        startDate = startDateEn,
                        time = reminderTime2?.timeEN,
                        photos = photosList ?: listOf(),
                    ).buildRequestBody()
                ).onEach { response ->
                    emitLoading(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            if (response.data == true) {
                                logEvent(
                                    GoogleAnalyticsEvent(
                                        eventName = SYMPTOM_CONFIRMED,
                                    )
                                )
                                resetSymptomTrack()
                                _viewStateEditSymptom.postValue(true to destinationId)
                            }
                        }

                        else -> {}
                    }
                }.catch {
                    _viewStateLoading.emit(false)
                    emitError(ErrorEntity.ApiError.Network)
                }.launchIn(viewModelScope)
            }
        }
    }

    private fun editSymptomWithUpload() {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            val symptomTrackField = getSymptomTrack()
            logSymptomEvent(symptomTrackField)
            symptomTrackField?.run {
                val destinationId = this.destinationId
                editSymptomUseCase(
                    symptomId = symptomId ?: 0,
                    AddSymptomRequestBuilder(
                        details = details ?: "",
                        symptomLookupIds = symptomTypes?.map { it.id } ?: listOf(),
                        doctorName = doctorName ?: "",
                        startDate = startDateEn,
                        time = reminderTime2?.timeEN,
                        photos = photosList ?: listOf(),
                    )
                ).collect { response ->
                    emitLoading(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                logEvent(
                                    GoogleAnalyticsEvent(
                                        eventName = SYMPTOM_CONFIRMED,
                                    )
                                )
                                resetSymptomTrack()
                                _viewStateEditSymptom.postValue(true to destinationId)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun selectSymptomTypes(symptomTypes: List<SymptomType>) =
        symptomTrackField.get()?.also {
            it.symptomTypes = symptomTypes
        }

    fun getSymptomTrack(): SymptomTrack? =
        symptomTrackField.get()

    fun resetSymptomTrack(){
        symptomTrackField.set(SymptomTrack())
        resetSymptomsTypes()
    }

    fun setDestinationId(destinationId: Int) {
        symptomTrackField.get()?.also {
            it.destinationId = destinationId
        }
    }

    fun setSymptomTrack(symptomTrack: SymptomTrack) =
        symptomTrackField.set(symptomTrack)

    fun userIsLoggedIn() =
        prefEncryptionUtil.isLogged

    fun setSymptomData(details: String) =
        symptomTrackField.get()?.also {
            it.details = details
        }

    fun deleteSymptomPhoto(photo: Photo) =
        symptomTrackField.get()?.also {
            it.photosList?.remove(photo)
        }

    fun addSymptomPhoto(photo: Photo) =
        symptomTrackField.get()?.also {
            it.photosList?.add(photo)
        }

    fun addSymptomPhotos(photos: List<Photo>) =
        symptomTrackField.get()?.also {
            it.photosList?.addAll(photos)
        }

    fun createPhoto(uri: Uri): Photo {
        val timeMillis = getCurrentTimeMillis()
        val fileName = generateFileName()
        return Photo(
            id = timeMillis,
            uri = uri,
            imageName = fileName
        )
    }

    fun createPhotos(uris: List<Uri>): List<Photo> {
        var timeMillis = 0L
        var fileName = ""

        val photos = uris.map { uri ->
            timeMillis = getCurrentTimeMillis()
            fileName = generateFileName()
            Photo(
                id = timeMillis,
                uri = uri,
                imageName = fileName
            )
        }

        return photos
    }

    fun getSymptomPhotos(): List<Photo>? =
        symptomTrackField.get()?.photosList

    fun selectStartDate(startDateUI: String, startDateEn: String) =
        symptomTrackField.get()?.also {
            it.startDateUI = startDateUI
            it.startDateEn = startDateEn
        }

    fun selectReminderTime(reminderTime: ReminderTime2?) =
        symptomTrackField.get()?.also {
            it.reminderTime2 = reminderTime
        }

    fun setDoctorName(doctorName: String) =
        symptomTrackField.get()?.also {
            it.doctorName = doctorName
        }
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