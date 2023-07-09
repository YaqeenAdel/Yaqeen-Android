package com.cancer.yaqeen.data.features.auth

import com.cancer.yaqeen.data.base.Resource
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.requests.LoginRequestBody
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    suspend fun login(requestBody: LoginRequestBody): Flow<Resource<User>>
}