package com.cancer.yaqeen.domain.features.home.articles.usecases

import com.cancer.yaqeen.data.features.home.IHomeRepository
import com.cancer.yaqeen.data.features.home.requests.AddArticleToFavouriteRequest
import com.cancer.yaqeen.data.network.base.DataState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddArticleToFavouriteUseCase @Inject constructor(private val repository: IHomeRepository) {
    suspend operator fun invoke(
        request: AddArticleToFavouriteRequest
    ): Flow<DataState<Boolean>> =
        repository.addArticleToFavourite(
            request
        )
}