package com.cancer.yaqeen.data.features.onboarding

import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.home.IHomeRepository
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.network.base.flowStatus
import com.cancer.yaqeen.data.features.home.mappers.MappingResourcesRemoteAsModel
import com.cancer.yaqeen.data.features.home.mappers.MappingUniversitiesRemoteAsModel
import com.cancer.yaqeen.data.features.home.mappers.MappingUserProfileRemoteAsModel
import com.cancer.yaqeen.data.features.home.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateInterestsUserRequestBody
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil.Companion.PREF_USER
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: YaqeenAPI,
    errorHandler: ErrorHandlerImpl,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IHomeRepository {

    override suspend fun getResources(): Flow<DataState<Resources>> =
        flowStatus {
            getResultRestAPI(MappingResourcesRemoteAsModel()){
                apiService.getResources(prefEncryptionUtil.selectedLanguage)
            }
        }


    override suspend fun getHomeArticles(langCode: String): Flow<DataState<HomeArticlesResponse>> {
        flowStatus {
            getResultRestAPI(MappingResourcesRemoteAsModel()){
                apiService.getResources(prefEncryptionUtil.selectedLanguage)
            }
        }
    }
}