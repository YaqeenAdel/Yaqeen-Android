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
    @SerializedName("Phase")
    val phase: String?,
    @SerializedName("Tags")
    val tags: List<TagResponse>?,
    @SerializedName("Translations")
    val translations: List<TranslationResponse>?,
    @SerializedName("UpdatedAt")
    val updatedAt: String?,
    @SerializedName("Visibility")
    val visibility: String?,
    @SerializedName("IsFavorite")
    val isFavorite: String?
)
data class TagResponse(
    @SerializedName("InterestId")
    val interestID: String?,
    @SerializedName("BackgroundColor")
    val backgroundColor: String?,
    @SerializedName("TextColor")
    val textColor: String?,
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