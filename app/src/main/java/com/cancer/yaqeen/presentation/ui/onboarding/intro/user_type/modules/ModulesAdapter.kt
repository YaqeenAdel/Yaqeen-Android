package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.modules

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.models.CancerType
import com.cancer.yaqeen.data.features.onboarding.models.Module
import com.cancer.yaqeen.databinding.ItemModuleBinding
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage
import java.util.Locale

class ModulesAdapter(
    private var items: List<Module> = listOf(),
    private val onItemClick: (Module) -> Unit
) :
    ListAdapter<Module, ModulesAdapter.ModulesViewHolder>(Companion), Filterable {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

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
        val item = currentList[position]
        item?.let {
            holder.bind(position, it)
        }
    }
    fun setList(list: List<Module>?) {
        submitList(list)

        items = list ?: listOf()
    }

    fun selectItem(id: Int){
        val positionItem = currentList.indexOfFirst {
            it.id.toInt() == id
        }
        notifyItemChangedByPosition(positionItem)
    }

    fun allItemsUnSelected(): Boolean =
        !currentList.any {
            it.selected
        }


    inner class ModulesViewHolder(
        private val itemBinding: ItemModuleBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {


        fun bind(position: Int, item: Module) {
            itemBinding.tvModuleName.text = item.moduleName
            bindImage(itemBinding.ivModuleIcon, item.icon)
//            val isSelected = selectedPosition == position
            if(item.selected){
                bindResourceImage(itemBinding.radioButton, R.drawable.ic_checked)
//                itemBinding.viewSelected.visibility = View.VISIBLE
//                itemBinding.viewUnselected.visibility = View.INVISIBLE
                itemBinding.cardContainer.strokeColor =
                    ContextCompat.getColor(itemBinding.viewSelected.context, R.color.primary_color)
                itemBinding.itemContainer.setBackgroundColor(ContextCompat.getColor(itemBinding.viewSelected.context, R.color.primary_color_30))
                itemBinding.ivModuleIcon.setBackgroundColor(ContextCompat.getColor(itemBinding.viewSelected.context, R.color.white))
                itemBinding.tvModuleName.setTextColor(ContextCompat.getColor(itemBinding.viewSelected.context, R.color.primary_color))
            }else {
                bindResourceImage(itemBinding.radioButton, R.drawable.ic_uncheck_circle)
//                itemBinding.viewSelected.visibility = View.INVISIBLE
//                itemBinding.viewUnselected.visibility = View.VISIBLE
                itemBinding.cardContainer.strokeColor =
                    ContextCompat.getColor(itemBinding.viewSelected.context, R.color.light_gray)
                itemBinding.itemContainer.setBackgroundColor(ContextCompat.getColor(itemBinding.viewSelected.context, R.color.white))
                itemBinding.ivModuleIcon.setBackgroundColor(ContextCompat.getColor(itemBinding.viewSelected.context, R.color.light_gray))
                itemBinding.tvModuleName.setTextColor(ContextCompat.getColor(itemBinding.viewSelected.context, R.color.light_black))
//                val textViewColor: Int = itemBinding.tvModuleName.textColors.defaultColor
//                val imageViewColor: Int = itemBinding.ivModuleIcon.solidColor
//                itemBinding.tvModuleName.setTextColor(Color.argb(128, Color.red(textViewColor), Color.green(textViewColor), Color.blue(textViewColor)))
//                itemBinding.ivModuleIcon.setBackgroundColor(Color.argb(10, Color.red(imageViewColor), Color.green(imageViewColor), Color.blue(imageViewColor)))

            }

            itemBinding.itemContainer.setOnClickListener {
                item.selected = !item.selected
                onItemClick(item)
                notifyItemChanged(adapterPosition)
            }

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