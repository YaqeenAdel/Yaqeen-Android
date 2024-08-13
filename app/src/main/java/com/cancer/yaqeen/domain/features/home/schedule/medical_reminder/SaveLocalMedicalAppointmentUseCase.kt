package com.cancer.yaqeen.domain.features.home.schedule.medical_reminder

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.room.MedicalAppointmentDB
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveLocalMedicalAppointmentUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(
        medicalAppointment: MedicalAppointmentDB
    ): Flow<DataState<Long>> =
        repository.saveMedicalAppointment(
            medicalAppointment
        )
}