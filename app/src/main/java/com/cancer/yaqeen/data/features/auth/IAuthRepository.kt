package com.cancer.yaqeen.data.features.auth

import com.cancer.yaqeen.data.network.base.DataState
import android.content.Context
import com.cancer.yaqeen.data.features.auth.models.User
import kotlinx.coroutines.flow.Flow

interface IAuthRepository {
    suspend fun login(context: Context): Flow<DataState<User>>
    suspend fun logout(context: Context): Flow<DataState<Boolean>>
}