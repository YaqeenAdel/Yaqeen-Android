package com.cancer.yaqeen.data.features.auth.models

data class Profile(
    var userType: UserType? = null,
    var cancerTypeId: Int? = null,
    var stageId: Int? = null,
    var interestModuleId: Int? = null,
    var universityId: String? = null,
    var degreeId: String? = null,
    var medicalFieldId: String? = null,
    val agreedTerms: Boolean = false
)