package com.cancer.yaqeen.presentation.ui.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.domain.features.auth.login.usecases.RefreshTokenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    private val prefUtil: SharedPrefEncryptionUtil,
    private val refreshTokenUseCase: RefreshTokenUseCase
) : ViewModel() {

    private var viewModelJob: Job? = null

    fun userIsLogged() = prefUtil.isLogged

    fun refreshToken(requireContext: Context) {
        val isLoggedIn = prefUtil.isLogged
        if (isLoggedIn)
            viewModelJob = viewModelScope.launch(Dispatchers.IO) {
                refreshTokenUseCase(requireContext)
            }
    }

    override fun onCleared() {
        super.onCleared()
        viewModelJob?.cancel()
    }
}