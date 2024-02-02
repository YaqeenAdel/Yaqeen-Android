package com.cancer.yaqeen.data.features.home.articles.requests

import com.google.gson.annotations.SerializedName

data class RemoveArticleFromFavouriteRequest(
    @SerializedName("bookmarkId")
    val bookmarkId: Int
)
