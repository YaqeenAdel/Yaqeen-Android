package com.cancer.yaqeen.presentation.ui.auth.intro.user_type.doctor.specialization.university

import android.R
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.onboarding.models.University
import com.cancer.yaqeen.databinding.ItemUniversityBinding
import java.util.Locale

class UniversitiesAdapter(
    private var items: List<University> = listOf(),
    private val onItemClick: (University) -> Unit
) :
    ListAdapter<University, UniversitiesAdapter.UniversitiesViewHolder>(Companion), Filterable {

    private var selectedPosition = -1
    private var lastSelectedPosition = -1

    companion object : DiffUtil.ItemCallback<University>() {
        override fun areItemsTheSame(
            oldItem: University,
            newItem: University
        ): Boolean {
            return oldItem.universityID == newItem.universityID
        }

        override fun areContentsTheSame(
            oldOrder: University,
            newOrder: University
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UniversitiesViewHolder {
        val binding =
            ItemUniversityBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return UniversitiesViewHolder(binding) {
            onItemClick(currentList[it])
        }
    }

    override fun onBindViewHolder(holder: UniversitiesViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(position, it)
        }
    }

    fun selectItem(id: Int){
        val positionItem = currentList.indexOfFirst {
            it.universityID.toInt() == id
        }
        notifyItemChangedByPosition(positionItem)
    }

    fun setList(list: List<University>?, universityIdSelected: String?) {
        submitList(list)

        items = list ?: listOf()

        universityIdSelected?.let {
            val positionUniversitySelected = currentList.indexOfFirst {
                it.universityID.toString() == universityIdSelected
            }
            selectedPosition = positionUniversitySelected
            notifyItemChangedByPosition(positionUniversitySelected)
        }
    }


    inner class UniversitiesViewHolder(
        private val itemBinding: ItemUniversityBinding,
        onItemClick: (Int) -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        init {
            itemBinding.btnUniversity.setOnClickListener {
                onItemClick(adapterPosition)

                notifyItemChangedByPosition(adapterPosition)
            }
        }

        fun bind(position: Int, item: University) {
            itemBinding.btnUniversity.text = item.universityName
            val isSelected = selectedPosition == position
            itemBinding.btnUniversity.isChecked = isSelected
            changeCircleColorOfRadioButton()
            itemBinding.btnUniversity.isSelected = true
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
            itemBinding.btnUniversity.buttonTintList = colorStateList
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
                    submitList(filterResults.values as List<University>)
                notifyDataSetChanged()
            }
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase(Locale.ROOT)
                val filterResults = FilterResults()
                filterResults.values = if (queryString.isNullOrEmpty())
                    items
                else
                    items.filter {
                        it.universityName.trim().toLowerCase(Locale.ROOT).contains(queryString)
                    }
                return filterResults
            }
        }
    }
}