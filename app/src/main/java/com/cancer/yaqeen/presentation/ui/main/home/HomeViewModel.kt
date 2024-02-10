package com.cancer.yaqeen.presentation.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.home.articles.models.Article
import com.cancer.yaqeen.data.features.home.articles.room.Article as LocalArticle
import com.cancer.yaqeen.data.features.home.articles.requests.BookmarkArticleRequest
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.articles.usecases.BookmarkArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetBookmarkedArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetLocalBookmarkedArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.RemoveBookmarkedArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.SaveArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.UnBookmarkArticleUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetMedicationRemindersFromNowUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUserProfileUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getArticlesUseCase: GetArticlesUseCase,
    private val getBookmarkedArticlesUseCase: GetBookmarkedArticlesUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase,
    private val unBookmarkArticleUseCase: UnBookmarkArticleUseCase,
    private val getMedicationRemindersFromNowUseCase: GetMedicationRemindersFromNowUseCase,
    private val getLocalBookmarkedArticlesUseCase: GetLocalBookmarkedArticlesUseCase,
    private val removeBookmarkedArticleUseCase: RemoveBookmarkedArticleUseCase,
    private val saveArticleUseCase: SaveArticleUseCase,
//    private val removeArticleFromFavouriteUseCase: RemoveArticleFromFavouriteUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateMedications = MutableStateFlow<List<Medication>>(listOf())
    val viewStateMedications = _viewStateMedications.asStateFlow()

    private val _viewStateArticles = MutableStateFlow<List<Article>>(listOf())
    val viewStateArticles = _viewStateArticles.asStateFlow()

    private val _viewStateBookmarkedArticles = MutableStateFlow<List<Article>>(listOf())
    val viewStateBookmarkedArticles = _viewStateBookmarkedArticles.asStateFlow()

    private val _viewStateUser = MutableStateFlow<Pair<User?, Boolean>>(null to false)
    val viewStateUser = _viewStateUser.asStateFlow()

    private val _viewStateFavouriteStatusArticle = SingleLiveEvent<Pair<Article, Boolean>?>()
    val viewStateFavouriteStatusArticle: LiveData<Pair<Article, Boolean>?> = _viewStateFavouriteStatusArticle

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()


    fun getMedicationsFromNow() {
        viewModelJob = viewModelScope.launch {
            getMedicationRemindersFromNowUseCase(
                scheduleType = ScheduleType.MEDICATION.scheduleType
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateMedications.emit(it)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun getArticles(searchQuery: String = "") {
        viewModelScope.launch {
            getArticlesUseCase(searchQuery).onEach { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        if (response.data?.isNotEmpty() == true){
                            if (prefEncryptionUtil.isLogged){
                                getLocalBookmarkedArticlesUseCase().onEach { responseLocal ->
                                    when (responseLocal.status) {
                                        Status.ERROR, Status.SUCCESS -> {
                                            if(responseLocal.data.isNullOrEmpty()) {
                                                getBookmarkedArticles(response.data)
                                            }
                                            else {
                                                val articles = injectLocalArticlesToArticles(response.data, responseLocal.data)
                                                _viewStateArticles.emit(articles)
                                            }
                                        }
                                        else -> {}
                                    }
                                }.launchIn(viewModelScope)
                            }else{
                                _viewStateArticles.emit(response.data)
                            }
                        }
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun getBookmarkedArticles(articles: List<Article>) {
        viewModelScope.launch {
            getBookmarkedArticlesUseCase().onEach { response ->
                when (response.status) {
                    Status.ERROR -> _viewStateArticles.emit(articles)
                    Status.SUCCESS -> {
                        if (response.data?.isNotEmpty() == true){
                            val articles = injectBookmarkedArticlesToArticles(articles, response.data)
                            _viewStateArticles.emit(articles)
                        }else {
                            _viewStateArticles.emit(articles)
                        }
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getSavedArticles() {
        viewModelScope.launch {
            getBookmarkedArticlesUseCase().onEach { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateArticles.emit(it)
                        }
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    private fun injectLocalArticlesToArticles(
        articles: List<Article>,
        bookmarkedArticles: List<LocalArticle>
    ): List<Article> {
        bookmarkedArticles.sortedBy { it.articleID }.onEach { bookmarkedArticle ->
            articles.sortedBy { it.contentID }.first {
                it.contentID == bookmarkedArticle.articleID
            }.apply {
                bookmarkID = bookmarkedArticle.bookmarkID
                isFavorite = true
            }
        }
        return articles
    }

    private fun injectBookmarkedArticlesToArticles(
        articles: List<Article>,
        bookmarkedArticles: List<Article>
    ): List<Article> {
        bookmarkedArticles.sortedBy { it.contentID }.onEach { bookmarkedArticle ->
            articles.sortedBy { it.contentID }.first {
                it.contentID == bookmarkedArticle.contentID
            }.apply {
                bookmarkID = bookmarkedArticle.bookmarkID
                isFavorite = true
            }
        }
        return articles
    }

    fun getUserInfo(){
        viewModelScope.launch {
            val isLoggedIn = prefEncryptionUtil.isLogged
            val user = prefEncryptionUtil.getModelData(
                SharedPrefEncryptionUtil.PREF_USER,
                User::class.java
            )

            _viewStateUser.emit(user to isLoggedIn)
        }
    }

    fun getUser(): User? =
        prefEncryptionUtil.getModelData(
            SharedPrefEncryptionUtil.PREF_USER,
            User::class.java
        )

    fun userIsLoggedIn() =
        prefEncryptionUtil.isLogged

    fun changeFavouriteStatusArticle(article: Article) {
        if (article.isFavorite)
            unBookmarkArticle(article)
        else
            bookmarkArticle(article)
    }

    private fun bookmarkArticle(article: Article) {
        viewModelJob = viewModelScope.launch {
            bookmarkArticleUseCase(
                BookmarkArticleRequest(article.contentID)
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateFavouriteStatusArticle.postValue(article.apply { bookmarkID = it } to true)
                            saveArticle(article.apply { bookmarkID = it })
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun saveArticle(article: Article){
        viewModelJob = viewModelScope.launch {
            saveArticleUseCase(
                LocalArticle(articleID = article.contentID, bookmarkID = article.bookmarkID)
            ).collect()
        }
    }

    private fun unBookmarkArticle(article: Article) {
        viewModelJob = viewModelScope.launch {
            unBookmarkArticleUseCase(
                article.bookmarkID ?: 0
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        if (response.data == true) {
                            _viewStateFavouriteStatusArticle.postValue(article.apply { bookmarkID = null } to false)
                            removeBookmarkedArticle(articleID = article.contentID)
                        }
                    }
                    else -> {}
                }
            }
        }
    }

    private fun removeBookmarkedArticle(articleID: Int){
        viewModelJob = viewModelScope.launch {
            removeBookmarkedArticleUseCase(
                articleID = articleID
            ).collect()
        }
    }

    private suspend fun emitError(errorEntity: ErrorEntity?) {
        _viewStateError.emit(errorEntity)
        _viewStateError.emit(null)
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob = null
    }
}