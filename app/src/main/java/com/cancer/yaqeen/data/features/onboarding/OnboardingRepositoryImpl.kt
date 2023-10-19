package com.cancer.yaqeen.data.features.onboarding

import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.network.base.flowStatus
import com.cancer.yaqeen.data.features.onboarding.mappers.MappingResourcesRemoteAsModel
import com.cancer.yaqeen.data.features.onboarding.mappers.MappingUserProfileRemoteAsModel
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateInterestsUserRequestBody
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.features.onboarding.responses.UpdateProfileResponse
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil.Companion.PREF_USER
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val apiService: YaqeenAPI,
    errorHandler: ErrorHandlerImpl,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IOnboardingRepository {

    override suspend fun getResources(): Flow<DataState<Resources>> =
        flowStatus {
            getResultRestAPI(MappingResourcesRemoteAsModel()){
                apiService.getResources(prefEncryptionUtil.selectedLanguage)
            }
        }

    override suspend fun updateUserProfile(requestBody: UpdateProfileRequestBody): Flow<DataState<Any>> =
        flowStatus {
            getResultRestAPI {
                apiService.updateUserProfile(requestBody)
            }
        }

    override suspend fun updateInterestsUser(requestBody: UpdateInterestsUserRequestBody): Flow<DataState<Any>> =
        flowStatus {
            getResultRestAPI {
                apiService.updateInterestsUser(requestBody)
            }
        }

    override suspend fun getUserProfile(): Flow<DataState<User?>> =
        flowStatus {
            val user = prefEncryptionUtil.getModelData(PREF_USER, User::class.java)
            val resultRestAPI = getResultRestAPI(MappingUserProfileRemoteAsModel(user)) {
                apiService.getUserProfile()
            }
            resultRestAPI.data?.let {
                prefEncryptionUtil.setModelData(it, PREF_USER)
            }
            resultRestAPI
        }
}