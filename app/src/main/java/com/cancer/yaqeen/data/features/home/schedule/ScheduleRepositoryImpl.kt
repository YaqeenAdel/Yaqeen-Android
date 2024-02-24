package com.cancer.yaqeen.data.features.home.schedule

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import androidx.core.net.toUri
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingAddMedicationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingEditMedicationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingRemindersFromNowRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingMedicationsRemindersRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Schedule
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingAddSymptomRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingCreateAnUploadLocationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingDeleteSymptomRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingEditSymptomRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingSymptomsRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingSymptomsTypesRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifySymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequest
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

    override suspend fun addSymptom(request: AddSymptomRequest): Flow<DataState<ModifySymptomResponse?>> =
        flowStatus {
            val fileUri = request.symptom.photoLink.toUri()
            val file = FileUtils.getFile(context, fileUri)
            val restCreateLocationAPI = getResultRestAPI(MappingCreateAnUploadLocationRemoteAsUIModel()) {
                apiService.createAnUploadLocation(
                    UploadUrlRequest(
                        path = "symptoms/${file?.name}"
                    )
                )
            }

            if (restCreateLocationAPI.status == Status.SUCCESS) {
                val signedURL = restCreateLocationAPI.data?.signedURL
                val pathURL = restCreateLocationAPI.data?.path

                val byteArray = compressImage(
                    context,
                    Uri.parse(fileUri.toString()),
                    1080,
                    2400,
                    100
                )
                val fileExtension =
                    MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
                val imageRequestBody = RequestBody.Companion.create(
                    "image/${fileExtension}".toMediaTypeOrNull(),
                    byteArray!!.toByteString()
                )
                coroutineScope {
                    launch(Dispatchers.IO) {
                        val putRequest = Request
                            .Builder()
                            .url(signedURL.toString())
                            .put(imageRequestBody)
                            .build()

                        try {
                            val response = OkHttpClient().newCall(putRequest).execute()

                            if (response.isSuccessful) {
                                restCreateLocationAPI.data?.apply {
                                    photoIsUploaded = true
                                }
                                val addSymptomResponseAPI = getResultRestAPI {
                                    apiService.addSymptom(request.apply {
                                        symptom.photoLink = pathURL.toString()
                                    })
                                }

                                if (addSymptomResponseAPI.status == Status.SUCCESS) {
                                    restCreateLocationAPI.data?.apply {
                                        symptomIsAdded = true
                                    }
                                }
                            }
                        } catch (e: Exception) {
                        }
                    }
                }
            }

            restCreateLocationAPI
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
        request: AddSymptomRequest
    ): Flow<DataState<UploadUrlResponse>> =
        flowStatus {
            val fileUri = request.symptom.photoLink.toUri()
            val file = FileUtils.getFile(context, fileUri)
            val restCreateLocationAPI = getResultRestAPI {
                apiService.createAnUploadLocation(
                    UploadUrlRequest(
                        path = "symptoms/${file?.name}"
                    )
                )
            }

            if (restCreateLocationAPI.status == Status.SUCCESS) {
                val signedURL = restCreateLocationAPI.data?.getUploadURL?.signedURL
                val pathURL = restCreateLocationAPI.data?.getUploadURL?.path

                val byteArray = compressImage(
                    context,
                    Uri.parse(fileUri.toString()),
                    1080,
                    2400,
                    100
                )
                val fileExtension =
                    MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
                val imageRequestBody = RequestBody.Companion.create(
                    "image/${fileExtension}".toMediaTypeOrNull(),
                    byteArray!!.toByteString()
                )
                coroutineScope {
                    launch(Dispatchers.IO) {
                        val putRequest = Request
                            .Builder()
                            .url(signedURL.toString())
                            .put(imageRequestBody)
                            .build()

                        val response = OkHttpClient().newCall(putRequest).execute()

                        if (response.isSuccessful) {
                            Log.d("TAG", "uploadImage: $response ")
                            getResultRestAPI() {
                                apiService.editSymptom(symptomId, request.apply {
                                    symptom.photoLink = pathURL.toString()
                                })
                            }
                        }
                    }
                }
            }

            restCreateLocationAPI
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