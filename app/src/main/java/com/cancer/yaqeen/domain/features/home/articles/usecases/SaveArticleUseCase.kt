package com.cancer.yaqeen.domain.features.home.articles.usecases

import com.cancer.yaqeen.data.features.home.articles.IHomeRepository
import com.cancer.yaqeen.data.features.home.articles.room.Article
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveArticleUseCase @Inject constructor(private val repository: IHomeRepository) {
    suspend operator fun invoke(article: Article): Flow<DataState<Long>> =
        repository.saveBookmarkedArticle(article)
}