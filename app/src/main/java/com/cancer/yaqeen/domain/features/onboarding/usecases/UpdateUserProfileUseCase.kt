package com.cancer.yaqeen.domain.features.onboarding.usecases

import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.onboarding.IOnboardingRepository
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateUserProfileUseCase @Inject constructor(private val repository: IOnboardingRepository) {
    suspend operator fun invoke(requestBody: UpdateProfileRequestBody): Flow<DataState<Boolean>> =
        repository.updateUserProfile(requestBody)
}