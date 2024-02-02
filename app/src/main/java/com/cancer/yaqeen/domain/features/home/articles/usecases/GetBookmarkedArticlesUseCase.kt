package com.cancer.yaqeen.domain.features.home.articles.usecases

import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.features.home.articles.IHomeRepository
import com.cancer.yaqeen.data.features.home.articles.models.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookmarkedArticlesUseCase @Inject constructor(private val repository: IHomeRepository) {
    suspend operator fun invoke(): Flow<DataState<List<Article>>> =
        repository.getBookmarkedArticles()
}