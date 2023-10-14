package com.cancer.yaqeen.data.features.onboarding.mappers

import com.auth0.android.result.UserProfile
import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.responses.LoginRemote
import com.cancer.yaqeen.data.features.onboarding.models.CancerType
import com.cancer.yaqeen.data.features.onboarding.models.Module
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.models.Stage
import com.cancer.yaqeen.data.features.onboarding.responses.CancerStageResponse
import com.cancer.yaqeen.data.features.onboarding.responses.CancerTypeResponse
import com.cancer.yaqeen.data.features.onboarding.responses.InterestResponse
import com.cancer.yaqeen.data.features.onboarding.responses.ResourcesResponse


class MappingResourcesRemoteAsModel: Mapper<ResourcesResponse, Resources> {
    override fun map(input: ResourcesResponse): Resources = input.run {
        Resources(
            stages = cancerStages.map { MappingStageRemoteAsModel().map(it) },
            cancerTypes = cancerTypes.map { MappingCancerTypeRemoteAsModel().map(it) },
            patientInterests = patientInterests.map { MappingInterestRemoteAsModel().map(it) },
            doctorInterests = doctorInterests.map { MappingInterestRemoteAsModel().map(it) }
        )
    }
}
class MappingStageRemoteAsModel: Mapper<CancerStageResponse, Stage> {
    override fun map(input: CancerStageResponse): Stage = input.run {
        Stage(
            id = stageID,
            number = 0,
            icon = logoURL,
            stageName = translations.firstOrNull()?.translation?.stageName ?: ""
        )
    }
}
class MappingCancerTypeRemoteAsModel: Mapper<CancerTypeResponse, CancerType> {
    override fun map(input: CancerTypeResponse): CancerType = input.run {
        CancerType(
            id = cancerID,
            icon = logoURL,
            typeName = translations.firstOrNull()?.translation?.cancerTypeName ?: ""
        )
    }
}
class MappingInterestRemoteAsModel: Mapper<InterestResponse, Module> {
    override fun map(input: InterestResponse): Module = input.run {
        Module(
            id = interestID,
            icon = logoURL,
            moduleName = translations.firstOrNull()?.translation?.name ?: ""
        )
    }
}
