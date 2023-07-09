package com.cancer.yaqeen.domain.features.auth.login.errors

sealed class LoginErrors : Exception() {

  object NullUserNameInput : LoginErrors()
  object NullPasswordInput : LoginErrors()

}