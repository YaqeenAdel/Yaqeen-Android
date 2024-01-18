package com.cancer.yaqeen.data.features.home.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.models.Article
import com.cancer.yaqeen.data.features.home.models.Bookmark
import com.cancer.yaqeen.data.features.home.models.Interest
import com.cancer.yaqeen.data.features.home.responses.AddArticleToFavouriteResponse
import com.cancer.yaqeen.data.features.home.responses.ArticleResponse
import com.cancer.yaqeen.data.features.home.responses.BookmarkResponse
import com.cancer.yaqeen.data.features.home.responses.BookmarkedArticlesResponse
import com.cancer.yaqeen.data.features.home.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.features.home.responses.InterestResponse
import com.cancer.yaqeen.data.utils.formatDate


class MappingArticlesRemoteAsModel : Mapper<HomeArticlesResponse, List<Article>> {
    override fun map(input: HomeArticlesResponse): List<Article> = input.run {
        articles?.map {
            MappingArticleRemoteAsModel().map(it)
        }
    } ?: listOf()
}


class MappingArticleRemoteAsModel : Mapper<ArticleResponse, Article> {
    override fun map(input: ArticleResponse): Article = input.run {
        Article(
            authorUserID = authorUserID ?: "",
            contentID = contentID ?: -1,
            createdDate = createdDate?.formatDate() ?: "",
            phase = phase ?: "",
            interests = interests?.filter { it.interest != null }?.map {
                MappingInterestRemoteAsModel().map(
                    it.interest!!
                )
            } ?: listOf(),
            description = translations?.firstOrNull()?.translationDetails?.description ?: "",
            link = translations?.firstOrNull()?.translationDetails?.link ?: "",
            thumbnail = translations?.firstOrNull()?.translationDetails?.thumbnail ?: "",
            title = translations?.firstOrNull()?.translationDetails?.title ?: "",
            updatedAt = updatedAt?.formatDate() ?: "",
            visibility = visibility.equals("true")
        )
    }
}

class MappingInterestRemoteAsModel : Mapper<InterestResponse, Interest> {
    override fun map(input: InterestResponse): Interest = input.run {
        Interest(
            interestID = interestID ?: 0,
            interestName = translations?.firstOrNull()?.translation?.name ?: "",
            backgroundColor = if (styleBackgroundColorHex == null) "#D2D4DA" else "#$styleBackgroundColorHex",
            textColor = if (styleForegroundColorHex == null) "#FFFFFFFF" else "#$styleForegroundColorHex"
        )
    }
}

class MappingAddArticleToFavouriteRemoteAsUIModel :
    Mapper<AddArticleToFavouriteResponse, Boolean> {
    override fun map(input: AddArticleToFavouriteResponse): Boolean {
        return input.bookmark != null
    }
}

class MappingBookmarkedArticlesRemoteAsModel : Mapper<BookmarkedArticlesResponse, List<Bookmark>> {
    override fun map(input: BookmarkedArticlesResponse): List<Bookmark> = input.run {
        bookmarks?.map {
            MappingBookmarkRemoteAsModel().map(it)
        }
    } ?: listOf()
}


class MappingBookmarkRemoteAsModel : Mapper<BookmarkResponse, Bookmark> {
    override fun map(input: BookmarkResponse): Bookmark = input.run {
        Bookmark(
            bookmarkID = bookmarkID ?: -1,
            contentID = contentID ?: -1
        )
    }
}