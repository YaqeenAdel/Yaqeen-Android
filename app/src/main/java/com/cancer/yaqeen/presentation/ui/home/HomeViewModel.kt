package com.cancer.yaqeen.presentation.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.home.models.Article
import com.cancer.yaqeen.data.features.home.requests.AddArticleToFavouriteRequest
import com.cancer.yaqeen.data.features.home.requests.RemoveArticleFromFavouriteRequest
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.data.network.error.ErrorEntity
import com.cancer.yaqeen.domain.features.home.articles.usecases.AddArticleToFavouriteUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.GetArticlesUseCase
import com.cancer.yaqeen.domain.features.onboarding.usecases.GetUserProfileUseCase
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
    private val addArticleToFavouriteUseCase: AddArticleToFavouriteUseCase,
//    private val removeArticleFromFavouriteUseCase: RemoveArticleFromFavouriteUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateArticles = MutableStateFlow<List<Article>>(listOf())
    val viewStateArticles = _viewStateArticles.asStateFlow()

    private val _viewStateUser = MutableStateFlow<Pair<User?, Boolean>>(null to false)
    val viewStateUser = _viewStateUser.asStateFlow()

    private val _viewStateFavouriteStatusArticle = SingleLiveEvent<Pair<Article, Boolean>?>()
    val viewStateFavouriteStatusArticle: LiveData<Pair<Article, Boolean>?> = _viewStateFavouriteStatusArticle

    private val _viewStateLoading = MutableStateFlow<Boolean>(false)
    val viewStateLoading = _viewStateLoading.asStateFlow()

    private val _viewStateError = MutableStateFlow<ErrorEntity?>(null)
    val viewStateError = _viewStateError.asStateFlow()


    fun getArticles(searchQuery: String = "") {
        viewModelScope.launch {
            getArticlesUseCase(searchQuery).onEach { response ->
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

    fun getUserInfo(){
        viewModelScope.launch {
            val isLoggedIn = prefEncryptionUtil.isLogged
            val user = prefEncryptionUtil.getModelData(
                SharedPrefEncryptionUtil.PREF_USER,
                User::class.java
            )

            Log.d("TAG", "getUserInfo: $isLoggedIn / $user")
            _viewStateUser.emit(user to isLoggedIn)
        }
    }

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
            addArticleToFavouriteUseCase(
                AddArticleToFavouriteRequest(article.contentID)
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
//        viewModelJob = viewModelScope.launch {
//            removeArticleFromFavouriteUseCase(
//                RemoveArticleFromFavouriteRequest(article.contentID)
//            ).collect { response ->
//                _viewStateLoading.emit(response.loading)
//                when (response.status) {
//                    Status.ERROR -> emitError(response.errorEntity)
//                    Status.SUCCESS -> {
//                        if (response.data == true)
//                            _viewStateFavouriteStatusArticle.emit(article to false)
//
//                        _viewStateFavouriteStatusArticle.emit(null)
//                    }
//                    else -> {}
//                }
//            }
//        }
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