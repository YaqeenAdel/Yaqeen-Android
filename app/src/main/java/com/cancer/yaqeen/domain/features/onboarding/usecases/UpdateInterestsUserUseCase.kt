package com.cancer.yaqeen.domain.features.onboarding.usecases

import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.onboarding.IOnboardingRepository
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateInterestsUserRequestBody
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.features.onboarding.responses.UpdateProfileResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateInterestsUserUseCase @Inject constructor(private val repository: IOnboardingRepository) {
    suspend operator fun invoke(requestBody: UpdateInterestsUserRequestBody): Flow<DataState<Any>> =
        repository.updateInterestsUser(requestBody)
}