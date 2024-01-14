package com.cancer.yaqeen.data.features.home.requests

import com.google.gson.annotations.SerializedName

data class AddArticleToFavouriteRequest(
    @SerializedName("contentId")
    val articleId: Int
)
