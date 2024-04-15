package com.cancer.yaqeen.data.features.home.schedule.data_sources

import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow

interface IScheduleLocalDataSource {
    suspend fun saveMedication(medication: MedicationDB): DataState<Long>
    suspend fun saveRoutineTest(routineTest: RoutineTestDB): DataState<Long>
    suspend fun saveMedicalAppointment(medicalAppointment: MedicalAppointmentDB): DataState<Long>
}