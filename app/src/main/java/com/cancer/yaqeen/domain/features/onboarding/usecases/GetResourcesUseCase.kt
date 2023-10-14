package com.cancer.yaqeen.domain.features.onboarding.usecases

import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.onboarding.IOnboardingRepository
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetResourcesUseCase @Inject constructor(private val repository: IOnboardingRepository) {
    suspend operator fun invoke(): Flow<DataState<Resources>> =
        repository.getResources()
}