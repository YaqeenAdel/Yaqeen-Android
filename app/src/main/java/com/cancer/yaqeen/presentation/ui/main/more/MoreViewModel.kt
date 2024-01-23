package com.cancer.yaqeen.presentation.ui.main.more

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.onboarding.models.Language
import com.cancer.yaqeen.data.local.SharedPrefEncryptionUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MoreViewModel @Inject constructor(
    private val prefEncryptionUtil: SharedPrefEncryptionUtil
) : ViewModel() {

    private var viewModelJob: Job? = null

    private val _viewStateUser = MutableStateFlow<Pair<User?, Boolean>>(null to false)
    val viewStateUser = _viewStateUser.asStateFlow()

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

    override fun onCleared() {
        super.onCleared()
        viewModelJob = null
    }

    fun logOut() {
        prefEncryptionUtil.clearUserPreferenceStorage()
    }
}