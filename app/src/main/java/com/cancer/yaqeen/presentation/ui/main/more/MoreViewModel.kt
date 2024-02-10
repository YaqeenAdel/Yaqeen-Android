package com.cancer.yaqeen.presentation.ui.main.more

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.onboarding.models.Language
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.domain.features.auth.login.usecases.LogoutUseCase
import com.cancer.yaqeen.domain.features.home.articles.usecases.RemoveBookmarkedArticlesUseCase
import com.cancer.yaqeen.presentation.util.SingleLiveEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil,
    private val logoutUseCase: LogoutUseCase,
    private val removeBookmarkedArticlesUseCase: RemoveBookmarkedArticlesUseCase,
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateUser = MutableStateFlow<Pair<User?, Boolean>>(null to false)
    val viewStateUser = _viewStateUser.asStateFlow()

    private val _viewStateLogoutSuccess = SingleLiveEvent<Boolean?>()
    val viewStateLogoutSuccess: LiveData<Boolean?> = _viewStateLogoutSuccess

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

    fun selectedLanguageIsEnglish() =
        prefEncryptionUtil.selectedLanguageIsEnglish()

    fun switchLanguage() {
        val lang = if (selectedLanguageIsEnglish())
            Language.ARABIC.lang
        else
            Language.ENGLISH.lang
        viewModelScope.launch {
            prefEncryptionUtil.selectedLanguage = lang
        }
    }

    fun logout(context: Context) {
        viewModelScope.launch {
            logoutUseCase(context).onEach { response ->
                when (response.status) {
//                    Status.ERROR -> emitError(response.errorEntity)
                    Status.SUCCESS -> {
                        response.data?.let {
                            _viewStateLogoutSuccess.postValue(it)
                            if (it){
                                removeBookmarkedArticles()
                            }
                        }
                    }

                    else -> {}
                }
            }.catch {

            }.launchIn(viewModelScope)
        }
    }

    private fun removeBookmarkedArticles(){
        viewModelJob = viewModelScope.launch {
            removeBookmarkedArticlesUseCase().collect()
        }
    }
    override fun onCleared() {
        super.onCleared()
        viewModelJob = null
    }

}