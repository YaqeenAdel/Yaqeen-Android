package com.cancer.yaqeen.data.features.home.schedule.symptom.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifySymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomTrack
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.AddSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.DeleteSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.EditSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.SymptomTypesResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.SymptomsResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.UploadUrlResponse
import com.cancer.yaqeen.data.utils.formatDate
import com.cancer.yaqeen.data.utils.formatTime


class MappingSymptomsTypesRemoteAsUIModel: Mapper<SymptomTypesResponse, List<SymptomType>> {
    override fun map(input: SymptomTypesResponse): List<SymptomType> = input.symptomLookups?.map {
        it.run {
            SymptomType(
                id = symptomLookupID ?: 0,
                name = translations?.firstOrNull()?.translation?.name ?: ""
            )
        }
    } ?: listOf()
}

class MappingCreateAnUploadLocationRemoteAsUIModel :
    Mapper<UploadUrlResponse, ModifySymptomResponse?> {
    override fun map(input: UploadUrlResponse): ModifySymptomResponse? {
        return input.getUploadURL?.run {
            ModifySymptomResponse(
                signedURL = signedURL,
                path = path
            )
        }
    }
}

class MappingAddSymptomRemoteAsUIModel :
    Mapper<AddSymptomResponse, Boolean> {
    override fun map(input: AddSymptomResponse): Boolean {
        return input.response != null
    }
}

class MappingDeleteSymptomRemoteAsUIModel :
    Mapper<DeleteSymptomResponse, Boolean> {
    override fun map(input: DeleteSymptomResponse): Boolean {
        return input.response != null
    }
}

class MappingEditSymptomRemoteAsUIModel :
    Mapper<EditSymptomResponse, Boolean> {
    override fun map(input: EditSymptomResponse): Boolean {
        return input.response != null
    }
}

class MappingSymptomsRemoteAsModel: Mapper<SymptomsResponse, List<Symptom>> {
    override fun map(input: SymptomsResponse): List<Symptom> = input.symptoms?.map {
        it.run {
            Symptom(
                id = symptomId ?: 0,
                symptomTypes = listOf(
                    SymptomType(
                        id = symptomLookupID ?: 0,
                        name = symptomLookup.translations?.firstOrNull()?.translation?.name ?: ""
                    )
                ),
                imageUrl = photoLink ?: "",
                imageDownloadUrl = downloadPhotoLink?.url ?: "",
                details = details,
                reminderTime = time?.formatTime() ?: "",
                startDate = time?.formatDate() ?: "",
                doctorName = notes
            )
        }
    } ?: listOf()
}


class MappingSymptomAsSymptomTrack: Mapper<Symptom, SymptomTrack> {
    override fun map(input: Symptom): SymptomTrack = input.run {
        SymptomTrack(
            symptomTypes = symptomTypes,
            details = details,
            imageUrl = imageUrl,
            imageDownloadUrl = imageDownloadUrl,
            reminderTime = reminderTime,
            startDate = startDate,
            doctorName = doctorName,
            editable = true,
            symptomId = id,
        )
    }
}
