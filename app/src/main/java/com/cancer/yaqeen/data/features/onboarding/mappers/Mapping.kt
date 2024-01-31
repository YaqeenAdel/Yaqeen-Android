package com.cancer.yaqeen.data.features.onboarding.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.auth.models.Doctor
import com.cancer.yaqeen.data.features.auth.models.Patient
import com.cancer.yaqeen.data.features.auth.models.User
import com.cancer.yaqeen.data.features.auth.models.UserInterest
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
import com.cancer.yaqeen.data.features.onboarding.responses.InterestUserResponse
import com.cancer.yaqeen.data.features.onboarding.responses.PatientResponse
import com.cancer.yaqeen.data.features.onboarding.responses.PhotoResponse
import com.cancer.yaqeen.data.features.onboarding.responses.ResourcesResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UniversitiesResponse
import com.cancer.yaqeen.data.features.onboarding.responses.UserProfileResponse


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
            title = "Explore trusted articles to learn more about your condition",
            body = " Discover helpful tips for recovery, Empower your healing journey with reliable information and practical advice."
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
    override fun map(input: UserProfileResponse): User? = input.users?.let {
        it.firstOrNull()?.run {
            User(
                id = user?.id ?: "",
                name = user?.name ?: "",
                nickname = user?.nickname ?: "",
                pictureURL = user?.pictureURL ?: "",
                familyName = user?.familyName ?: "",

                gender = gender ?: "",
                doctor = MappingDoctorRemoteAsModel().map(doctor),
                agreedTerms = agreedTerms ?: false,
//                isEmailVerified = isEmailVerified,
//                questionsAggregate = MappingSAggregateRemoteAsModel().map(questionsAggregate),
                firstName = firstName ?: "",
                lastName = lastName ?: "",
                patient = MappingPatientRemoteAsModel().map(patient),
                email = email ?: "",
                userInterests = MappingUserInterestRemoteAsModel().map(interestUsers),
            )
        }
    }
}
class MappingUserInterestRemoteAsModel: Mapper<List<InterestUserResponse>?, List<UserInterest>> {
    override fun map(input: List<InterestUserResponse>?): List<UserInterest> = input?.map {
        it.run {
            UserInterest(
                id = interestID ?: 0,
                name = interest?.translations?.firstOrNull()?.translation?.name ?: "",
            )
        }
    } ?: listOf()
}
class MappingPatientRemoteAsModel: Mapper<PatientResponse?, Patient?> {
    override fun map(input: PatientResponse?): Patient? = input?.run {
        Patient(
            ageGroup = ageGroup ?: 0,
            cancerStageID = cancerStageID ?: 0,
            cancerTypeID = cancerTypeID ?: 0,
            stageName = cancerStage?.translations?.firstOrNull()?.translation?.stageName ?: "",
            cancerTypeName = cancerType?.translations?.firstOrNull()?.translation?.cancerTypeName ?: "",
        )
    }
}
class MappingDoctorRemoteAsModel: Mapper<DoctorResponse?, Doctor?> {
    override fun map(input: DoctorResponse?): Doctor? = input?.run {
        Doctor(
            credentialsAttachments = credentialsAttachments ?: listOf(),
            degree = degree ?: "",
            medicalField = medicalField ?: "",
            university = university ?: "",
            verificationStatus = verificationStatus ?: ""
        )
    }
}

