package com.cancer.yaqeen.domain.features.onboarding.usecases

import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.onboarding.IOnboardingRepository
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.features.onboarding.responses.UpdateProfileResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserProfileUseCase @Inject constructor(private val repository: IOnboardingRepository) {
    suspend operator fun invoke(): Flow<DataState<User?>> =
        repository.getUserProfile()
}