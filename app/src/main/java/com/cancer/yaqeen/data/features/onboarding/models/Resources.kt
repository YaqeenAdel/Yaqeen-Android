package com.cancer.yaqeen.data.features.onboarding.models

data class Resources(
    val stages: List<Stage>,
    val cancerTypes: List<CancerType>,
    val patientInterests: List<Module>,
    val doctorInterests: List<Module>
)
