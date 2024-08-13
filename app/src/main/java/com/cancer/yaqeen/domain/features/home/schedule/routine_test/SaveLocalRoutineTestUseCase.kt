package com.cancer.yaqeen.domain.features.home.schedule.routine_test

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.routine_test.room.RoutineTestDB
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveLocalRoutineTestUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(
        routineTest: RoutineTestDB
    ): Flow<DataState<Long>> =
        repository.saveRoutineTest(
            routineTest
        )
}