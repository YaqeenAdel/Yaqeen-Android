package com.cancer.yaqeen.presentation.ui.profile

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import com.cancer.yaqeen.data.network.base.DataState
import com.cancer.yaqeen.data.network.base.Status
import com.cancer.yaqeen.domain.features.auth.login.usecases.LoginUseCase
import com.cancer.yaqeen.domain.features.profile.GetProfileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getProfileUseCase: GetProfileUseCase
): ViewModel() {
     suspend fun getCurrentUser(context: Context): Flow<DataState<User>> = getProfileUseCase(context)
}