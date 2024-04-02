package com.cancer.yaqeen.domain.features.home.schedule.symptom

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifyScheduleResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequestBuilder
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddSymptomUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(
        builder: AddSymptomRequestBuilder
    ): Flow<DataState<ModifyScheduleResponse?>> =
        repository.addSymptom(
            builder
        )
}