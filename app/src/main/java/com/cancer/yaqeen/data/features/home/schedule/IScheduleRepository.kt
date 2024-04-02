package com.cancer.yaqeen.data.features.home.schedule

import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.ModifyMedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.requests.AddMedicalReminderRequest
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Schedule
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTest
import com.cancer.yaqeen.data.features.home.schedule.routine_test.requests.AddRoutineTestRequest
import com.cancer.yaqeen.data.features.home.schedule.routine_test.requests.AddRoutineTestRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifyScheduleResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequestBuilder
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow

interface IScheduleRepository {
     suspend fun addMedication(request: AddMedicationRequest): Flow<DataState<Boolean>>
     suspend fun getMedicationsReminders(scheduleType: String): Flow<DataState<List<Medication>>>
     suspend fun getTodayReminders(): Flow<DataState<List<Schedule>>>
     suspend fun editMedication(scheduleId: Int, request: AddMedicationRequest): Flow<DataState<Boolean>>
     suspend fun getSymptomsTypes(): Flow<DataState<List<SymptomType>>>
     suspend fun addSymptom(builder: AddSymptomRequestBuilder): Flow<DataState<ModifyScheduleResponse?>>
     suspend fun addSymptomWithoutPhoto(request: AddSymptomRequest): Flow<DataState<Boolean>>
     suspend fun editSymptom(symptomId: Int, builder: AddSymptomRequestBuilder): Flow<DataState<ModifyScheduleResponse?>>
     suspend fun editSymptomWithoutUpload(symptomId: Int, request: AddSymptomRequest): Flow<DataState<Boolean>>
     suspend fun deleteSymptom(symptomId: Int): Flow<DataState<Boolean>>
     suspend fun getSymptoms(): Flow<DataState<List<Symptom>>>
     suspend fun addMedicalReminder(request: AddMedicalReminderRequest, symptomId: Int?): Flow<DataState<ModifyMedicalReminder?>>
     suspend fun getMedicalReminders(scheduleType: String): Flow<DataState<List<MedicalReminder>>>
     suspend fun deleteSchedule(scheduleId: Int): Flow<DataState<Boolean>>
     suspend fun addRoutineTest(builder: AddRoutineTestRequestBuilder): Flow<DataState<ModifyScheduleResponse?>>
     suspend fun addRoutineTestWithoutPhoto(request: AddRoutineTestRequest): Flow<DataState<Boolean>>
     suspend fun getRoutineTests(scheduleType: String): Flow<DataState<List<RoutineTest>>>
     suspend fun editRoutineTest(scheduleId: Int, request: AddRoutineTestRequestBuilder): Flow<DataState<ModifyScheduleResponse?>>
     suspend fun editRoutineTestWithoutUpload(scheduleId: Int, request: AddRoutineTestRequest): Flow<DataState<Boolean>>
     suspend fun editMedicalReminder(scheduleId: Int, request: AddMedicalReminderRequest, symptomId: Int?): Flow<DataState<ModifyMedicalReminder?>>
}