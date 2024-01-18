package com.cancer.yaqeen.data.features.home.responses

import com.google.gson.annotations.SerializedName

data class BookmarkedArticlesResponse(
    @SerializedName("Bookmarks")
    val bookmarks: List<BookmarkResponse>?
)

data class BookmarkResponse (
    @SerializedName("BookmarkId")
    val bookmarkID: Int?,

    @SerializedName("ContentId")
    val contentID: Int?
)