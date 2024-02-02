package com.cancer.yaqeen.domain.features.home.articles.usecases

import com.cancer.yaqeen.data.features.home.articles.IHomeRepository
import com.cancer.yaqeen.data.features.home.articles.requests.BookmarkArticleRequest
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkArticleUseCase @Inject constructor(private val repository: IHomeRepository) {
    suspend operator fun invoke(
        request: BookmarkArticleRequest
    ): Flow<DataState<Boolean>> =
        repository.bookmarkArticle(
            request
        )
}