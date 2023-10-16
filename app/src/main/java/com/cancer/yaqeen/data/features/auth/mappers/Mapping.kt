package com.cancer.yaqeen.data.features.auth.mappers

import com.auth0.android.result.UserProfile
import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.responses.LoginRemote


class MappingLoginRemoteAsUser: Mapper<UserProfile, User> {
    override fun map(input: UserProfile): User = input.run {
        User(
            id = getId() ?: "",
            name = name ?: "",
            nickname = nickname ?: "",
            pictureURL = pictureURL ?: "",
            email = email ?: "",
            isEmailVerified = isEmailVerified ?: false,
            familyName = familyName ?: ""
        )
    }
}
