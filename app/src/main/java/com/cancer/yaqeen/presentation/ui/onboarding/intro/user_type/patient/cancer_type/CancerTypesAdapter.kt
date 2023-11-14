package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.patient.cancer_type

import android.R
import android.content.res.ColorStateList
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.onboarding.models.CancerType
import com.cancer.yaqeen.databinding.ItemCancerTypeBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class CancerTypesAdapter(
    private var items: List<CancerType> = listOf(),
    private val onItemClick: (CancerType) -> Unit
) :
    ListAdapter<CancerType, CancerTypesAdapter.CancerTypesViewHolder>(Companion), Filterable {

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

    fun selectItem(id: Int){
        val positionItem = currentList.indexOfFirst {
            it.id.toInt() == id
        }
        notifyItemChangedByPosition(positionItem)
    }

    fun setList(list: List<CancerType>?) {
        submitList(list)

        items = list ?: listOf()
    }


    inner class CancerTypesViewHolder(
        private val itemBinding: ItemCancerTypeBinding,
        onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        init {
            itemBinding.btnCancerType.setOnClickListener {
                onItemClick(adapterPosition)

                notifyItemChangedByPosition(adapterPosition)
            }
        }

        fun bind(position: Int, item: CancerType) {
            itemBinding.btnCancerType.text = item.typeName
            val isSelected = selectedPosition == position
            itemBinding.btnCancerType.isChecked = isSelected
            changeCircleColorOfRadioButton()
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
            itemBinding.btnCancerType.buttonTintList = colorStateList
        }
    }

    private fun notifyItemChangedByPosition(position: Int) {
        selectedPosition = position

        notifyItemChanged(lastSelectedPosition)
        notifyItemChanged(selectedPosition)

        lastSelectedPosition = selectedPosition
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                if (filterResults.values != null)
                    submitList(filterResults.values as List<CancerType>)
                notifyDataSetChanged()
            }
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase(Locale.ROOT)
                val filterResults = FilterResults()
                filterResults.values = if (queryString.isNullOrEmpty())
                    items
                else
                    items.filter {
                        it.typeName.trim().toLowerCase(Locale.ROOT).contains(queryString)
                    }
                return filterResults
            }
        }
    }
}