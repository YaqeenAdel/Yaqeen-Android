package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.stages

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.onboarding.models.Stage
import com.cancer.yaqeen.databinding.ItemStageBinding

class StagesAdapter(
    private val onItemClick: (Stage) -> Unit
) :
    ListAdapter<Stage, StagesAdapter.StagesViewHolder>(Companion) {

    private var selectedPosition = 0
    private var lastSelectedPosition = 0

    companion object : DiffUtil.ItemCallback<Stage>() {
        override fun areItemsTheSame(
            oldItem: Stage,
            newItem: Stage
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: Stage,
            newOrder: Stage
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): StagesViewHolder {
        val binding =
            ItemStageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return StagesViewHolder(binding) {
            onItemClick(currentList[it])
        }
    }

    override fun onBindViewHolder(holder: StagesViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(position, it)
        }
    }

    fun selectItem(id: Int){
        val positionItem = currentList.indexOfFirst {
            it.id.toInt() == id
        }
        notifyItemChangedByPosition(positionItem)
    }

    inner class StagesViewHolder(
        private val itemBinding: ItemStageBinding,
        onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.itemContainer.setOnClickListener {
                onItemClick(adapterPosition)

                notifyItemChangedByPosition(adapterPosition)
            }
        }

        fun bind(position: Int, item: Stage) {
            itemBinding.tvStageName.text = item.stageName
            itemBinding.tvStageNo.text = item.number.toString()
            val isSelected = selectedPosition == position
            itemBinding.viewSelected.visibility = if(isSelected) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun notifyItemChangedByPosition(position: Int) {
        selectedPosition = position

        notifyItemChanged(lastSelectedPosition)
        notifyItemChanged(selectedPosition)

        lastSelectedPosition = selectedPosition
    }
}