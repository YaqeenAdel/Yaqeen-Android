package com.cancer.yaqeen.presentation.ui.home.articles

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.models.Article
import com.cancer.yaqeen.databinding.ItemArticleBinding
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import com.cancer.yaqeen.presentation.util.changeVisibility

class ArticlesAdapter(
    private var articles: MutableList<Article> = arrayListOf(),
    private val onItemClick: (Article) -> Unit,
    private val onFavouriteArticleClick: (Article) -> Unit,
) :
    ListAdapter<Article, ArticlesAdapter.ArticlesViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem.contentID == newItem.contentID
        }

        override fun areContentsTheSame(
            oldOrder: Article,
            newOrder: Article
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ArticlesViewHolder {
        val binding =
            ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ArticlesViewHolder(binding)
    }

    fun setList(list: List<Article>) {
        submitList(list)
        articles = if(list.isEmpty())
            arrayListOf()
        else
            list as MutableList<Article>
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(position, it)
        }
    }

    inner class ArticlesViewHolder(
        private val itemBinding: ItemArticleBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        fun bind(position: Int, item: Article) {
            itemBinding.articleImageUrl = item.thumbnail
            itemBinding.tvArticleHeadline.text = item.title
            itemBinding.tvArticleDate.text = item.createdDate
            itemBinding.tvCategoryName.text = item.tags.firstOrNull()?.interestID ?: ""
            itemBinding.cardCategory.backgroundTintList =
                ColorStateList.valueOf(Color.parseColor(item.tags.firstOrNull()?.backgroundColor))
            itemBinding.tvCategoryName.setTextColor(ColorStateList.valueOf(Color.parseColor(item.tags.firstOrNull()?.textColor)))

            itemBinding.view.changeVisibility(show = (position + 1) < itemCount, isGone = false)
            bindResourceImage(
                itemBinding.ivArticleBookmark,
                if(item.isFavorite) R.drawable.ic_bookmark_checked else R.drawable.ic_bookmark_unchecked
            )

            itemBinding.itemContainer.setOnClickListener {
                onItemClick(item)
            }
            itemBinding.cardArticleBookmark.setOnClickListener {
                onFavouriteArticleClick(item)
            }
        }
     }

    fun changeFavouriteStatusArticle(article: Article, isFavorite: Boolean) {
        val index = articles.indexOfFirst {
            it.contentID == article.contentID
        }
        articles.first {
            it.contentID == article.contentID
        }.also {
            it.isFavorite = isFavorite
        }
        notifyItemChanged(index)
    }

}