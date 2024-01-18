package com.cancer.yaqeen.data.features.home.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Article(
//    val assignedTo: Any,
//    val attachments: List<Any>,
    val authorUserID: String,
    val contentID: Int,
    var bookmarkID: Int? = null,
    val createdDate: String,
    val phase: String,
    val interests: List<Interest>,
    val description: String,
    val link: String,
    val thumbnail: String,
    val title: String,
    val updatedAt: String,
    val visibility: Boolean,
    var isFavorite: Boolean = false
): Parcelable

//@Parcelize
//data class Tag(
//    val interestID: String,
//    val backgroundColor: String,
//    val textColor: String,
//): Parcelable