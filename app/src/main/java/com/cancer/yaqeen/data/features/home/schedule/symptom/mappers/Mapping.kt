package com.cancer.yaqeen.data.features.home.schedule.symptom.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ReminderTime2
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.ModifyScheduleResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomTrack
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.AddSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.DeleteSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.EditSymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.SymptomResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.SymptomTypesResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.SymptomsResponse
import com.cancer.yaqeen.data.features.home.schedule.symptom.responses.UploadUrlResponse
import com.cancer.yaqeen.data.utils.convertDateTimeAPIToHour24UI
import com.cancer.yaqeen.data.utils.createPhotosList
import com.cancer.yaqeen.data.utils.formatDateTimeAPIToDateUI
import com.cancer.yaqeen.data.utils.formatDateTimeAPIToTimeUI
import java.util.Locale


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
    Mapper<UploadUrlResponse, ModifyScheduleResponse?> {
    override fun map(input: UploadUrlResponse): ModifyScheduleResponse? {
        return input.getUploadURL?.run {
            ModifyScheduleResponse(
                signedURL = signedURL,
                path = path
            )
        }
    }
}

class MappingAddSymptomWithUploadRemoteAsUIModel :
    Mapper<AddSymptomResponse, ModifyScheduleResponse?> {
    override fun map(input: AddSymptomResponse): ModifyScheduleResponse? {
        return ModifyScheduleResponse(
            photoIsUploaded = true,
            scheduleIsModified = input.response != null
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
    Mapper<EditSymptomResponse, ModifyScheduleResponse?> {
    override fun map(input: EditSymptomResponse): ModifyScheduleResponse? {
        return ModifyScheduleResponse(
            photoIsUploaded = true,
            scheduleIsModified = input.response != null
        )
    }
}

class MappingSymptomsRemoteAsModel: Mapper<SymptomsResponse, List<Symptom>> {
    override fun map(input: SymptomsResponse): List<Symptom> = input.symptoms?.map {
        MappingSymptomRemoteAsModel().map(it)
    } ?: listOf()

}

class MappingSymptomRemoteAsModel: Mapper<SymptomResponse, Symptom> {
    override fun map(input: SymptomResponse): Symptom = input.run {
        Symptom(
            id = symptomId ?: 0,
            symptomTypes = listOf(
                SymptomType(
                    id = symptomLookups?.firstOrNull()?.details?.symptomLookupID ?: 0,
                    name = symptomLookups?.firstOrNull()?.details?.translations?.firstOrNull()?.translation?.name ?: ""
                )
            ),
            photosList = createPhotosList(photoLink, downloadPhotoLink?.url, downloadPhotoLinks?.urls?.map { it.url }),
            details = details,
            reminderTime2 = ReminderTime2(
                timeEN = formatDateTimeAPIToTimeUI(dateTime = time, locale = Locale.ENGLISH),
                timeUI = formatDateTimeAPIToTimeUI(time),
                hour24 = convertDateTimeAPIToHour24UI(time),
            ),
            startDateUI = formatDateTimeAPIToDateUI(time),
//            reminderTime = formatDateTimeAPIToTimeUI(dateTime = time, locale = Locale.ENGLISH),
            startDate = formatDateTimeAPIToDateUI(time, locale = Locale.ENGLISH),
            doctorName = notes
        )
    }
}


class MappingSymptomAsSymptomTrack: Mapper<Symptom, SymptomTrack> {
    override fun map(input: Symptom): SymptomTrack = input.run {
        SymptomTrack(
            symptomTypes = symptomTypes,
            details = details,
            photosList = photosList as MutableList<Photo>?,
            reminderTime2 = reminderTime2,
            startDateUI = startDateUI,
            startDateEn = startDate,
            doctorName = doctorName,
            editable = true,
            symptomId = id,
        )
    }
}

