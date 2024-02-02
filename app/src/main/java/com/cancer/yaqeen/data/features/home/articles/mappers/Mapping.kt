package com.cancer.yaqeen.data.features.home.articles.mappers

import com.cancer.yaqeen.data.base.Mapper
import com.cancer.yaqeen.data.features.home.articles.models.Article
import com.cancer.yaqeen.data.features.home.articles.models.Bookmark
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Interest
import com.cancer.yaqeen.data.features.home.articles.responses.BookmarkArticleResponse
import com.cancer.yaqeen.data.features.home.articles.responses.ArticleResponse
import com.cancer.yaqeen.data.features.home.articles.responses.BookmarkedArticlesResponse
import com.cancer.yaqeen.data.features.home.articles.responses.HomeArticlesResponse
import com.cancer.yaqeen.data.features.home.articles.responses.InterestResponse
import com.cancer.yaqeen.data.features.home.articles.responses.SavedArticleResponse
import com.cancer.yaqeen.data.features.home.articles.responses.UnBookmarkArticleResponse
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
            backgroundColor = if (styleBackgroundColorHex == null) "#1BAFB7" else "#$styleBackgroundColorHex",
            textColor = if (styleForegroundColorHex == null) "#FF000000" else "#$styleForegroundColorHex"
        )
    }
}

class MappingBookmarkArticleRemoteAsUIModel :
    Mapper<BookmarkArticleResponse, Boolean> {
    override fun map(input: BookmarkArticleResponse): Boolean {
        return input.bookmark != null
    }
}

class MappingUnBookmarkArticleRemoteAsUIModel :
    Mapper<UnBookmarkArticleResponse, Boolean> {
    override fun map(input: UnBookmarkArticleResponse): Boolean {
        return input.unBookmark != null
    }
}

class MappingBookmarkedArticlesRemoteAsModel : Mapper<BookmarkedArticlesResponse, List<Bookmark>> {
    override fun map(input: BookmarkedArticlesResponse): List<Bookmark> = input.run {
        bookmarks?.map {
            MappingBookmarkRemoteAsModel().map(it)
        }
    } ?: listOf()
}


class MappingBookmarkRemoteAsModel : Mapper<SavedArticleResponse, Bookmark> {
    override fun map(input: SavedArticleResponse): Bookmark = input.run {
        Bookmark(
            bookmarkID = bookmarkID ?: -1,
            contentID = contentID ?: -1
        )
    }
}

class MappingSavedArticlesRemoteAsModel : Mapper<BookmarkedArticlesResponse, List<Article>> {
    override fun map(input: BookmarkedArticlesResponse): List<Article> = input.run {
        bookmarks?.map {
            MappingSavedArticleRemoteAsModel().map(it)
        }
    } ?: listOf()
}

class MappingSavedArticleRemoteAsModel : Mapper<SavedArticleResponse, Article> {
    override fun map(input: SavedArticleResponse): Article = input.run {
        Article(
            authorUserID = "",
            bookmarkID = bookmarkID ?: -1,
            contentID = contentID ?: -1,
            createdDate = createdDate?.formatDate() ?: "",
            phase = "",
            interests = listOf(),
            description = content?.translations?.firstOrNull()?.translationDetails?.description ?: "",
            link = content?.translations?.firstOrNull()?.translationDetails?.link ?: "",
            thumbnail = content?.translations?.firstOrNull()?.translationDetails?.thumbnail ?: "",
            title = content?.translations?.firstOrNull()?.translationDetails?.title ?: "",
            updatedAt = "",
            visibility = false
        )
    }
}