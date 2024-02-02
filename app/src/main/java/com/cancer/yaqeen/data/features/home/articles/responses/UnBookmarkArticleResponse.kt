package com.cancer.yaqeen.data.features.home.articles.responses

import com.google.gson.annotations.SerializedName


data class UnBookmarkArticleResponse (
    @SerializedName("update_Bookmarks_by_pk")
    val unBookmark: UpdateBookmarkResponse?
)

data class UpdateBookmarkResponse (
    @SerializedName("BookmarkId")
    val bookmarkID: Long?
)