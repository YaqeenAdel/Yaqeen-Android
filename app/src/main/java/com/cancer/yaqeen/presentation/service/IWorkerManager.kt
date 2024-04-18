package com.cancer.yaqeen.presentation.service

import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import java.util.UUID

interface IWorkerManager {
    fun setScheduleMedication(medication: MedicationDB): UUID
    fun setPeriodScheduleForMedication(medication: MedicationDB): UUID
    fun setPeriodScheduleForRoutineTest(routineTest: RoutineTestDB): Pair<UUID, UUID?>
    fun scheduleReminderForRoutineTest(routineTest: RoutineTestDB): UUID
    fun setPeriodScheduleForMedicalAppointment(medicalAppointment: MedicalAppointmentDB): Pair<UUID, UUID?>
    fun scheduleReminderForMedicalAppointment(medicalAppointment: MedicalAppointmentDB): UUID
    fun cancelWork(workRequestId: UUID)
    fun cancelAllWorks()
}