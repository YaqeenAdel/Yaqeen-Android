package com.cancer.yaqeen.domain.features.profile

import com.cancer.yaqeen.data.network.base.DataState
import android.content.Context
import com.cancer.yaqeen.data.features.auth.IAuthRepository
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.profile.IProfileRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetProfileUseCase @Inject constructor(private val repository: IProfileRepository) {
    suspend operator fun invoke(context: Context): Flow<DataState<User>> =
        repository.GetCurrentUser(context)
}