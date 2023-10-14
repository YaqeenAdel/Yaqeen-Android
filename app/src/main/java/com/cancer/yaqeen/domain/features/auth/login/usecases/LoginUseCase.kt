package com.cancer.yaqeen.domain.features.auth.login.usecases

import com.cancer.yaqeen.data.network.base.DataState
import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.cancer.yaqeen.data.base.Resource
import com.cancer.yaqeen.data.features.auth.IAuthRepository
import com.cancer.yaqeen.data.features.auth.models.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(context: Context): Flow<DataState<User>> =
        repository.login(context)
}