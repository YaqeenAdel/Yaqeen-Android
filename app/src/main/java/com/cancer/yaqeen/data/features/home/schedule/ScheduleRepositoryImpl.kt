package com.cancer.yaqeen.data.features.home.schedule

import android.content.Context
import android.net.Uri
import android.webkit.MimeTypeMap
import com.cancer.yaqeen.data.features.home.schedule.data_sources.ScheduleLocalDataSourceImpl
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.mappers.MappingAddMedicalReminderRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.mappers.MappingAddSymptomToMedicalReminderRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.mappers.MappingDeleteScheduleRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.mappers.MappingDeleteSymptomFromScheduleRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.mappers.MappingEditMedicalReminderRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.mappers.MappingMedicalRemindersRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.ModifyMedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.requests.AddMedicalReminderRequest
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.requests.AddSymptomToMedicalReminderRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingAddMedicationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingEditMedicationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingRemindersFromNowRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.medication.mappers.MappingMedicationsRemindersRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Schedule
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.mappers.MappingAddRoutineTestRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.routine_test.mappers.MappingAddRoutineTestWithUploadRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.routine_test.mappers.MappingEditRoutineTestRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.routine_test.mappers.MappingEditRoutineTestWithUploadRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.routine_test.mappers.MappingRoutineTestsRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTest
import com.cancer.yaqeen.data.features.home.schedule.routine_test.requests.AddRoutineTestRequest
import com.cancer.yaqeen.data.features.home.schedule.routine_test.requests.AddRoutineTestRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingAddSymptomRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingAddSymptomWithUploadRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingCreateAnUploadLocationRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingDeleteSymptomRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingEditSymptomRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingEditSymptomWithUploadRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingSymptomsRemoteAsModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.mappers.MappingSymptomsTypesRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifyScheduleResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.UploadUrlRequest
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
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleRepositoryImpl @Inject constructor(
    private val context: Context,
    private val apiService: YaqeenAPI,
    private val scheduleLocalDataSource: ScheduleLocalDataSourceImpl,
    errorHandler: ErrorHandlerImpl,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil
) : BaseDataSource(errorHandler), IScheduleRepository {


    override suspend fun addMedication(request: AddMedicationRequest): Flow<DataState<Int?>> =
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
    ): DataState<ModifyScheduleResponse?> {
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

    private suspend fun uploadImage(
        folderName: String,
        photo: Photo
    ): DataState<ModifyScheduleResponse?> {
        val response = coroutineScope {
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

                            val client = OkHttpClient.Builder()
                                .connectTimeout(30, TimeUnit.SECONDS)
                                .writeTimeout(30, TimeUnit.SECONDS)
                                .readTimeout(30, TimeUnit.SECONDS)
                                .build()

                            val response =
                                client.newCall(putRequest).execute()

                            if (response.isSuccessful) {
                                val pathURL = restCreateLocationAPI.data?.path.toString()
                                restCreateLocationAPI.data?.apply {
                                    photoIsUploaded = true
                                }

                                photo.pathURL = pathURL

                            } else {
                                throw Exception()
                            }
                        } catch (e: Exception) {
                            throw Exception()
                        }
                    }
                }
            } else {
                throw Exception()
            }
            restCreateLocationAPI
        }
        return response
    }

    private suspend fun uploadImages(
        folderName: String,
        photos: List<Photo>
    ): List<DataState<ModifyScheduleResponse?>?> {
        val responses = coroutineScope {
            photos.filter { it.uri != null }.map { photo ->
                async {
                    uploadImage(
                        folderName = folderName,
                        photo = photo
                    )
                }
            }.awaitAll()
        }
        return responses
    }

    override suspend fun addSymptom(builder: AddSymptomRequestBuilder): Flow<DataState<ModifyScheduleResponse?>> =
        flow {
            emit(DataState.Loading())
            try {
                val photos = builder.photos

                val responses = uploadImages("symptoms", photos)

                val isFailed =
                    responses.any { it?.status == Status.ERROR || it?.status == Status.FAILED }

                if (isFailed) {
                    emit(DataState.Failed())
                } else {
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
    ): Flow<DataState<ModifyScheduleResponse?>> =
        flow {
            emit(DataState.Loading())
            try {
                val photos = builder.photos

                val responses = uploadImages("symptoms", photos)

                val isFailed =
                    responses.any { it?.status == Status.ERROR || it?.status == Status.FAILED }

                if (isFailed) {
                    emit(DataState.Failed())
                } else {
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

    override suspend fun addMedicalReminder(
        request: AddMedicalReminderRequest,
        symptomId: Int?
    ): Flow<DataState<ModifyMedicalReminder?>> =
        flowStatus {
            val addMedicalReminderResponseAPI =
                getResultRestAPI(MappingAddMedicalReminderRemoteAsUIModel()) {
                    apiService.addMedicalReminder(request)
                }

            if (symptomId == null || symptomId == 0)
                addMedicalReminderResponseAPI
            else
                addMedicalReminderResponseAPI.data?.let {
                    getResultRestAPI(MappingAddSymptomToMedicalReminderRemoteAsUIModel(it.scheduleID)) {
                        apiService.addSymptomToMedicalReminder(
                            AddSymptomToMedicalReminderRequestBuilder(
                                medicalReminderID = it.scheduleID,
                                symptomID = symptomId ?: 0,
                            ).buildRequestBody()
                        )
                    }
                } ?: run {
                    addMedicalReminderResponseAPI
                }

        }

    override suspend fun editMedicalReminder(
        scheduleId: Int,
        request: AddMedicalReminderRequest,
        symptomId: Int?
    ): Flow<DataState<ModifyMedicalReminder?>> =
        flowStatus {
            val editMedicalReminderResponseAPI =
                getResultRestAPI(MappingEditMedicalReminderRemoteAsUIModel()) {
                    apiService.editMedicalReminder(scheduleId, request)
                }

            if (symptomId == null || symptomId == 0)
                editMedicalReminderResponseAPI
            else
                editMedicalReminderResponseAPI.data?.let {
                    getResultRestAPI(MappingAddSymptomToMedicalReminderRemoteAsUIModel(it.scheduleID)) {
                        apiService.addSymptomToMedicalReminder(
                            AddSymptomToMedicalReminderRequestBuilder(
                                medicalReminderID = it.scheduleID,
                                symptomID = symptomId ?: 0,
                            ).buildRequestBody()
                        )
                    }
                } ?: run {
                    editMedicalReminderResponseAPI
                }

        }

    override suspend fun deleteSymptomFromMedicalReminder(scheduleId: Int, symptomId: Int): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingDeleteSymptomFromScheduleRemoteAsUIModel()) {
                apiService.deleteSymptomFromMedicalReminder(scheduleId, symptomId)
            }
        }

    override suspend fun getMedicalReminders(scheduleType: String): Flow<DataState<List<MedicalReminder>>> =
        flowStatus {
            getResultRestAPI(MappingMedicalRemindersRemoteAsModel()) {
                apiService.getMedicalReminders(scheduleType = scheduleType, lang = prefEncryptionUtil.selectedLanguage)
            }
        }

    override suspend fun deleteSchedule(scheduleId: Int): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingDeleteScheduleRemoteAsUIModel()) {
                apiService.deleteSchedule(scheduleId)
            }
        }

    override suspend fun addRoutineTest(builder: AddRoutineTestRequestBuilder): Flow<DataState<ModifyScheduleResponse?>> =
        flow {
            emit(DataState.Loading())
            try {
                val photo = builder.photo

                val response = uploadImage("routine_tests", photo!!)

                val isFailed = response?.status == Status.ERROR || response?.status == Status.FAILED

                if (isFailed) {
                    emit(DataState.Failed())
                } else {
                    val addRoutineTestResponseAPI = getResultRestAPI(
                        MappingAddRoutineTestWithUploadRemoteAsUIModel()
                    ) {
                        apiService.addRoutineTest(builder.apply {
                            this.photo = photo
                        }.buildRequestBody())
                    }
                    emit(addRoutineTestResponseAPI)
                }

            } catch (e: Exception) {
                emit(DataState.Failed())
            }
        }

    override suspend fun addRoutineTestWithoutPhoto(request: AddRoutineTestRequest): Flow<DataState<Int?>> =
        flowStatus {
            getResultRestAPI(MappingAddRoutineTestRemoteAsUIModel()) {
                apiService.addRoutineTest(request)
            }
        }

    override suspend fun editRoutineTest(
        scheduleId: Int,
        builder: AddRoutineTestRequestBuilder
    ): Flow<DataState<ModifyScheduleResponse?>> =
        flow {
            emit(DataState.Loading())
            try {
                val photo = builder.photo

                val response = uploadImage("routine_tests", photo!!)

                val isFailed = response?.status == Status.ERROR || response?.status == Status.FAILED

                if (isFailed) {
                    emit(DataState.Failed())
                } else {
                    val editRoutineTestResponseAPI = getResultRestAPI(
                        MappingEditRoutineTestWithUploadRemoteAsUIModel()
                    ) {
                        apiService.editRoutineTest(scheduleId, builder.apply {
                            this.photo = photo
                        }.buildRequestBody())
                    }
                    emit(editRoutineTestResponseAPI)
                }

            } catch (e: Exception) {
                emit(DataState.Failed())
            }
        }
    override suspend fun editRoutineTestWithoutUpload(
        scheduleId: Int,
        request: AddRoutineTestRequest
    ): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingEditRoutineTestRemoteAsUIModel()) {
                apiService.editRoutineTest(scheduleId, request)
            }
        }

    override suspend fun getRoutineTests(scheduleType: String): Flow<DataState<List<RoutineTest>>> =
        flowStatus {
            getResultRestAPI(MappingRoutineTestsRemoteAsModel()) {
                apiService.getRoutineTests(scheduleType)
            }
        }

    override suspend fun getMedication(medicationId: Int): Flow<DataState<MedicationDB?>> =
        flowStatus {
            scheduleLocalDataSource.getMedication(medicationId)
        }

    override suspend fun getMedications(): Flow<DataState<List<MedicationDB>?>> =
        flowStatus {
            scheduleLocalDataSource.getMedications()
        }

    override suspend fun getRemindedMedications(): Flow<DataState<List<MedicationDB>?>> =
        flowStatus {
            scheduleLocalDataSource.getRemindedMedications()
        }

    override suspend fun saveMedication(medication: MedicationDB): Flow<DataState<Long>> =
        flowStatus {
            scheduleLocalDataSource.saveMedication(medication)
        }

    override suspend fun editMedication(medication: MedicationDB): Flow<DataState<Int>> =
        flowStatus {
            scheduleLocalDataSource.editMedication(medication)
        }

    override suspend fun removeMedication(medicationId: Int): Flow<DataState<Int>> =
        flowStatus {
            scheduleLocalDataSource.removeMedication(medicationId)
        }

    override suspend fun removeMedications(): Flow<DataState<Int>> =
        flowStatus {
            scheduleLocalDataSource.removeMedications()
        }

    override suspend fun getRoutineTest(routineTestId: Int): Flow<DataState<RoutineTestDB?>> =
        flowStatus {
            scheduleLocalDataSource.getRoutineTest(routineTestId)
        }

    override suspend fun getRoutineTests(): Flow<DataState<List<RoutineTestDB>?>> =
        flowStatus {
            scheduleLocalDataSource.getRoutineTests()
        }

    override suspend fun getRemindedRoutineTests(): Flow<DataState<List<RoutineTestDB>?>> =
        flowStatus {
            scheduleLocalDataSource.getRemindedRoutineTests()
        }

    override suspend fun saveRoutineTest(routineTest: RoutineTestDB): Flow<DataState<Long>> =
        flowStatus {
            scheduleLocalDataSource.saveRoutineTest(routineTest)
        }

    override suspend fun editRoutineTest(routineTest: RoutineTestDB): Flow<DataState<Int>> =
        flowStatus {
            scheduleLocalDataSource.editRoutineTest(routineTest)
        }

    override suspend fun removeRoutineTest(routineTestId: Int): Flow<DataState<Int>> =
        flowStatus {
            scheduleLocalDataSource.removeRoutineTest(routineTestId)
        }

    override suspend fun removeRoutineTests(): Flow<DataState<Int>> =
        flowStatus {
            scheduleLocalDataSource.removeRoutineTests()
        }

    override suspend fun getMedicalAppointment(medicalAppointmentId: Int): Flow<DataState<MedicalAppointmentDB?>> =
        flowStatus {
            scheduleLocalDataSource.getMedicalAppointment(medicalAppointmentId)
        }

    override suspend fun saveMedicalAppointment(medicalAppointment: MedicalAppointmentDB): Flow<DataState<Long>> =
        flowStatus {
            scheduleLocalDataSource.saveMedicalAppointment(medicalAppointment)
        }

    override suspend fun editMedicalAppointment(medicalAppointment: MedicalAppointmentDB): Flow<DataState<Int>> =
        flowStatus {
            scheduleLocalDataSource.editMedicalAppointment(medicalAppointment)
        }

    override suspend fun removeMedicalAppointment(medicalAppointmentId: Int): Flow<DataState<Int>> =
        flowStatus {
            scheduleLocalDataSource.removeMedicalAppointment(medicalAppointmentId)
        }

    override suspend fun removeMedicalAppointments(): Flow<DataState<Int>> =
        flowStatus {
            scheduleLocalDataSource.removeMedicalAppointments()
        }

}