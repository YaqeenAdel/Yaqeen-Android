package com.cancer.yaqeen.data.features.home.responses

data class Article(
    val AssignedTo: Any,
    val Attachments: List<Any>,
    val AuthorUserId: String,
    val ContentId: Int,
    val CreatedDate: String,
    val Phase: String,
    val Tags: List<Tag>,
    val Translations: List<Translation>,
    val UpdatedAt: String,
    val Visibility: String
)