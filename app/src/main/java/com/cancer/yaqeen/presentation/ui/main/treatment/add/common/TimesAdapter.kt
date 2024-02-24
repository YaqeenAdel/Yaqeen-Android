package com.cancer.yaqeen.presentation.ui.main.treatment.add.common

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Time
import com.cancer.yaqeen.databinding.ItemHourMinuteBinding
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.changeVisibility

class TimesAdapter(
    private val onItemClick: (Time) -> Unit
) :
    ListAdapter<Time, TimesAdapter.TimesViewHolder>(Companion) {

    private var selectedPosition = Constants.INIT_SELECTED_POSITION
    private var lastSelectedPosition = Constants.INIT_SELECTED_POSITION

    companion object : DiffUtil.ItemCallback<Time>() {
        override fun areItemsTheSame(
            oldItem: Time,
            newItem: Time
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: Time,
            newOrder: Time
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TimesViewHolder {
        val binding =
            ItemHourMinuteBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TimesViewHolder(binding)
    }

    fun setList(list: List<Time>?) {
        submitList(list)
    }

    override fun onBindViewHolder(holder: TimesViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(position, it)
        }
    }


    fun selectItem(id: Int): Int{
        val positionItem = currentList.indexOfFirst {
            it.id == id
        }
        notifyItemChangedByPosition(positionItem)
        return positionItem
    }

    fun anyItemIsSelected(): Boolean =
        selectedPosition != Constants.INIT_SELECTED_POSITION

    fun getItemSelected(): Time =
        currentList[selectedPosition]

    inner class TimesViewHolder(
        private val itemBinding: ItemHourMinuteBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context


        fun bind(position: Int, item: Time) {
            val isSelected = selectedPosition == position
            itemBinding.tvTime.text = item.time

            itemBinding.viewLine.changeVisibility(!isSelected)

            if (isSelected){
                itemBinding.itemContainer.setBackgroundResource(R.drawable.background_view_radius_0)
            }else {
                itemBinding.itemContainer.background = null
            }

            itemBinding.itemContainer.setOnClickListener {
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