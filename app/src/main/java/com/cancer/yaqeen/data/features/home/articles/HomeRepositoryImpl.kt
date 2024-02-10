package com.cancer.yaqeen.data.features.home.articles

import com.cancer.yaqeen.data.features.home.articles.mappers.MappingArticlesAsLocalModel
import com.cancer.yaqeen.data.features.home.articles.mappers.MappingBookmarkArticleRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.articles.mappers.MappingArticlesRemoteAsModel
import com.cancer.yaqeen.data.features.home.articles.mappers.MappingSavedArticlesRemoteAsModel
import com.cancer.yaqeen.data.features.home.articles.mappers.MappingUnBookmarkArticleRemoteAsUIModel
import com.cancer.yaqeen.data.features.home.articles.models.Article
import com.cancer.yaqeen.data.features.home.articles.room.Article as LocalArticle
import com.cancer.yaqeen.data.features.home.articles.requests.BookmarkArticleRequest
import com.cancer.yaqeen.data.network.base.BaseDataSource
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.apis.YaqeenAPI
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.base.flowStatus
import com.cancer.yaqeen.data.network.error.ErrorHandlerImpl
import com.cancer.yaqeen.data.room.YaqeenDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HomeRepositoryImpl @Inject constructor(
    private val apiService: YaqeenAPI,
    private val yaqeenDao: YaqeenDao,
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
            val resultRestAPI = getResultRestAPI(MappingSavedArticlesRemoteAsModel()) {
                apiService.getBookmarkedArticles()
            }

            if (resultRestAPI.status == Status.SUCCESS){
                resultRestAPI.data?.let {
                    saveBookmarkedArticles(resultRestAPI.data)
                }
            }

            resultRestAPI
        }

    override suspend fun getLocalBookmarkedArticles(): Flow<DataState<List<LocalArticle>>> =
        flowStatus {
            getResultDao { yaqeenDao.getArticles() }
        }

    override suspend fun saveBookmarkedArticles(articles: List<Article>): DataState<List<Long>> {
        removeBookmarkedArticles()
        return getResultDao { yaqeenDao.insertArticles(
            MappingArticlesAsLocalModel().map(articles)
        ) }
    }

    override suspend fun removeBookmarkedArticles(): Flow<DataState<Int>> =
        flowStatus {
            getResultDao { yaqeenDao.removeArticles() }
        }


    override suspend fun saveBookmarkedArticle(article: LocalArticle): Flow<DataState<Long>> =
        flowStatus {
            getResultDao { yaqeenDao.insertArticle(article) }
        }


    override suspend fun removeBookmarkedArticle(articleID: Int): Flow<DataState<Int>> =
        flowStatus {
            getResultDao { yaqeenDao.removeArticle(articleID) }
        }


    override suspend fun bookmarkArticle(request: BookmarkArticleRequest): Flow<DataState<Int?>> =
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