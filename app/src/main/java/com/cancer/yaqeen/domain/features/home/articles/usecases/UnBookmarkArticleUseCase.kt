package com.cancer.yaqeen.domain.features.home.articles.usecases

import com.cancer.yaqeen.data.features.home.articles.IHomeRepository
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnBookmarkArticleUseCase @Inject constructor(private val repository: IHomeRepository) {
    suspend operator fun invoke(
        bookmarkId: Int
    ): Flow<DataState<Boolean>> =
        repository.unBookmarkArticle(
            bookmarkId
        )
}