package com.cancer.yaqeen.data.features.home.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.models.Article
import com.cancer.yaqeen.data.features.home.models.Tag
import com.cancer.yaqeen.data.features.home.responses.AddArticleToFavouriteResponse
import com.cancer.yaqeen.data.features.home.responses.ArticleResponse
import com.cancer.yaqeen.data.features.home.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.features.home.responses.TagResponse
import com.cancer.yaqeen.data.utils.formatDate
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate


class MappingArticlesRemoteAsModel: Mapper<HomeArticlesResponse, List<Article>> {
    override fun map(input: HomeArticlesResponse): List<Article> = input.run {
        articles?.map {
            MappingArticleRemoteAsModel().map(it)
        }
    } ?: listOf()
}


class MappingArticleRemoteAsModel: Mapper<ArticleResponse, Article> {
    override fun map(input: ArticleResponse): Article = input.run {
        Article(
            authorUserID = authorUserID ?: "",
            contentID = contentID ?: -1,
            createdDate = createdDate?.formatDate() ?: "",
            phase = phase ?: "",
            tags = tags?.map { MappingTagRemoteAsModel().map(it) } ?: listOf(),
            description = translations?.firstOrNull()?.translationDetails?.description ?: "",
            link = translations?.firstOrNull()?.translationDetails?.link ?: "",
            thumbnail = translations?.firstOrNull()?.translationDetails?.thumbnail ?: "",
            title = translations?.firstOrNull()?.translationDetails?.title ?: "",
            updatedAt = updatedAt?.formatDate() ?: "",
            visibility = visibility.equals("true"),
            isFavorite = isFavorite.equals("true")
        )
    }
}

class MappingTagRemoteAsModel: Mapper<TagResponse, Tag> {
    override fun map(input: TagResponse): Tag = input.run {
        Tag(
            interestID = interestID ?: "",
            backgroundColor = backgroundColor ?: "#D2D4DA",
            textColor = textColor ?: "#FFFFFFFF"
        )
    }
}

class MappingAddArticleToFavouriteRemoteAsUIModel :
    Mapper<AddArticleToFavouriteResponse, Boolean> {
    override fun map(input: AddArticleToFavouriteResponse): Boolean {
        return input.bookmark != null
    }
}