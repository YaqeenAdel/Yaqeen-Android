package com.cancer.yaqeen.presentation.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel
@Inject constructor(
    private val prefUtil: SharedPrefEncryptionUtil
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

    override fun onCleared() {
        super.onCleared()
        viewModelJob?.cancel()
    }
}