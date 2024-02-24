package com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.home.schedule.symptom.models.Symptom
import com.cancer.yaqeen.databinding.ItemSymptomBinding
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility

class SymptomsAdapter(
    private var items: MutableList<Symptom> = arrayListOf(),
    private val onEditClick: (Symptom) -> Unit,
    private val onDeleteClick: (Symptom) -> Unit,
) :
    ListAdapter<Symptom, SymptomsAdapter.SymptomsViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Symptom>() {
        override fun areItemsTheSame(
            oldItem: Symptom,
            newItem: Symptom
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: Symptom,
            newOrder: Symptom
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SymptomsViewHolder {
        val binding =
            ItemSymptomBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return SymptomsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SymptomsViewHolder, position: Int) {
        val item = items[position]
        item?.let {
            holder.bind(it)
        }
    }
    fun setList(list: List<Symptom>?) {
        submitList(list)

        items = (list)?.toMutableList() ?: arrayListOf()
    }

    fun deleteSymptom(symptomId: Int) {
        val position = items.indexOfFirst { it.id == symptomId }

        items.removeAt(position)
//        notifyItemRemoved(position)
    }

    inner class SymptomsViewHolder(
        private val itemBinding: ItemSymptomBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        private var types = ""
        private var isReminder = false

        fun bind(item: Symptom) {
            with(item){
                types = symptomTypes?.joinToString(separator = "\n"){ it.name } ?: ""
                isReminder = doctorName?.isNotEmpty() == true

                bindImage(itemBinding.ivSymptom, imageDownloadUrl)
                bindImage(itemBinding.ivSymptom2, imageDownloadUrl)
                itemBinding.tvSymptomTypes.text = types
                itemBinding.tvSymptomsVal.text = types
                itemBinding.tvNotesVal.text = details
                itemBinding.tvReminderVal.text = doctorName ?: ""
                itemBinding.tvReminder.changeVisibility(show = isReminder, isGone = true)
                itemBinding.tvReminderVal.changeVisibility(show = isReminder, isGone = true)
                itemBinding.tvDateTimeVal.text = "$reminderTime - $startDate"

                itemBinding.view.changeVisibility(show = (adapterPosition + 1) < itemCount, isGone = false)
            }

            itemBinding.btnShowMore.setOnClickListener {
                itemBinding.layoutLess.changeVisibility(show = false, isGone = true)
                itemBinding.layoutMore.changeVisibility(show = true)
            }

            itemBinding.btnShowLess.setOnClickListener {
                itemBinding.layoutLess.changeVisibility(show = true)
                itemBinding.layoutMore.changeVisibility(show = false, isGone = true)
            }

            itemBinding.btnEdit.setOnClickListener {
                onEditClick(item)
            }

            itemBinding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
     }

}