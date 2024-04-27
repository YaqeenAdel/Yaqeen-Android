package com.cancer.yaqeen.presentation.service

import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB

interface IWorkerManager {
    fun setScheduleMedication(medication: MedicationDB): String
    fun setPeriodScheduleForMedication(medication: MedicationDB): String
    fun setPeriodScheduleDaysForMedication(medication: MedicationDB): List<String>
    fun setPeriodScheduleForRoutineTest(routineTest: RoutineTestDB): Pair<String, String?>
    fun scheduleReminderForRoutineTest(routineTest: RoutineTestDB): String
    fun setPeriodScheduleForMedicalAppointment(medicalAppointment: MedicalAppointmentDB): Pair<String, String?>
    fun scheduleReminderForMedicalAppointment(medicalAppointment: MedicalAppointmentDB): String
    fun cancelWork(workRequestId: String)
    fun cancelAllWorks()
}