package com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.databinding.ItemPhotoSmallBinding
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage

class SmallPhotosAdapter(
    private val onItemClick: (String) -> Unit
) :
    ListAdapter<String, SmallPhotosAdapter.PhotosViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldOrder: String,
            newOrder: String
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotosViewHolder {
        val binding =
            ItemPhotoSmallBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhotosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(it)
        }
    }

    inner class PhotosViewHolder(
        private val itemBinding: ItemPhotoSmallBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: String) {
            bindImage(itemBinding.ivSymptom, item)
        }
    }

}