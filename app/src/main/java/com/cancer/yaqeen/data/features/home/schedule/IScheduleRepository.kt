package com.cancer.yaqeen.data.features.home.schedule

import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow

interface IScheduleRepository {
     suspend fun addMedication(request: AddMedicationRequest): Flow<DataState<Boolean>>
     suspend fun getMedicationsReminders(scheduleType: String): Flow<DataState<List<Medication>>>
     suspend fun getTodayReminders(): Flow<DataState<List<Medication>>>
     suspend fun editMedication(scheduleId: Int, request: AddMedicationRequest): Flow<DataState<Boolean>>

}