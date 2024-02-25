package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms.details

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Photo
import com.cancer.yaqeen.databinding.ItemPhotoAttachedBinding
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage

class AttachedPhotosAdapter(
    private var items: MutableList<Photo> = arrayListOf(),
    private val onShowClick: (Photo) -> Unit,
    private val onDeleteClick: (Photo) -> Unit,
) :
    ListAdapter<Photo, AttachedPhotosAdapter.SymptomsTypesViewHolder>(Companion) {

    private var selectedPosition = Constants.INIT_SELECTED_POSITION
    private var lastSelectedPosition = Constants.INIT_SELECTED_POSITION

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
        val item = items[position]
        item?.let {
            holder.bind(it)
        }
    }
    fun setList(list: List<Photo>?) {
        submitList(list)

        items = (list ?: listOf()) as MutableList<Photo>
    }

    fun addPicture(uri: Uri) {

        items.add(
            Photo(
                id = 0,
                uri = uri,
                imageName = ""
            )
        )

        notifyItemInserted(items.size - 1)
        if(items.size == 1)
            super.submitList(items)
    }

    fun deletePicture() {

    }


    inner class SymptomsTypesViewHolder(
        private val itemBinding: ItemPhotoAttachedBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {


        private val _context = itemBinding.root.context

        fun bind(item: Photo) {
            with(item){
                itemBinding.tvSymptomImageName.text = imageName

                itemBinding.btnShow.setOnClickListener {
                    onShowClick(item)
                }

                itemBinding.btnDelete.setOnClickListener {
                    onDeleteClick(item)
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