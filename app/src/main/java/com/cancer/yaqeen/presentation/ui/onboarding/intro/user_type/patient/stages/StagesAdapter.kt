package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.stages

import android.R
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.onboarding.models.Stage
import com.cancer.yaqeen.databinding.ItemCancerStageBinding
import com.cancer.yaqeen.databinding.ItemCancerTypeBinding
import com.cancer.yaqeen.databinding.ItemStageBinding

class StagesAdapter(
    private val onItemClick: (Stage) -> Unit
) :
    ListAdapter<Stage, StagesAdapter.StagesViewHolder>(Companion) {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

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
            ItemCancerStageBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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
        private val itemBinding: ItemCancerStageBinding,
        onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        init {
            itemBinding.btnCancerStage.setOnClickListener {
                onItemClick(adapterPosition)

                notifyItemChangedByPosition(adapterPosition)
            }
        }

        fun bind(position: Int, item: Stage) {
            itemBinding.btnCancerStage.text = item.stageName
//            itemBinding.tvStageNo.text = item.number.toString()
            val isSelected = selectedPosition == position
//            itemBinding.viewSelected.visibility = if(isSelected) View.VISIBLE else View.INVISIBLE
            itemBinding.btnCancerStage.isChecked = isSelected
            changeCircleColorOfRadioButton()
            itemBinding.btnCancerStage.isSelected = true
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
            itemBinding.btnCancerStage.buttonTintList = colorStateList
        }
    }

    private fun notifyItemChangedByPosition(position: Int) {
        selectedPosition = position

        notifyItemChanged(lastSelectedPosition)
        notifyItemChanged(selectedPosition)

        lastSelectedPosition = selectedPosition
    }
}