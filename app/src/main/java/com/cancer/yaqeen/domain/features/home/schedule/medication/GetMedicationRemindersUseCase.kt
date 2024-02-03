package com.cancer.yaqeen.domain.features.home.schedule.medication

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMedicationRemindersUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(
        scheduleType: String
    ): Flow<DataState<List<Medication>>> =
        repository.getMedicationsReminders(
            scheduleType
        )
}