package com.cancer.yaqeen.presentation.ui.main.treatment.history

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.databinding.ItemMedicationBinding

class MedicationsAdapter(
    private val onItemClick: (Medication) -> Unit,
) :
    ListAdapter<Medication, MedicationsAdapter.MedicationsViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Medication>() {
        override fun areItemsTheSame(
            oldItem: Medication,
            newItem: Medication
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: Medication,
            newOrder: Medication
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MedicationsViewHolder {
        val binding =
            ItemMedicationBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return MedicationsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MedicationsViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(it)
        }
    }

    inner class MedicationsViewHolder(
        private val itemBinding: ItemMedicationBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        fun bind(item: Medication) {
            with(item){
                itemBinding.tvTime.text = time
                itemBinding.tvMedicationDetails.text = "$medicationName, $strengthAmount $unitType"
                itemBinding.tvNotes.text = notes
                itemBinding.tvDosageAmount.text = "$dosageAmount $medicationType"
            }

            itemBinding.itemContainer.setOnClickListener {
                onItemClick(item)
            }
        }
     }

}