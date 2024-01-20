package com.cancer.yaqeen.data.features.home.responses

import com.google.gson.annotations.SerializedName


data class BookmarkArticleResponse (
    @SerializedName("insert_Bookmarks_one")
    val bookmark: InsertBookmarksOneResponse?
)

data class InsertBookmarksOneResponse (
    @SerializedName("BookmarkId")
    val bookmarkID: Long?,

    @SerializedName("ContentId")
    val contentID: Long?,

    @SerializedName("CreatedDate")
    val createdDate: String?
)