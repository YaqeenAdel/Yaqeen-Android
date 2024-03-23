package com.cancer.yaqeen.presentation.ui.main.treatment.add.symptoms

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.SymptomType
import com.cancer.yaqeen.databinding.ItemSymptomTypeBinding
import com.cancer.yaqeen.presentation.util.Constants

class SymptomsTypesAdapter(
    private var items: List<SymptomType> = listOf(),
    private val onItemClick: (SymptomType) -> Unit
) :
    ListAdapter<SymptomType, SymptomsTypesAdapter.SymptomsTypesViewHolder>(Companion) {

    private var selectedPosition = Constants.INIT_SELECTED_POSITION
    private var lastSelectedPosition = Constants.INIT_SELECTED_POSITION

    companion object : DiffUtil.ItemCallback<SymptomType>() {
        override fun areItemsTheSame(
            oldItem: SymptomType,
            newItem: SymptomType
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: SymptomType,
            newOrder: SymptomType
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SymptomsTypesViewHolder {
        val binding =
            ItemSymptomTypeBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SymptomsTypesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SymptomsTypesViewHolder, position: Int) {
        val item = items[position]
        item?.let {
            holder.bind(position, it)
        }
    }
    fun setList(list: List<SymptomType>?) {
        submitList(list)

        items = list ?: listOf()
    }

    fun getItemsSelected(): List<SymptomType> =
        items.filter { it.selected }

    fun anyItemIsSelected(): Boolean =
        items.any { it.selected }


    fun selectItems(types: List<SymptomType>?) {
        types?.forEach { type ->
            items.firstOrNull {
                it.id == type.id
            }?.selected = true
        }

        notifyDataSetChanged()
    }


    inner class SymptomsTypesViewHolder(
        private val itemBinding: ItemSymptomTypeBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {


        private val _context = itemBinding.root.context

        fun bind(position: Int, item: SymptomType) {
            itemBinding.btnSymptom.text = item.name
            itemBinding.btnSymptom.isChecked = item.selected

            changeCircleColorOfRadioButton()

            itemBinding.itemContainer.setOnClickListener {
                item.selected = !item.selected
                onItemClick(item)
                notifyItemChanged(position)
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
            itemBinding.btnSymptom.buttonTintList = colorStateList
        }
    }

    private fun notifyItemChangedByPosition(position: Int) {
        selectedPosition = position

        notifyItemChanged(lastSelectedPosition)
        notifyItemChanged(selectedPosition)

        lastSelectedPosition = selectedPosition
    }

}