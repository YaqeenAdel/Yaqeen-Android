package com.cancer.yaqeen.presentation.ui.main.treatment.medications

import android.R
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.home.models.MedicationType
import com.cancer.yaqeen.databinding.ItemMedicationTypeBinding

class MedicationTypesAdapter(
    private val onItemClick: (MedicationType) -> Unit
) :
    ListAdapter<MedicationType, MedicationTypesAdapter.MedicationTypesViewHolder>(Companion) {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    companion object : DiffUtil.ItemCallback<MedicationType>() {
        override fun areItemsTheSame(
            oldItem: MedicationType,
            newItem: MedicationType
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: MedicationType,
            newOrder: MedicationType
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicationTypesViewHolder {
        val binding =
            ItemMedicationTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MedicationTypesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationTypesViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(position, it)
        }
    }

    fun anyItemIsSelected(): Boolean =
        selectedPosition != -1

    fun selectItem(id: Int){
        val positionItem = currentList.indexOfFirst {
            it.id == id
        }
        notifyItemChangedByPosition(positionItem)
    }

    inner class MedicationTypesViewHolder(
        private val itemBinding: ItemMedicationTypeBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        fun bind(position: Int, item: MedicationType) {
            itemBinding.tvMedicationType.text = item.name
            itemBinding.medicalTypeUrl = item.url
            val isSelected = selectedPosition == position
            itemBinding.btnMedication.isChecked = isSelected

            val backgroundId = if (isSelected)
                com.cancer.yaqeen.R.drawable.background_selected_view
            else
                com.cancer.yaqeen.R.drawable.background_unselected_view

            itemBinding.container.setBackgroundResource(backgroundId)

            changeCircleColorOfRadioButton()

            itemBinding.container.setOnClickListener {
                notifyItemChangedByPosition(position)
                onItemClick(item)
            }
        }

        private fun changeCircleColorOfRadioButton() {
            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-R.attr.state_checked), // unchecked
                    intArrayOf(R.attr.state_checked)    // checked
                ),
                intArrayOf(
                    ContextCompat.getColor(_context, com.cancer.yaqeen.R.color.light_black), // unchecked
                    ContextCompat.getColor(_context, com.cancer.yaqeen.R.color.primary_color) // checked
                )
            )

            // Apply color state list to the radio button
            itemBinding.btnMedication.buttonTintList = colorStateList
        }
    }

    private fun notifyItemChangedByPosition(position: Int) {
        selectedPosition = position

        notifyItemChanged(lastSelectedPosition)
        notifyItemChanged(selectedPosition)

        lastSelectedPosition = selectedPosition
    }
}