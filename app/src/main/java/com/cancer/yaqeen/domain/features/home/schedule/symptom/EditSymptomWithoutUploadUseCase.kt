package com.cancer.yaqeen.domain.features.home.schedule.symptom

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.medication.requests.AddMedicationRequest
import com.cancer.yaqeen.data.features.home.schedule.symptom.requests.AddSymptomRequest
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class EditSymptomWithoutUploadUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(
        symptomId: Int,
        request: AddSymptomRequest,
    ): Flow<DataState<Boolean>> =
        repository.editSymptomWithoutUpload(
            symptomId, request
        )
}