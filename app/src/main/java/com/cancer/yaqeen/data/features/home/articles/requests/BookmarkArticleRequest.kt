package com.cancer.yaqeen.data.features.home.articles.requests

import com.google.gson.annotations.SerializedName

data class BookmarkArticleRequest(
    @SerializedName("contentId")
    val articleId: Int
)
