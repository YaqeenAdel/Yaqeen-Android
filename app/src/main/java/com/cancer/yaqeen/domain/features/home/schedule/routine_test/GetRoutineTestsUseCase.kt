package com.cancer.yaqeen.domain.features.home.schedule.routine_test

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTest
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetRoutineTestsUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(
        scheduleType: String
    ): Flow<DataState<List<RoutineTest>>> =
        repository.getRoutineTests(
            scheduleType
        )
}