package com.cancer.yaqeen.data.features.home

import com.cancer.yaqeen.data.features.home.mappers.MappingBookmarkArticleRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.mappers.MappingArticlesRemoteAsModel
import com.cancer.yaqeen.data.features.home.mappers.MappingBookmarkedArticlesRemoteAsModel
import com.cancer.yaqeen.data.features.home.mappers.MappingSavedArticlesRemoteAsModel
import com.cancer.yaqeen.data.features.home.mappers.MappingUnBookmarkArticleRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.models.Article
import com.cancer.yaqeen.data.features.home.models.Bookmark
import com.cancer.yaqeen.data.features.home.requests.BookmarkArticleRequest
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.base.flowStatus
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: YaqeenAPI,
    errorHandler: ErrorHandlerImpl,
    private val prefEncryptionUtil: SharedPrefEncryptionUtil
): BaseDataSource(errorHandler), IHomeRepository {


    override suspend fun getHomeArticles(searchQuery: String): Flow<DataState<List<Article>>> =
        flowStatus {
            getResultRestAPI(MappingArticlesRemoteAsModel()){
                apiService.getHomeArticles(prefEncryptionUtil.selectedLanguage, "%$searchQuery%")
            }
        }

    override suspend fun getBookmarkedArticles(): Flow<DataState<List<Article>>> =
        flowStatus {
            getResultRestAPI(MappingSavedArticlesRemoteAsModel()){
                apiService.getBookmarkedArticles()
            }
        }

    override suspend fun bookmarkArticle(request: BookmarkArticleRequest): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingBookmarkArticleRemoteAsUIModel()){
                apiService.bookmarkArticle(request)
            }
        }

    override suspend fun unBookmarkArticle(bookmarkId: Int): Flow<DataState<Boolean>> =
        flowStatus {
            getResultRestAPI(MappingUnBookmarkArticleRemoteAsUIModel()){
                apiService.unBookmarkArticle(bookmarkId)
            }
        }


}