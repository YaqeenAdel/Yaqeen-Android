package com.cancer.yaqeen.data.features.home.schedule.data_sources

import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.network.base.DataState

interface IScheduleLocalDataSource {
    suspend fun getMedication(medicationId: Int): DataState<MedicationDB?>
    suspend fun getMedications(): DataState<List<MedicationDB>?>
    suspend fun getRemindedMedications(): DataState<List<MedicationDB>?>
    suspend fun saveMedication(medication: MedicationDB): DataState<Long>
    suspend fun editMedication(medication: MedicationDB): DataState<Int>
    suspend fun removeMedication(medicationId: Int): DataState<Int>
    suspend fun removeMedications(): DataState<Int>
    suspend fun getRoutineTest(routineTestId: Int): DataState<RoutineTestDB?>
    suspend fun getRoutineTests(): DataState<List<RoutineTestDB>?>
    suspend fun getRemindedRoutineTests(): DataState<List<RoutineTestDB>?>
    suspend fun saveRoutineTest(routineTest: RoutineTestDB): DataState<Long>
    suspend fun editRoutineTest(routineTest: RoutineTestDB): DataState<Int>
    suspend fun removeRoutineTest(routineTestId: Int): DataState<Int>
    suspend fun removeRoutineTests(): DataState<Int>
    suspend fun saveMedicalAppointment(medicalAppointment: MedicalAppointmentDB): DataState<Long>
    suspend fun editMedicalAppointment(medicalAppointment: MedicalAppointmentDB): DataState<Int>
    suspend fun getMedicalAppointment(medicalAppointmentId: Int): DataState<MedicalAppointmentDB?>
    suspend fun removeMedicalAppointment(medicalAppointmentId: Int): DataState<Int>
    suspend fun removeMedicalAppointments(): DataState<Int>
}