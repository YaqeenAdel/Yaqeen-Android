package com.cancer.yaqeen.domain.features.auth.login.usecases

import android.content.Context
import android.text.TextUtils
import android.util.Log
import com.cancer.yaqeen.data.base.Resource
import com.cancer.yaqeen.data.features.auth.IAuthRepository
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import com.cancer.yaqeen.domain.features.auth.login.errors.LoginErrors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginUseCase @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(context: Context): Flow<Resource<User>> =
        repository.login(context)
}