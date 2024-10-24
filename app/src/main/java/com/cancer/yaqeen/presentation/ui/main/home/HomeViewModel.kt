package com.cancer.yaqeen.presentation.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.home.articles.models.Article
import com.cancer.yaqeen.data.features.home.articles.room.Article as LocalArticle
import com.cancer.yaqeen.data.features.home.articles.requests.BookmarkArticleRequest
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Schedule
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
import com.cancer.yaqeen.domain.features.home.schedule.medication.GetTodayRemindersUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUserProfileUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val getUserProfileUseCase: GetUserProfileUseCase,
    private val getArticlesUseCase: GetArticlesUseCase,
    private val getBookmarkedArticlesUseCase: GetBookmarkedArticlesUseCase,
    private val bookmarkArticleUseCase: BookmarkArticleUseCase,
    private val unBookmarkArticleUseCase: UnBookmarkArticleUseCase,
    private val getTodayRemindersUseCase: GetTodayRemindersUseCase,
    private val getLocalBookmarkedArticlesUseCase: GetLocalBookmarkedArticlesUseCase,
    private val removeBookmarkedArticleUseCase: RemoveBookmarkedArticleUseCase,
    private val saveArticleUseCase: SaveArticleUseCase,
//    private val removeArticleFromFavouriteUseCase: RemoveArticleFromFavouriteUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val defaultDispatcher = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _viewStateSchedules = MutableStateFlow<List<Schedule>>(listOf())
    val viewStateScheduleS = _viewStateSchedules.asStateFlow()

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


    fun getTodayReminders() {
        if(!userIsLoggedIn())
            return

        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            getTodayRemindersUseCase().collect { response ->
                emitLoading(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateSchedules.emit(it)
                        }
                    }

                    else -> {}
                }
            }
        }
    }

    fun getArticles(searchQuery: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            getArticlesUseCase(searchQuery).onEach { response ->
                emitLoading(response.loading)
                when (response.status) {
                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        if (response.data?.isNotEmpty() == true){
                            if (prefEncryptionUtil.isLogged){
                                getLocalBookmarkedArticlesUseCase().onEach { responseLocal ->
                                    emitLoading(response.loading)
                                    when (responseLocal.status) {
                                        Status.ERROR, Status.SUCCESS -> {
                                            if(responseLocal.data.isNullOrEmpty()) {
                                                getBookmarkedArticles(response.data)
                                            }
                                            else {
                                                injectLocalArticlesToArticles(response.data, responseLocal.data)
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
        viewModelScope.launch(Dispatchers.IO) {
            getBookmarkedArticlesUseCase().onEach { response ->
                emitLoading(response.loading)
                when (response.status) {
                    Status.ERROR -> _viewStateArticles.emit(articles)
                    Status.SUCCESS -> {
                        if (response.data?.isNotEmpty() == true){
                            injectBookmarkedArticlesToArticles(articles, response.data)
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
        viewModelScope.launch(Dispatchers.IO) {
            getBookmarkedArticlesUseCase().onEach { response ->
                emitLoading(response.loading)
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
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkedArticles.sortedBy { it.articleID }.onEach { bookmarkedArticle ->
                articles.sortedBy { it.contentID }.first {
                    it.contentID == bookmarkedArticle.articleID
                }.apply {
                    bookmarkID = bookmarkedArticle.bookmarkID
                    isFavorite = true
                }
            }
            _viewStateArticles.emit(articles)
        }
    }

    private fun injectBookmarkedArticlesToArticles(
        articles: List<Article>,
        bookmarkedArticles: List<Article>
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            bookmarkedArticles.sortedBy { it.contentID }.onEach { bookmarkedArticle ->
                articles.sortedBy { it.contentID }.first {
                    it.contentID == bookmarkedArticle.contentID
                }.apply {
                    bookmarkID = bookmarkedArticle.bookmarkID
                    isFavorite = true
                }
            }
            _viewStateArticles.emit(articles)
        }
    }

    fun getUserInfo(){
        viewModelScope.launch(Dispatchers.IO) {
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
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            bookmarkArticleUseCase(
                BookmarkArticleRequest(article.contentID)
            ).collect { response ->
                emitLoading(response.loading)
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
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            saveArticleUseCase(
                LocalArticle(articleID = article.contentID, bookmarkID = article.bookmarkID)
            ).collect()
        }
    }

    private fun unBookmarkArticle(article: Article) {
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            unBookmarkArticleUseCase(
                article.bookmarkID ?: 0
            ).collect { response ->
                emitLoading(response.loading)
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
        viewModelJob = viewModelScope.launch(Dispatchers.IO) {
            removeBookmarkedArticleUseCase(
                articleID = articleID
            ).collect()
        }
    }

    private suspend fun emitLoading(isLoading: Boolean) {
        withContext(Dispatchers.Main) {
            _viewStateLoading.emit(isLoading)
        }
    }

    private suspend fun emitError(errorEntity: ErrorEntity?) {
        withContext(Dispatchers.Main) {
            _viewStateError.emit(errorEntity)
            _viewStateError.emit(null)
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob = null
    }
}