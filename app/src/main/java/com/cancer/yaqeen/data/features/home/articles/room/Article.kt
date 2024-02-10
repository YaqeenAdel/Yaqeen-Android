package com.cancer.yaqeen.data.features.home.articles.room

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Interest
import kotlinx.android.parcel.Parcelize

@Entity
data class Article(
    @PrimaryKey
    val articleID: Int,
    val bookmarkID: Int? = null
)
