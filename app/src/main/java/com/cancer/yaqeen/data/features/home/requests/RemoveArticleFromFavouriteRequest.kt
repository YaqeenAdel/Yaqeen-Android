package com.cancer.yaqeen.data.features.home.requests

import com.google.gson.annotations.SerializedName

data class RemoveArticleFromFavouriteRequest(
    @SerializedName("bookmarkId")
    val bookmarkId: Int
)
