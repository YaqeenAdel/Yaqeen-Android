package com.cancer.yaqeen.domain.features.home.schedule.routine_test

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.features.home.schedule.routine_test.requests.AddRoutineTestRequest
import com.cancer.yaqeen.data.features.home.schedule.routine_test.requests.AddRoutineTestRequestBuilder
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifyScheduleResponse
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditRoutineTestUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(
        scheduleId: Int,
        builder: AddRoutineTestRequestBuilder
    ): Flow<DataState<ModifyScheduleResponse?>> =
        repository.editRoutineTest(
            scheduleId,
            builder
        )
}