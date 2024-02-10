package com.cancer.yaqeen.domain.features.home.articles.usecases

import com.cancer.yaqeen.data.features.home.articles.IHomeRepository
import com.cancer.yaqeen.data.features.home.articles.room.Article
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RemoveBookmarkedArticleUseCase @Inject constructor(private val repository: IHomeRepository) {
    suspend operator fun invoke(articleID: Int): Flow<DataState<Int>> =
        repository.removeBookmarkedArticle(articleID)
}