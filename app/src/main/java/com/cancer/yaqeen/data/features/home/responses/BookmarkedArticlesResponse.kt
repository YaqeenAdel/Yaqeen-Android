package com.cancer.yaqeen.data.features.home.responses

import com.google.gson.annotations.SerializedName

data class BookmarkedArticlesResponse(
    @SerializedName("Bookmarks")
    val bookmarks: List<SavedArticleResponse>?
)

data class SavedArticleResponse (
    @SerializedName("BookmarkId")
    val bookmarkID: Int?,
    @SerializedName("ContentId")
    val contentID: Int?,
    @SerializedName("CreatedDate")
    val createdDate: String?,
    @SerializedName("Content")
    val content: ContentResponse?
)
data class ContentResponse (
    @SerializedName("Translations")
    val translations: List<TranslationResponse>?
)
