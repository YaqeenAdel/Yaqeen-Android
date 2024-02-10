package com.cancer.yaqeen.data.features.home.articles.responses

import com.google.gson.annotations.SerializedName


data class BookmarkArticleResponse (
    @SerializedName("insert_Bookmarks_one")
    val bookmark: InsertBookmarksOneResponse?
)

data class InsertBookmarksOneResponse (
    @SerializedName("BookmarkId")
    val bookmarkID: Int?,

    @SerializedName("ContentId")
    val contentID: Int?,

    @SerializedName("CreatedDate")
    val createdDate: String?
)