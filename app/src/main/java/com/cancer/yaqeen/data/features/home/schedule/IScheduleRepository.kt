package com.cancer.yaqeen.data.features.home.schedule

import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Schedule
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifySymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.UploadUrlRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.DeleteSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.UploadUrlResponse
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow

interface IScheduleRepository {
     suspend fun addMedication(request: AddMedicationRequest): Flow<DataState<Boolean>>
     suspend fun getMedicationsReminders(scheduleType: String): Flow<DataState<List<Medication>>>
     suspend fun getTodayReminders(): Flow<DataState<List<Schedule>>>
     suspend fun editMedication(scheduleId: Int, request: AddMedicationRequest): Flow<DataState<Boolean>>
     suspend fun getSymptomsTypes(): Flow<DataState<List<SymptomType>>>
     suspend fun addSymptom(builder: AddSymptomRequestBuilder): Flow<DataState<ModifySymptomResponse?>>
     suspend fun addSymptomWithoutPhoto(request: AddSymptomRequest): Flow<DataState<Boolean>>
     suspend fun editSymptom(symptomId: Int, builder: AddSymptomRequestBuilder): Flow<DataState<ModifySymptomResponse?>>
     suspend fun editSymptomWithoutUpload(symptomId: Int, request: AddSymptomRequest): Flow<DataState<Boolean>>
     suspend fun deleteSymptom(symptomId: Int): Flow<DataState<Boolean>>
     suspend fun getSymptoms(): Flow<DataState<List<Symptom>>>
}