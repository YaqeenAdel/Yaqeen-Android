package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.cancer_type

import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.onboarding.models.CancerType
import com.cancer.yaqeen.databinding.ItemCancerTypeBinding

class CancerTypesAdapter(
    private val onItemClick: (CancerType) -> Unit
) :
    ListAdapter<CancerType, CancerTypesAdapter.CancerTypesViewHolder>(Companion) {

    private var selectedPosition = 0
    private var lastSelectedPosition = 0

    companion object : DiffUtil.ItemCallback<CancerType>() {
        override fun areItemsTheSame(
            oldItem: CancerType,
            newItem: CancerType
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: CancerType,
            newOrder: CancerType
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CancerTypesViewHolder {
        val binding =
            ItemCancerTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return CancerTypesViewHolder(binding) {
            onItemClick(currentList[it])
        }
    }

    override fun onBindViewHolder(holder: CancerTypesViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(position, it)
        }
    }

    inner class CancerTypesViewHolder(
        private val itemBinding: ItemCancerTypeBinding,
        onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.btnCancerType.setOnClickListener {
                onItemClick(adapterPosition)

                selectedPosition = adapterPosition

                notifyItemChanged(lastSelectedPosition)
                notifyItemChanged(selectedPosition)

                lastSelectedPosition = selectedPosition
            }
        }

        fun bind(position: Int, item: CancerType) {
            itemBinding.btnCancerType.text = item.typeName
            val isSelected = selectedPosition == position
            itemBinding.btnCancerType.isChecked = isSelected
        }
    }
}