package com.cancer.yaqeen.presentation.ui.splash

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import com.cancer.yaqeen.domain.features.auth.login.usecases.RefreshTokenUseCase
import com.cancer.yaqeen.presentation.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    @ApplicationContext val _context: Context,
    private val prefUtil: SharedPrefEncryptionUtil,
    private val refreshTokenUseCase: RefreshTokenUseCase
) : BaseViewModel(context = _context, prefEncryptionUtil = prefUtil) {

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