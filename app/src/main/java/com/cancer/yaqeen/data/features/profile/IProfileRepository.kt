package com.cancer.yaqeen.data.features.profile

import com.cancer.yaqeen.data.network.base.DataState
import android.content.Context
import com.cancer.yaqeen.data.features.auth.models.User
import kotlinx.coroutines.flow.Flow

interface IProfileRepository {
    suspend fun GetCurrentUser(context: Context): Flow<DataState<User>>
 }