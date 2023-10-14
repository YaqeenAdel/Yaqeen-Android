package com.cancer.yaqeen.data.features.onboarding

import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import kotlinx.coroutines.flow.Flow

interface IOnboardingRepository {
    suspend fun getResources(): Flow<DataState<Resources>>
}