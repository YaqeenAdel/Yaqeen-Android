package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.modules

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.onboarding.models.Module
import com.cancer.yaqeen.databinding.ItemModuleBinding
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage

class ModulesAdapter(
    private val onItemClick: (Module) -> Unit
) :
    ListAdapter<Module, ModulesAdapter.ModulesViewHolder>(Companion) {

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

        return ModulesViewHolder(binding) {
            onItemClick(currentList[it])
        }
    }

    override fun onBindViewHolder(holder: ModulesViewHolder, position: Int) {
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

    inner class ModulesViewHolder(
        private val itemBinding: ItemModuleBinding,
        onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        init {
            itemBinding.itemContainer.setOnClickListener {
                onItemClick(adapterPosition)

                notifyItemChangedByPosition(adapterPosition)
            }
        }

        fun bind(position: Int, item: Module) {
            itemBinding.tvModuleName.text = item.moduleName
            bindImage(itemBinding.ivModuleIcon, item.icon)
            val isSelected = selectedPosition == position
            if(isSelected){
                bindResourceImage(itemBinding.radioButton, R.drawable.ic_check_circle)
//                itemBinding.viewSelected.visibility = View.VISIBLE
//                itemBinding.viewUnselected.visibility = View.INVISIBLE
//                itemBinding.ivModuleIcon.setBackgroundColor(ContextCompat.getColor(itemBinding.viewSelected.context, R.color.silver_medal))
//                itemBinding.tvModuleName.setTextColor(ContextCompat.getColor(itemBinding.viewSelected.context, R.color.black))
            }else {
                bindResourceImage(itemBinding.radioButton, R.drawable.ic_uncheck_circle)
//                itemBinding.viewSelected.visibility = View.INVISIBLE
//                itemBinding.viewUnselected.visibility = View.VISIBLE
//                val textViewColor: Int = itemBinding.tvModuleName.textColors.defaultColor
//                val imageViewColor: Int = itemBinding.ivModuleIcon.solidColor
//                itemBinding.tvModuleName.setTextColor(Color.argb(128, Color.red(textViewColor), Color.green(textViewColor), Color.blue(textViewColor)))
//                itemBinding.ivModuleIcon.setBackgroundColor(Color.argb(10, Color.red(imageViewColor), Color.green(imageViewColor), Color.blue(imageViewColor)))

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