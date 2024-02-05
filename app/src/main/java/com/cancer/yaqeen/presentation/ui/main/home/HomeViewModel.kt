package com.cancer.yaqeen.presentation.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.home.articles.models.Article
import com.cancer.yaqeen.data.features.home.articles.requests.BookmarkArticleRequest
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.articles.usecases.BookmarkArticleUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetBookmarkedArticlesUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.UnBookmarkArticleUseCase
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetMedicationRemindersFromNowUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUserProfileUseCase
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
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
                        response.data?.let {
                            val bookmarkedArticles = _viewStateBookmarkedArticles.value
                            val articles = injectBookmarkIdsToArticles(it, bookmarkedArticles)
                            _viewStateArticles.emit(articles)
                        }
                    }
                    else -> {}
                }
            }.launchIn(viewModelScope)
        }
    }

    fun getBookmarkedArticles() {
        if (!prefEncryptionUtil.isLogged)
            return
        viewModelScope.launch {
            getBookmarkedArticlesUseCase().onEach { response ->
//                _viewStateLoading.emit(response.loading)
                when (response.status) {
//                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            val articles = injectBookmarkIdsToArticles(_viewStateArticles.value, it)
                            if (articles.isNotEmpty()){
                                _viewStateArticles.emit(articles)
                            }
                            _viewStateBookmarkedArticles.emit(it)
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

    private fun injectBookmarkIdsToArticles(
        articles: List<Article>,
        bookmarkedArticles: List<Article>
    ): List<Article> {
        if (articles.isNotEmpty() && bookmarkedArticles.isNotEmpty()) {
            bookmarkedArticles.sortedBy { it.contentID }.onEach { bookmarkedArticle ->
                articles.sortedBy { it.contentID }.first {
                    it.contentID == bookmarkedArticle.contentID
                }.apply {
                    bookmarkID = bookmarkedArticle.bookmarkID
                    isFavorite = true
                }
//
//                articles.binarySearch() { article ->
//                    String.CASE_INSENSITIVE_ORDER.compare(
//                        article.contentID.toString(),
//                        bookmarkedArticle.contentID.toString()
//                    )
//                    article.contentID
//                }
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
            removeArticleFromFavourite(article)
        else
            addArticleToFavourite(article)
    }

    private fun addArticleToFavourite(article: Article) {
        viewModelJob = viewModelScope.launch {
            bookmarkArticleUseCase(
                BookmarkArticleRequest(article.contentID)
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        if (response.data == true)
                            _viewStateFavouriteStatusArticle.postValue(article to true)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun removeArticleFromFavourite(article: Article) {
        viewModelJob = viewModelScope.launch {
            unBookmarkArticleUseCase(
                article.bookmarkID ?: 0
            ).collect { response ->
                _viewStateLoading.emit(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        if (response.data == true)
                            _viewStateFavouriteStatusArticle.postValue(article to false)
                    }
                    else -> {}
                }
            }
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