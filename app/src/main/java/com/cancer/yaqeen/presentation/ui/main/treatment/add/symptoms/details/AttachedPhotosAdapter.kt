package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.details

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.databinding.ItemPhotoAttachedBinding
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.generateFileName
import com.cancer.yaqeen.presentation.util.getCurrentTimeMillis
import java.text.SimpleDateFormat
import java.util.Locale

class AttachedPhotosAdapter(
    private var items: MutableList<Photo> = arrayListOf(),
    private val onShowClick: (Photo) -> Unit,
    private val onDeleteClick: (Photo) -> Unit,
) :
    ListAdapter<Photo, AttachedPhotosAdapter.SymptomsTypesViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Photo>() {
        override fun areItemsTheSame(
            oldItem: Photo,
            newItem: Photo
        ): Boolean {
            return oldItem.id == newItem.id
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
    ): SymptomsTypesViewHolder {
        val binding =
            ItemPhotoAttachedBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SymptomsTypesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SymptomsTypesViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(position, it)
        }
    }

    fun setList(list: List<Photo>) {
        super.submitList(list)

        if (items.isEmpty())
            items = list as MutableList<Photo>
    }

    fun addPicture(photo: Photo) {
        items.add(
            photo
        )

        notifyItemInserted(items.size - 1)
        if(items.size == 1)
            super.submitList(items)
    }

    fun addPictures(photos: List<Photo>) {
        val positionStart = items.size
        items.addAll(
            photos
        )

        notifyItemRangeInserted(positionStart, photos.size)
        if(positionStart == 0)
            super.submitList(items)
    }

    fun deletePicture(item: Photo) {
        val position = items.indexOfFirst { it.id == item.id }

        if (position == -1)
            return
        if (items.size == 1)
            items = arrayListOf()
        else
            items.removeAt(position)

        super.submitList(items)
        notifyDataSetChanged()
    }


    inner class SymptomsTypesViewHolder(
        private val itemBinding: ItemPhotoAttachedBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {


        private val _context = itemBinding.root.context

        fun bind(position: Int, item: Photo) {
            with(item){
                itemBinding.tvSymptomImageName.text = imageName

                itemBinding.btnShow.setOnClickListener {
                    onShowClick(item)
                }

                itemBinding.btnDelete.setOnClickListener {
                    onDeleteClick(item)
                    deletePicture(item)
                }

                uri?.let {
                    itemBinding.ivSymptom.setImageURI(uri)
                }
                url?.let {
                    bindImage(itemBinding.ivSymptom, url)
                }
            }
        }
    }

}