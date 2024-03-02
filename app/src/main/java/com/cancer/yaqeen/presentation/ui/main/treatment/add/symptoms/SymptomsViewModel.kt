package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms

import android.net.Uri
import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomTrack
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequestBuilder
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.data.utils.formatDateAPI
import com.cancer.yaqeen.data.utils.formatTimeAPI
import com.cancer.yaqeen.domain.features.home.schedule.symptom.AddSymptomUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.AddSymptomWithoutPhotoUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.EditSymptomUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.EditSymptomWithoutUploadUseCase
import com.cancer.yaqeen.domain.features.home.schedule.symptom.GetSymptomsTypesUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import com.cancer.yaqeen.presentation.util.generateFileName
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SymptomsViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val getSymptomsTypesUseCase: GetSymptomsTypesUseCase,
    private val addSymptomUseCase: AddSymptomUseCase,
    private val addSymptomWithoutPhotoUseCase: AddSymptomWithoutPhotoUseCase,
    private val editSymptomWithoutUploadUseCase: EditSymptomWithoutUploadUseCase,
    private val editSymptomUseCase: EditSymptomUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null


    private val _viewStateSymptomsTypes = MutableStateFlow<List<SymptomType>>(listOf())
    val viewStateSymptomsTypes = _viewStateSymptomsTypes.asStateFlow()

    private val _viewStateAddSymptom = SingleLiveEvent<Boolean?>()
    val viewStateAddSymptom: LiveData<Boolean?> = _viewStateAddSymptom

    private val _viewStateEditSymptom = SingleLiveEvent<Boolean?>()
    val viewStateEditSymptom: LiveData<Boolean?> = _viewStateEditSymptom

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()

    private var symptomTrackField: ObservableField<SymptomTrack> = ObservableField(
        SymptomTrack()
    )


    fun getSymptomsTypes(){
        viewModelJob = viewModelScope.launch {
            getSymptomsTypesUseCase().collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            val symptomTypes = symptomTrackField.get()?.symptomTypes
                            Log.d("TAG", "getSymptomsTypes: $symptomTypes")
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

    fun addSymptom() {
        viewModelJob = viewModelScope.launch {
            val symptomTrackField = getSymptomTrack()
            val isReadyToUploading = symptomTrackField?.photosList?.any { it.uri != null } ?: false
            if (isReadyToUploading)
                addSymptomWithPhoto()
            else
                addSymptomWithoutPhoto()
        }
    }
    private fun addSymptomWithPhoto() {
        viewModelJob = viewModelScope.launch {
            val symptomTrackField = getSymptomTrack()
            symptomTrackField?.run {
                addSymptomUseCase(
                    AddSymptomRequestBuilder(
                        details = details ?: "",
                        symptomLookupIds = symptomTypes?.map { it.id } ?: listOf(),
                        doctorName = doctorName ?: "",
                        dateTime = "${startDate?.formatDateAPI() ?: ""} ${reminderTime?.formatTimeAPI() ?: ""}",
                        photos = photosList ?: listOf(),
                    )
                ).onEach { response ->
                    Log.d("TAG", "addSymptomFailed: ${response.errorEntity}")
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                if (it.symptomIsModified) {
                                    resetSymptomTrack()
                                    _viewStateAddSymptom.postValue(true)
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

    private fun addSymptomWithoutPhoto() {
        viewModelJob = viewModelScope.launch {
            val symptomTrackField = getSymptomTrack()
            symptomTrackField?.run {
                addSymptomWithoutPhotoUseCase(
                    AddSymptomRequestBuilder(
                        details = details ?: "",
                        symptomLookupIds = symptomTypes?.map { it.id } ?: listOf(),
                        doctorName = doctorName ?: "",
                        dateTime = "${startDate?.formatDateAPI() ?: ""} ${reminderTime?.formatTimeAPI() ?: ""}",
                        photos = listOf(),
                    ).buildRequestBody()
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                resetSymptomTrack()
                                _viewStateAddSymptom.postValue(true)
                            }
                        }

                        else -> {}
                    }
                }
            }
        }
    }

    fun editSymptom() {
        viewModelJob = viewModelScope.launch {
            val symptomTrackField = getSymptomTrack()
            val isReadyToUploading = symptomTrackField?.photosList?.any { it.uri != null } ?: false
            if (isReadyToUploading)
                editSymptomWithoutUpload()
            else
                editSymptomWithUpload()
        }
    }

    private fun editSymptomWithoutUpload() {
        viewModelJob = viewModelScope.launch {
            val symptomTrackField = getSymptomTrack()
            symptomTrackField?.run {
                editSymptomWithoutUploadUseCase(
                    symptomId = symptomId ?: 0,
                    AddSymptomRequestBuilder(
                        details = details ?: "",
                        symptomLookupIds = symptomTypes?.map { it.id } ?: listOf(),
                        doctorName = doctorName ?: "",
                        dateTime = "${startDate?.formatDateAPI() ?: ""} ${reminderTime?.formatTimeAPI() ?: ""}",
                        photos = photosList ?: listOf(),
                    ).buildRequestBody()
                ).onEach { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            if (response.data == true) {
                                resetSymptomTrack()
                                _viewStateEditSymptom.postValue(true)
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
        viewModelJob = viewModelScope.launch {
            val symptomTrackField = getSymptomTrack()
            symptomTrackField?.run {
                editSymptomUseCase(
                    symptomId = symptomId ?: 0,
                    AddSymptomRequestBuilder(
                        details = details ?: "",
                        symptomLookupIds = symptomTypes?.map { it.id } ?: listOf(),
                        doctorName = doctorName ?: "",
                        dateTime = "${startDate?.formatDateAPI() ?: ""} ${reminderTime?.formatTimeAPI() ?: ""}",
                        photos = photosList ?: listOf(),
                    )
                ).collect { response ->
                    _viewStateLoading.emit(response.loading)
                    when (response.status) {
                        Status.ERROR -> emitError(response.errorEntity)
                        Status.SUCCESS -> {
                            response.data?.let {
                                resetSymptomTrack()
                                _viewStateEditSymptom.postValue(true)
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

    fun resetSymptomTrack() =
        symptomTrackField.set(SymptomTrack())

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

    fun selectStartDate(startDate: String) =
        symptomTrackField.get()?.also {
            it.startDate = startDate
        }

    fun selectReminderTime(time: String?) =
        symptomTrackField.get()?.also {
            it.reminderTime = time
        }

    fun setDoctorName(doctorName: String) =
        symptomTrackField.get()?.also {
            it.doctorName = doctorName
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