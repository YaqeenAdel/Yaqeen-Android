package com.cancer.yaqeen.data.features.home

import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.home.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateInterestsUserRequestBody
import com.cancer.yaqeen.data.features.onboarding.requests.UpdateProfileRequestBody
import kotlinx.coroutines.flow.Flow

interface IHomeRepository {
     suspend fun getHomeArticles(langCode: String): Flow<DataState<HomeArticlesResponse>>

}