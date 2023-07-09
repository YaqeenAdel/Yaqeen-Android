package com.cancer.yaqeen.data.features.auth.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.responses.LoginRemote


class MappingLoginRemoteAsUser: Mapper<LoginRemote, User> {
    override fun map(input: LoginRemote): User = input.user.run {
        User(
            fullName = fullName,
            email = email,
            mobileNumber = mobileNumber,
            userName = userName
        )
    }
}
