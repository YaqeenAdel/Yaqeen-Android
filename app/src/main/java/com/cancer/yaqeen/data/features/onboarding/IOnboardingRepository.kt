package com.cancer.yaqeen.data.features.onboarding

import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.features.onboarding.responses.UpdateProfileResponse
import kotlinx.coroutines.flow.Flow

interface IOnboardingRepository {
    suspend fun getResources(): Flow<DataState<Resources>>
    suspend fun updateUserProfile(requestBody: UpdateProfileRequestBody): Flow<DataState<Any>>
    suspend fun getUserProfile(): Flow<DataState<User?>>
}