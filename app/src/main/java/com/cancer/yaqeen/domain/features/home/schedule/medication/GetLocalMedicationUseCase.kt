package com.cancer.yaqeen.domain.features.home.schedule.medication

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.medication.room.MedicationDB
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetLocalMedicationUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(
        medicationId: Int
    ): Flow<DataState<MedicationDB?>> =
        repository.getMedication(
            medicationId
        )
}