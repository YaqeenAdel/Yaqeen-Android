package com.cancer.yaqeen.data.features.onboarding.requests

import com.google.gson.annotations.SerializedName

data class UpdateInterestsUserRequestBody(
    val interests: List<InterestRequestBody>
)
data class InterestRequestBody (
    @SerializedName("InterestsInterestId")
    val interestID: Int
)

data class UpdateInterestsUserRequestBuilder(
    val interestID: Int?
){
    fun buildRequestBody(): UpdateInterestsUserRequestBody =
        UpdateInterestsUserRequestBody(
            listOf(
                InterestRequestBody(
                    interestID ?: 0
                )
            )
        )
}
