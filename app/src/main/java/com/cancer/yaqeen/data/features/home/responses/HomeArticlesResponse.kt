package com.cancer.yaqeen.data.features.home.responses

import com.google.gson.annotations.SerializedName

data class HomeArticlesResponse(
    @SerializedName("Articles")
    val Articles: List<Article>
)
data class Article(
    @SerializedName("AssignedTo")
    val AssignedTo: Any,
    @SerializedName("Attachments")
    val Attachments: List<Any>,
    @SerializedName("AuthorUserId")
    val AuthorUserId: String,
    @SerializedName("ContentId")
    val ContentId: Int,
    @SerializedName("CreatedDate")
    val CreatedDate: String,
    @SerializedName("Phase")
    val Phase: String,
    @SerializedName("Tags")
    val Tags: List<Tag>,
    @SerializedName("Translations")
    val Translations: List<Translation>,
    @SerializedName("UpdatedAt")
    val UpdatedAt: String,
    @SerializedName("Visibility")
    val Visibility: String
)
data class Tag(
    @SerializedName("InterestId")
    val InterestId: String
)
data class Translation(
    @SerializedName("Language")
    val Language: String,
    @SerializedName("Translation")
    val Translation: TranslationX
)
data class TranslationX(
    @SerializedName("Description")
    val Description: String,
    @SerializedName("Link")
    val Link: String,
    @SerializedName("Thumbnail")
    val Thumbnail: String,
    @SerializedName("Title")
    val Title: String
)