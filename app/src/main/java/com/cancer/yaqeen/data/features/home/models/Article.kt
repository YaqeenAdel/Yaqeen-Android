package com.cancer.yaqeen.data.features.home.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Article(
//    val assignedTo: Any,
//    val attachments: List<Any>,
    val authorUserID: String,
    val contentID: Int,
    val createdDate: String,
    val phase: String,
    val tags: List<Tag>,
    val description: String,
    val link: String,
    val thumbnail: String,
    val title: String,
    val updatedAt: String,
    val visibility: Boolean,
    var isFavorite: Boolean
): Parcelable

@Parcelize
data class Tag(
    val interestID: String,
    val backgroundColor: String,
    val textColor: String,
): Parcelable