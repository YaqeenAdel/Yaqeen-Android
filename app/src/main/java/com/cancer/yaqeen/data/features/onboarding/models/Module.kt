package com.cancer.yaqeen.data.features.onboarding.models

data class Module(
    val id: Int,
    val icon: String = "",
    val iconColor: String = "",
    val moduleName: String,
    var selected: Boolean = false
)
