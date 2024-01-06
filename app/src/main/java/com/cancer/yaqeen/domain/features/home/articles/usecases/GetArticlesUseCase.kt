package com.cancer.yaqeen.domain.features.home.articles.usecases

import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.home.IHomeRepository
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetArticlesUseCase @Inject constructor(private val repository: IHomeRepository) {
    suspend operator fun invoke(): Flow<DataState<Resources>> =
        repository.getHomeArticles()
}