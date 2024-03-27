package com.cancer.yaqeen.presentation.ui.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.domain.features.auth.login.usecases.RefreshTokenUseCase
import com.cancer.yaqeen.domain.features.auth.login.usecases.RefreshTokenUseCase2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    private val prefUtil: SharedPrefEncryptionUtil,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val refreshTokenUseCase2: RefreshTokenUseCase2
) : ViewModel() {

    private val _viewStateUserInfo = MutableStateFlow<Boolean?>(null)
    val viewStateUserInfo = _viewStateUserInfo.asStateFlow()

    private var viewModelJob: Job? = null

    fun checkUserInfo() {
        val isLoggedIn = prefUtil.isLogged
        viewModelJob = viewModelScope.launch {
            _viewStateUserInfo.emit(isLoggedIn)
        }

    }

    fun refreshToken(requireContext: Context) {
        viewModelJob = viewModelScope.launch {
            refreshTokenUseCase2(requireContext)
//            refreshTokenUseCase().onEach { response ->
//
//            }.launchIn(viewModelScope)
        }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob?.cancel()
    }
}