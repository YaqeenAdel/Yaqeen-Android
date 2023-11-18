package com.cancer.yaqeen.data.features.onboarding.mappers

import com.auth0.android.result.UserProfile
import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.auth.models.Aggregate
import com.cancer.yaqeen.data.features.auth.models.Doctor
import com.cancer.yaqeen.data.features.auth.models.Patient
import com.cancer.yaqeen.data.features.auth.models.SAggregate
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.models.VerificationStatus
import com.cancer.yaqeen.data.features.auth.responses.LoginRemote
import com.cancer.yaqeen.data.features.onboarding.models.CancerType
import com.cancer.yaqeen.data.features.onboarding.models.Module
import com.cancer.yaqeen.data.features.onboarding.models.Photo
import com.cancer.yaqeen.data.features.onboarding.models.Resources
import com.cancer.yaqeen.data.features.onboarding.models.Stage
import com.cancer.yaqeen.data.features.onboarding.models.University
import com.cancer.yaqeen.data.features.onboarding.responses.CancerStageResponse
import com.cancer.yaqeen.data.features.onboarding.responses.CancerTypeResponse
import com.cancer.yaqeen.data.features.onboarding.responses.DoctorResponse
import com.cancer.yaqeen.data.features.onboarding.responses.InterestResponse
import com.cancer.yaqeen.data.features.onboarding.responses.PatientResponse
import com.cancer.yaqeen.data.features.onboarding.responses.PhotoResponse
import com.cancer.yaqeen.data.features.onboarding.responses.ResourcesResponse
import com.cancer.yaqeen.data.features.onboarding.responses.SAggregateResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UniversitiesResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UserProfileResponse
import com.cancer.yaqeen.data.features.onboarding.responses.VerificationStatusResponse


class MappingResourcesRemoteAsModel: Mapper<ResourcesResponse, Resources> {
    override fun map(input: ResourcesResponse): Resources = input.run {
        Resources(
            stages = cancerStages.map { MappingStageRemoteAsModel().map(it) },
            cancerTypes = cancerTypes.map { MappingCancerTypeRemoteAsModel().map(it) },
            patientInterests = patientInterests.map { MappingInterestRemoteAsModel().map(it) },
            doctorInterests = doctorInterests.map { MappingInterestRemoteAsModel().map(it) },
            photos = photos.map { MappingPhotoRemoteAsModel().map(it) }
        )
    }
}
class MappingStageRemoteAsModel: Mapper<CancerStageResponse, Stage> {
    override fun map(input: CancerStageResponse): Stage = input.run {
        Stage(
            id = stageID ?: -1,
            number = stageID?.toInt() ?: 0,
            icon = logoURL ?: "",
            stageName = translations?.firstOrNull()?.translation?.stageName ?: ""
        )
    }
}
class MappingCancerTypeRemoteAsModel: Mapper<CancerTypeResponse, CancerType> {
    override fun map(input: CancerTypeResponse): CancerType = input.run {
        CancerType(
            id = cancerID ?: -1,
            icon = logoURL ?: "",
            typeName = translations?.firstOrNull()?.translation?.cancerTypeName ?: ""
        )
    }
}
class MappingInterestRemoteAsModel: Mapper<InterestResponse, Module> {
    override fun map(input: InterestResponse): Module = input.run {
        Module(
            id = interestID ?: -1,
            icon = logoURL ?: "",
            moduleName = translations?.firstOrNull()?.translation?.name ?: ""
        )
    }
}
class MappingPhotoRemoteAsModel: Mapper<PhotoResponse, Photo> {
    override fun map(input: PhotoResponse): Photo = input.run {
        Photo(
            photoURL = photoURL ?: "",
        )
    }
}


class MappingUniversitiesRemoteAsModel: Mapper<UniversitiesResponse, List<University>> {
    override fun map(input: UniversitiesResponse): List<University> = input.universities?.map {
        it.run {
            University(
                universityID = universityID ?: 0,
                universityName = universityName ?: "",
            )
        }
    } ?: listOf()
}

class MappingUserProfileRemoteAsModel(val user: User?): Mapper<UserProfileResponse, User?> {
    override fun map(input: UserProfileResponse): User? = input.users?.firstOrNull()?.run {
        User(
            id = user?.id,
            name = user?.name,
            nickname = user?.nickname,
            pictureURL = user?.pictureURL,
            familyName = user?.familyName,

            gender = gender,
            doctor = MappingDoctorRemoteAsModel().map(doctor),
            agreedTerms = agreedTerms,
            isEmailVerified = isEmailVerified,
            questionsAggregate = MappingSAggregateRemoteAsModel().map(questionsAggregate),
            firstName = firstName,
            lastName = lastName,
            patient = MappingPatientRemoteAsModel().map(patient),
            email = email
        )
    }
}
class MappingPatientRemoteAsModel: Mapper<PatientResponse?, Patient?> {
    override fun map(input: PatientResponse?): Patient? = input?.run {
        Patient(
            cancerTypeID = cancerTypeID,
            ageGroup = ageGroup,
            cancerStageID = cancerStageID
        )
    }
}
class MappingDoctorRemoteAsModel: Mapper<DoctorResponse?, Doctor?> {
    override fun map(input: DoctorResponse?): Doctor? = input?.run {
        Doctor(
            medicalField = medicalField,
            degree = degree,
            university = university,
            verificationStatus = MappingVerificationStatusRemoteAsModel().map(verificationStatus),
            answersAggregate = MappingSAggregateRemoteAsModel().map(answersAggregate),
        )
    }
}
class MappingVerificationStatusRemoteAsModel: Mapper<VerificationStatusResponse?, VerificationStatus?> {
    override fun map(input: VerificationStatusResponse?): VerificationStatus? = input?.run {
        VerificationStatus(
            notes = notes,
            verifierUserID = verifierUserID
        )
    }
}
class MappingSAggregateRemoteAsModel: Mapper<SAggregateResponse?, SAggregate?> {
    override fun map(input: SAggregateResponse?): SAggregate? = input?.run {
        SAggregate(
            aggregate = Aggregate(
                aggregate?.count
            )
        )
    }
}
