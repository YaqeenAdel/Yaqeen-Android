package com.cancer.yaqeen.presentation.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.models.Time
import com.cancer.yaqeen.databinding.ItemTimeBinding
import com.cancer.yaqeen.presentation.util.changeVisibility

class TimesAdapter(
    private val onItemClick: (Time) -> Unit
) :
    ListAdapter<Time, TimesAdapter.TimesViewHolder>(Companion) {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

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
            ItemTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

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


    inner class TimesViewHolder(
        private val itemBinding: ItemTimeBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context


        fun bind(position: Int, item: Time) {
            val isSelected = selectedPosition == position
            itemBinding.tvTime.text = item.time

            itemBinding.ivCurve.changeVisibility(show = isSelected, isGone = true)

            if (isSelected){
                itemBinding.tvTime.setTextColor(ContextCompat.getColor(_context, R.color.primary_color))
                itemBinding.itemContainer.backgroundTintList = ContextCompat.getColorStateList(_context, R.color.white)
            }else {
                itemBinding.tvTime.setTextColor(ContextCompat.getColor(_context, R.color.black))
                itemBinding.itemContainer.backgroundTintList = ContextCompat.getColorStateList(_context, android.R.color.transparent)
            }

//            itemBinding.itemContainer.setOnClickListener {
//                onItemClick(item)
//
//                notifyItemChangedByPosition(position)
//            }
        }

     }

    private fun notifyItemChangedByPosition(position: Int) {
        selectedPosition = position

        notifyItemChanged(lastSelectedPosition)
        notifyItemChanged(selectedPosition)

        lastSelectedPosition = selectedPosition
    }


}