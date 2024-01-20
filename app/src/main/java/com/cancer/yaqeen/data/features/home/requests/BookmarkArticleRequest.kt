package com.cancer.yaqeen.data.features.home.requests

import com.google.gson.annotations.SerializedName

data class BookmarkArticleRequest(
    @SerializedName("contentId")
    val articleId: Int
)
