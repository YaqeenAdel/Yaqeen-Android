package com.cancer.yaqeen.data.features.home.responses

import com.google.gson.annotations.SerializedName


data class AddArticleToFavouriteResponse (
    @SerializedName("insert_Bookmarks_one")
    val bookmark: InsertBookmarksOne?
)

data class InsertBookmarksOne (
    @SerializedName("BookmarkId")
    val bookmarkID: Long?,

    @SerializedName("ContentId")
    val contentID: Long?,

    @SerializedName("CreatedDate")
    val createdDate: String?
)