package com.cancer.yaqeen.presentation.ui.main.treatment.medications.strength.choose_time

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.models.Day
import com.cancer.yaqeen.databinding.ItemDayBinding

class DaysAdapter(
    private val onItemClick: (Day) -> Unit
) :
    ListAdapter<Day, DaysAdapter.DaysViewHolder>(Companion) {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    companion object : DiffUtil.ItemCallback<Day>() {
        override fun areItemsTheSame(
            oldItem: Day,
            newItem: Day
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: Day,
            newOrder: Day
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DaysViewHolder {
        val binding =
            ItemDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return DaysViewHolder(binding)
    }

    fun setList(list: List<Day>?) {
        submitList(list)
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
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


    inner class DaysViewHolder(
        private val itemBinding: ItemDayBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context


        fun bind(position: Int, item: Day) {
            val isSelected = item.selected
            itemBinding.tvDay.text = item.name

            if (isSelected){
                itemBinding.tvDay.setTextColor(ContextCompat.getColor(_context, R.color.primary_color))
                itemBinding.itemContainer.backgroundTintList = ContextCompat.getColorStateList(_context, R.color.header_background)
            }else {
                itemBinding.tvDay.setTextColor(ContextCompat.getColor(_context, R.color.black))
                itemBinding.itemContainer.backgroundTintList = ContextCompat.getColorStateList(_context, android.R.color.white)
            }

            itemBinding.itemContainer.setOnClickListener {
                item.selected = !item.selected
                onItemClick(item)
                notifyItemChangedByPosition(position)
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