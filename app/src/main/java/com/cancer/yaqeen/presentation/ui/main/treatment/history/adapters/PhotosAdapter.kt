package com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.databinding.ItemPhotoBinding
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage

class PhotosAdapter(
    private val onItemClick: (Photo) -> Unit
) :
    ListAdapter<Photo, PhotosAdapter.PhotosViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(
            oldItem: Photo,
            newItem: Photo
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldOrder: Photo,
            newOrder: Photo
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PhotosViewHolder {
        val binding =
            ItemPhotoBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return PhotosViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(it)
        }
    }

    inner class PhotosViewHolder(
        private val itemBinding: ItemPhotoBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: Photo) {
            with(item){
                url?.let {
                    updateUI(it)
                }
                uri?.let {
                    updateUI(it)
                }
            }
        }
        private fun updateUI(url: String) {
            bindImage(itemBinding.ivSymptom, url)
        }

        private fun updateUI(uri: Uri) {
            itemBinding.ivSymptom.setImageURI(uri)
        }
    }

}