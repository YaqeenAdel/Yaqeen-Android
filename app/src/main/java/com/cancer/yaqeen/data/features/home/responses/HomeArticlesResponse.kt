package com.cancer.yaqeen.data.features.home.responses

import com.google.gson.annotations.SerializedName

data class HomeArticlesResponse(
    @SerializedName("Articles")
    val articles: List<ArticleResponse>?
)
data class ArticleResponse(
    @SerializedName("AssignedTo")
    val assignedTo: Any?,
    @SerializedName("Attachments")
    val attachments: List<Any>?,
    @SerializedName("AuthorUserId")
    val authorUserID: String?,
    @SerializedName("ContentId")
    val contentID: Int?,
    @SerializedName("CreatedDate")
    val createdDate: String?,
    @SerializedName("UpdatedAt")
    val updatedAt: String?,
    @SerializedName("Phase")
    val phase: String?,
    @SerializedName("Visibility")
    val visibility: String?,
    @SerializedName("Interests")
    val interests: List<InterestElementResponse>?,
    @SerializedName("Translations")
    val translations: List<TranslationResponse>?
)
data class InterestElementResponse (
    @SerializedName("Interest")
    val interest: InterestResponse?
)

data class InterestResponse(
    @SerializedName("InterestId")
    val interestID: Int?,
    @SerializedName("Translations")
    val translations: List<InterestTranslationResponse>?,
    @SerializedName("StyleBackgroundColorHex")
    val styleBackgroundColorHex: String?,
    @SerializedName("StyleForegroundColorHex")
    val styleForegroundColorHex: String?
)
data class InterestTranslationResponse (
    @SerializedName("Language")
    val language: String?,
    @SerializedName("Translation")
    val translation: PurpleTranslation?
)
data class PurpleTranslation (
    @SerializedName("Name")
    val name: String?
)

data class TranslationResponse(
    @SerializedName("Language")
    val language: String?,
    @SerializedName("Translation")
    val translationDetails: TranslationDetailsResponse?
)
data class TranslationDetailsResponse(
    @SerializedName("Description")
    val description: String?,
    @SerializedName("Link")
    val link: String?,
    @SerializedName("Thumbnail")
    val thumbnail: String?,
    @SerializedName("Title")
    val title: String?
)