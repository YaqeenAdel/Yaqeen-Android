package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.home.models.MedicationType
import com.cancer.yaqeen.data.features.home.models.UnitType
import com.cancer.yaqeen.databinding.ItemUnitTypeBinding

class UnitTypesAdapter(
    private val onItemClick: (UnitType) -> Unit
) :
    ListAdapter<UnitType, UnitTypesAdapter.UnitTypesViewHolder>(Companion) {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    companion object : DiffUtil.ItemCallback<UnitType>() {
        override fun areItemsTheSame(
            oldItem: UnitType,
            newItem: UnitType
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: UnitType,
            newOrder: UnitType
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UnitTypesViewHolder {
        val binding =
            ItemUnitTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return UnitTypesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UnitTypesViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(position, it)
        }
    }

    fun anyItemIsSelected(): Boolean =
        selectedPosition != -1

    fun getItemSelected(): UnitType =
        currentList[selectedPosition]

    fun selectItem(id: Int){
        val positionItem = currentList.indexOfFirst {
            it.id == id
        }
        notifyItemChangedByPosition(positionItem)
    }

    inner class UnitTypesViewHolder(
        private val itemBinding: ItemUnitTypeBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        fun bind(position: Int, item: UnitType) {
            itemBinding.tvUnitType.text = item.name
            val isSelected = selectedPosition == position

            val backgroundId = if (isSelected)
                com.cancer.yaqeen.R.drawable.background_selected_view
            else
                com.cancer.yaqeen.R.drawable.background_unselected_view

            itemBinding.container.setBackgroundResource(backgroundId)

            itemBinding.container.setOnClickListener {
                notifyItemChangedByPosition(position)
                onItemClick(item)
            }
        }
    }

    private fun notifyItemChangedByPosition(position: Int) {
        selectedPosition = position

        notifyItemChanged(lastSelectedPosition)
        notifyItemChanged(selectedPosition)

        lastSelectedPosition = selectedPosition
    }
}