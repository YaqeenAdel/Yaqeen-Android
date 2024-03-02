package com.cancer.yaqeen.data.features.home.schedule

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingAddMedicationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingEditMedicationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingRemindersFromNowRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingMedicationsRemindersRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Schedule
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingAddSymptomRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingAddSymptomWithUploadRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingCreateAnUploadLocationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingDeleteSymptomRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingEditSymptomRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingEditSymptomWithUploadRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingSymptomsRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingSymptomsTypesRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifySymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.UploadUrlRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.UploadUrlResponse
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.base.flowStatus
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import com.cancer.yaqeen.data.utils.compressImage
import com.cancer.yaqeen.presentation.util.FileUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okio.ByteString.Companion.toByteString
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: YaqeenAPI,
    errorHandler: ErrorHandlerImpl,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil
) : BaseDataSource(errorHandler), IScheduleRepository {


    override suspend fun addMedication(request: AddMedicationRequest): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingAddMedicationRemoteAsUIModel()) {
                apiService.addMedication(request)
            }
        }

    override suspend fun getMedicationsReminders(scheduleType: String): Flow<DataState<List<Medication>>> =
        flowStatus {
            getResultRestAPI(MappingMedicationsRemindersRemoteAsModel()) {
                apiService.getMedicationsReminders(scheduleType)
            }
        }

    override suspend fun getTodayReminders(): Flow<DataState<List<Schedule>>> =
        flowStatus {
            getResultRestAPI(MappingRemindersFromNowRemoteAsModel()) {
                apiService.getTodayReminders()
            }
        }


    override suspend fun editMedication(
        scheduleId: Int,
        request: AddMedicationRequest
    ): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingEditMedicationRemoteAsUIModel()) {
                apiService.editMedication(scheduleId, request)
            }
        }


    override suspend fun getSymptomsTypes(): Flow<DataState<List<SymptomType>>> =
        flowStatus {
            getResultRestAPI(MappingSymptomsTypesRemoteAsUIModel()) {
                apiService.getSymptomsTypes()
            }
        }


    private suspend fun createAnUploadLocation(
        fileUri: Uri?,
        folderName: String
    ): DataState<ModifySymptomResponse?> {
        val file = FileUtils.getFile(context, fileUri)
        val restCreateLocationAPI =
            getResultRestAPI(MappingCreateAnUploadLocationRemoteAsUIModel()) {
                apiService.createAnUploadLocation(
                    UploadUrlRequest(
                        path = "$folderName/${file?.name}"
                    )
                )
            }
        return restCreateLocationAPI
    }

    private suspend fun uploadImages(folderName: String, photos: List<Photo>): List<DataState<ModifySymptomResponse?>?> {
        val responses = coroutineScope {
            photos.filter { it.uri != null }.map { photo ->
                async {
                    val fileUri = photo.uri

                    val restCreateLocationAPI = createAnUploadLocation(fileUri, folderName)

                    if (restCreateLocationAPI.status == Status.SUCCESS) {
                        val signedURL = restCreateLocationAPI.data?.signedURL

                        val imageRequestBody = getImageRequestBody(fileUri)

                        coroutineScope {
                            launch(Dispatchers.IO) {
                                val putRequest = Request
                                    .Builder()
                                    .url(signedURL.toString())
                                    .put(imageRequestBody)
                                    .build()

                                try {
                                    val response =
                                        OkHttpClient().newCall(putRequest).execute()

                                    if (response.isSuccessful) {
                                        val pathURL = restCreateLocationAPI.data?.path.toString()
                                        restCreateLocationAPI.data?.apply {
                                            photoIsUploaded = true
                                        }

                                        photos.firstOrNull {
                                            it.id == photo.id
                                        }?.pathURL = pathURL

                                    }else {
                                        throw Exception()
                                    }
                                } catch (e: Exception) {
                                    throw Exception()
                                }
                            }
                        }
                    }else{
                        throw Exception()
                    }
                    restCreateLocationAPI
                }
            }.awaitAll()
        }
        return responses
    }

    override suspend fun addSymptom(builder: AddSymptomRequestBuilder): Flow<DataState<ModifySymptomResponse?>> =
        flow {
            emit(DataState.Loading())
            try {
                val photos = builder.photos

                val responses = uploadImages("symptoms", photos)

                val isFailed =
                    responses.any { it?.status == Status.ERROR || it?.status == Status.FAILED}

                if (isFailed){
                    emit(DataState.Failed())
                }else {
                    val addSymptomResponseAPI = getResultRestAPI(
                        MappingAddSymptomWithUploadRemoteAsUIModel()
                    ) {
                        apiService.addSymptom(builder.apply {
                            this.photos = photos
                        }.buildRequestBody())
                    }
                    emit(addSymptomResponseAPI)
                }

            } catch (e: Exception) {
                emit(DataState.Failed())
            }
        }


    override suspend fun addSymptomWithoutPhoto(request: AddSymptomRequest): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingAddSymptomRemoteAsUIModel()) {
                apiService.addSymptom(request.apply {
                    symptom.photoLink = ""
                })
            }
        }


    override suspend fun editSymptomWithoutUpload(
        symptomId: Int,
        request: AddSymptomRequest
    ): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingEditSymptomRemoteAsUIModel()) {
                apiService.editSymptom(symptomId, request)
            }
        }


    override suspend fun editSymptom(
        symptomId: Int,
        builder: AddSymptomRequestBuilder
    ): Flow<DataState<ModifySymptomResponse?>> =
        flow {
            emit(DataState.Loading())
            try {
                val photos = builder.photos

                val responses = uploadImages("symptoms", photos)

                val isFailed =
                    responses.any { it?.status == Status.ERROR || it?.status == Status.FAILED}

                if (isFailed){
                    emit(DataState.Failed())
                }else {
                    val editSymptomResponseAPI = getResultRestAPI(
                        MappingEditSymptomWithUploadRemoteAsUIModel()
                    ) {
                        apiService.editSymptom(symptomId, builder.apply {
                            this.photos = photos
                        }.buildRequestBody())
                    }
                    emit(editSymptomResponseAPI)
                }

            } catch (e: Exception) {
                emit(DataState.Failed())
            }
        }

    private fun getImageRequestBody(fileUri: Uri?): RequestBody {
        val byteArray = compressImage(
            context,
            Uri.parse(fileUri.toString()),
            1080,
            2400,
            100
        )
        val fileExtension =
            MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
        val imageRequestBody = RequestBody.create(
            "image/${fileExtension}".toMediaTypeOrNull(),
            byteArray!!.toByteString()
        )
        return imageRequestBody
    }


    override suspend fun deleteSymptom(symptomId: Int): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingDeleteSymptomRemoteAsUIModel()) {
                apiService.deleteSymptom(symptomId)
            }
        }

    override suspend fun getSymptoms(): Flow<DataState<List<Symptom>>> =
        flowStatus {
            getResultRestAPI(MappingSymptomsRemoteAsModel()) {
                apiService.getSymptoms()
            }
        }

}