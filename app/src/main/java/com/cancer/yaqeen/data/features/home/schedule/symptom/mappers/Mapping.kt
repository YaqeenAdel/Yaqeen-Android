package com.cancer.yaqeen.data.features.home.schedule.symptom.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
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
import com.cancer.yaqeen.presentation.util.generateFileName
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis


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

class MappingAddSymptomWithUploadRemoteAsUIModel :
    Mapper<AddSymptomResponse, ModifySymptomResponse?> {
    override fun map(input: AddSymptomResponse): ModifySymptomResponse? {
        return ModifySymptomResponse(
            photoIsUploaded = true,
            symptomIsModified = input.response != null
        )
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

class MappingEditSymptomWithUploadRemoteAsUIModel :
    Mapper<EditSymptomResponse, ModifySymptomResponse?> {
    override fun map(input: EditSymptomResponse): ModifySymptomResponse? {
        return ModifySymptomResponse(
            photoIsUploaded = true,
            symptomIsModified = input.response != null
        )
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
                        name = symptomLookups?.firstOrNull()?.details?.translations?.firstOrNull()?.translation?.name ?: ""
                    )
                ),
                photosList = createPhotosList(photoLink, downloadPhotoLink?.url, downloadPhotoLinks?.urls?.map { it.url }),
                details = details,
                reminderTime = time?.formatTime() ?: "",
                startDate = time?.formatDate() ?: "",
                doctorName = notes
            )
        }
    } ?: listOf()

    private fun createPhotosList(photoPath: String?, url: String?, urls: List<String?>?): List<Photo> {
        val photos: MutableList<Photo> = arrayListOf()
        val photosPaths = photoPath?.split(",") ?: listOf()
//        val photosUrls = url?.split(",") ?: listOf()

        val photosUrls = urls ?: listOf()

        val pathsSize = photosPaths.size
        val urlsSize = photosUrls.size

        val size = if(pathsSize < urlsSize) pathsSize else urlsSize

        var pathURL = ""
        var imageName = ""

        for (index in 0 until size){
            pathURL = photosPaths[index]
            imageName = pathURL.substringAfterLast("/")
            imageName = imageName.ifEmpty { generateFileName() }

            photos.add(
                Photo(
                    id = getCurrentTimeMillis(),
                    url = photosUrls[index],
                    pathURL = pathURL,
                    imageName = imageName
                )
            )
        }

        return photos
    }
}


class MappingSymptomAsSymptomTrack: Mapper<Symptom, SymptomTrack> {
    override fun map(input: Symptom): SymptomTrack = input.run {
        SymptomTrack(
            symptomTypes = symptomTypes,
            details = details,
            photosList = photosList as MutableList<Photo>?,
            reminderTime = reminderTime,
            startDate = startDate,
            doctorName = doctorName,
            editable = true,
            symptomId = id,
        )
    }
}
