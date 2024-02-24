package com.cancer.yaqeen.domain.features.home.schedule.symptom

import com.cancer.yaqeen.data.features.home.schedule.IScheduleRepository
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSymptomsTypesUseCase @Inject constructor(private val repository: IScheduleRepository) {
    suspend operator fun invoke(): Flow<DataState<List<SymptomType>>> =
        repository.getSymptomsTypes()
}