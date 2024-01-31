package com.cancer.yaqeen.data.features.auth.models

data class Profile(
    var userType: UserType? = UserType.PATIENT,
    var cancerTypeId: Int? = null,
    var stageId: Int? = null,
    var interestModuleIds: MutableList<Int>? = null,
    var universityId: String? = null,
    var degreeId: String? = null,
    var medicalFieldId: String? = null,
    val agreedTerms: Boolean = false
)
