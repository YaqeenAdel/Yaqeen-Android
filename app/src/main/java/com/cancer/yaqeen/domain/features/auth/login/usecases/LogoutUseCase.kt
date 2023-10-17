package com.cancer.yaqeen.domain.features.auth.login.usecases

import com.cancer.yaqeen.data.network.base.DataState
import android.content.Context
import com.cancer.yaqeen.data.features.auth.IAuthRepository
import com.cancer.yaqeen.data.features.auth.models.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutUseCase @Inject constructor(private val repository: IAuthRepository) {
    suspend operator fun invoke(context: Context): Flow<DataState<Boolean>> =
        repository.logout(context)
}