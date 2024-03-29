package com.cancer.yaqeen.presentation.ui.auth.intro.user_type.modules

import android.content.res.ColorStateList
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.models.Module
import com.cancer.yaqeen.databinding.ItemModuleBinding
import com.cancer.yaqeen.presentation.util.Constants
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import java.util.Locale

class ModulesAdapter(
    private var items: List<Module> = listOf(),
    private val onItemClick: (Module) -> Unit
) :
    ListAdapter<Module, ModulesAdapter.ModulesViewHolder>(Companion), Filterable {

    private var selectedPosition = Constants.INIT_SELECTED_POSITION
    private var lastSelectedPosition = Constants.INIT_SELECTED_POSITION

    companion object : DiffUtil.ItemCallback<Module>() {
        override fun areItemsTheSame(
            oldItem: Module,
            newItem: Module
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: Module,
            newOrder: Module
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ModulesViewHolder {
        val binding =
            ItemModuleBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return ModulesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ModulesViewHolder, position: Int) {
        val item = items[position]
        item?.let {
            holder.bind(it)
        }
    }
    fun setList(list: List<Module>?) {
        submitList(list)

        items = list ?: listOf()
    }

    fun selectItems(ids: MutableList<Int>?){
        ids?.forEach { id ->
            items.firstOrNull {
                it.id == id
            }?.selected = true
        }
        notifyDataSetChanged()
    }

    fun allItemsUnSelected(): Boolean =
        !items.any {
            it.selected
        }


    inner class ModulesViewHolder(
        private val itemBinding: ItemModuleBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {


        private val _context = itemBinding.root.context

        fun bind(item: Module) {
            itemBinding.tvModuleName.text = item.moduleName
            bindImage(itemBinding.ivModuleIcon, item.icon)
            itemBinding.btnInterest.isChecked = item.selected

            if (item.iconColor.isNotEmpty())
                itemBinding.ivModuleIcon.imageTintList = ColorStateList.valueOf(Color.parseColor(item.iconColor))


            if(item.selected){
                bindResourceImage(itemBinding.radioButton, R.drawable.ic_checked)
                itemBinding.cardContainer.strokeColor =
                    ContextCompat.getColor(_context, R.color.primary_color)
                itemBinding.itemContainer.setBackgroundColor(ContextCompat.getColor(_context, R.color.header_background))
                itemBinding.ivModuleIcon.setBackgroundColor(ContextCompat.getColor(_context, R.color.light_gray))
//                itemBinding.tvModuleName.setTextColor(ContextCompat.getColor(_context, R.color.primary_color))
            }else {
                bindResourceImage(itemBinding.radioButton, R.drawable.ic_uncheck_circle)
                itemBinding.cardContainer.strokeColor =
                    ContextCompat.getColor(_context, R.color.light_gray)
                itemBinding.itemContainer.setBackgroundColor(ContextCompat.getColor(_context, R.color.white))
                itemBinding.ivModuleIcon.setBackgroundColor(ContextCompat.getColor(_context, R.color.light_gray))
//                itemBinding.tvModuleName.setTextColor(ContextCompat.getColor(_context, R.color.light_black))
            }

            changeCircleColorOfRadioButton()
            itemBinding.btnInterest.isSelected = true

            itemBinding.itemContainer.setOnClickListener {
                item.selected = !item.selected
                onItemClick(item)
                notifyItemChanged(adapterPosition)
            }

        }
        private fun changeCircleColorOfRadioButton() {
            val colorStateList = ColorStateList(
                arrayOf(
                    intArrayOf(-android.R.attr.state_checked), // unchecked
                    intArrayOf(android.R.attr.state_checked)    // checked
                ),
                intArrayOf(
                    ContextCompat.getColor(_context, R.color.light_black), // unchecked
                    ContextCompat.getColor(_context, R.color.primary_color) // checked
                )
            )

            // Apply color state list to the radio button
            itemBinding.btnInterest.buttonTintList = colorStateList
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
                    submitList(filterResults.values as List<Module>)
                notifyDataSetChanged()
            }
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.lowercase(Locale.ROOT)
                val filterResults = FilterResults()
                filterResults.values = if (queryString.isNullOrEmpty())
                    items
                else
                    items.filter {
                        it.moduleName.trim().lowercase(Locale.ROOT).contains(queryString)
                    }
                return filterResults
            }
        }
    }
}