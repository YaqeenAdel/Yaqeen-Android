package com.cancer.yaqeen.data.features.onboarding

import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.network.base.flowStatus
import com.cancer.yaqeen.data.features.onboarding.mappers.MappingResourcesRemoteAsModel
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val apiService: YaqeenAPI,
    errorHandler: ErrorHandlerImpl
): BaseDataSource(errorHandler), IOnboardingRepository {

    override suspend fun getResources(): Flow<DataState<Resources>> =
        flowStatus {
            getResultRestAPI(MappingResourcesRemoteAsModel()){
                apiService.getResources()
            }
        }
}