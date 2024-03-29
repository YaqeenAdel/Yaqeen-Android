package com.cancer.yaqeen.presentation.ui.main.treatment.add.medications.strength.choose_time

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Day
import com.cancer.yaqeen.databinding.ItemDayBinding
import com.cancer.yaqeen.presentation.util.Constants

class DaysAdapter(
    private var items: List<Day> = listOf(),
    private val onItemClick: (Day) -> Unit
) :
    ListAdapter<Day, DaysAdapter.DaysViewHolder>(Companion) {

    private var selectedPosition = Constants.INIT_SELECTED_POSITION
    private var lastSelectedPosition = Constants.INIT_SELECTED_POSITION

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

        items = list ?: listOf()
    }

    override fun onBindViewHolder(holder: DaysViewHolder, position: Int) {
        val item = items[position]
        item?.let {
            holder.bind(position, it)
        }
    }


    fun selectItem(id: Int): Int{
        val positionItem = items.indexOfFirst {
            it.id == id
        }
        notifyItemChangedByPosition(positionItem)
        return positionItem
    }

    fun getItemsSelected(): List<Day> =
        items.filter { it.selected }

    fun anyItemIsSelected(): Boolean =
        items.any { it.selected }


    fun selectItems(days: List<Day>?) {
        days?.forEach { day ->
            items.firstOrNull {
                it.id == day.id
            }?.selected = true
        }

        notifyDataSetChanged()
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