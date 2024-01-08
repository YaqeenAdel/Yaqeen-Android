package com.cancer.yaqeen.presentation.ui.home.articles

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cancer.yaqeen.data.features.home.responses.Article
import com.cancer.yaqeen.data.features.onboarding.models.University
import com.cancer.yaqeen.databinding.ItemArticleBinding

class ArticlesAdapter(
    private var items: List<Article> = listOf(),
    private val onItemClick: (Article) -> Unit
) :
    ListAdapter<Article, ArticlesAdapter.ArticlesViewHolder>(Companion), Filterable {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    companion object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(
            oldItem: Article,
            newItem: Article
        ): Boolean {
            return oldItem.ContentId == newItem.ContentId
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

        return ArticlesViewHolder(binding) {
            onItemClick(currentList[it])
        }
    }

    fun setList(list: List<Article>?) {
        submitList(list)
    }

    override fun onBindViewHolder(holder: ArticlesViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(position, it)
        }
    }




    inner class ArticlesViewHolder(
        private val itemBinding: ItemArticleBinding,
        onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context


        fun bind(position: Int, item: Article) {
            itemBinding.tvArticleHeadline.text = item.Translations.get(0).Translation.Title
            Glide.with(_context).load(item.Translations.get(0).Translation.Thumbnail).centerInside().into(itemBinding.ivArticleImage)
            itemBinding.tvArticleDate.text=item.CreatedDate.split('T').first()
        }

     }

    private fun notifyItemChangedByPosition(position: Int) {
        selectedPosition = position
        notifyItemChanged(lastSelectedPosition)
        notifyItemChanged(selectedPosition)
        lastSelectedPosition = selectedPosition
    }

    override fun getFilter(): Filter {
        TODO("Not yet implemented")
    }

}