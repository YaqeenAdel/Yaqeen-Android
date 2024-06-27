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
import com.cancer.yaqeen.presentation.util.dpToPx
import com.cancer.yaqeen.presentation.util.recyclerview.HorizontalMarginItemDecoration

class SymptomsAdapter(
    private val itemClickable: Boolean = false,
    private var items: MutableList<Symptom> = arrayListOf(),
    private val onEditClick: (Symptom) -> Unit = {},
    private val onDeleteClick: (Symptom) -> Unit = {},
    private val onItemClick: (Symptom) -> Unit = {},
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
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }
    fun setList(list: List<Symptom>?) {
        submitList(list)

        if (items.isEmpty())
            items = (list)?.toMutableList() ?: arrayListOf()
    }

    fun deleteSymptom(symptomId: Int) {
        val position = items.indexOfFirst { it.id == symptomId }

        if (position == -1)
            return

        items.removeAt(position)
        super.submitList(items)
        notifyDataSetChanged()
    }

    inner class SymptomsViewHolder(
        private val itemBinding: ItemSymptomBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        private lateinit var photosAdapter: SmallPhotosAdapter

        private var types = ""
        private var isReminder = false

        fun bind(item: Symptom) {
            with(item){
                types = symptomTypes?.joinToString(separator = "\n"){ it.name } ?: ""
                isReminder = doctorName?.isNotEmpty() == true

                bindImage(itemBinding.ivSymptom, photosList?.firstOrNull()?.url)

                photosList?.let {
                    setupPhotosAdapter(photosList!!.map { it.url ?: "" })
                }

                itemBinding.tvSymptomTypes.text = types
                itemBinding.tvSymptomsVal.text = types
                itemBinding.tvNotesVal.text = details
                itemBinding.tvReminderVal.text = doctorName ?: ""
                itemBinding.tvReminder.changeVisibility(show = isReminder, isGone = true)
                itemBinding.tvReminderVal.changeVisibility(show = isReminder, isGone = true)
                itemBinding.tvDateTimeVal.text = "$reminderTime - $startDate"

                itemBinding.layoutLess.changeVisibility(show = true)
                itemBinding.layoutMore.changeVisibility(show = false, isGone = true)

                itemBinding.linearLayout.changeVisibility(show = !itemClickable, isGone = true)
            }

            setListener(item)
        }

        private fun setListener(item: Symptom) {
            itemBinding.btnShowMore.setOnClickListener {
                itemBinding.layoutMore.changeVisibility(show = true)
                itemBinding.layoutLess.changeVisibility(show = false, isGone = true)
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

            if (itemClickable)
                itemBinding.itemContainer.setOnClickListener {
                    onItemClick(item)
                }
        }

        private fun setupPhotosAdapter(photosList: List<String>) {
            photosAdapter = SmallPhotosAdapter {

            }

            itemBinding.rvSymptomPhotos.apply {
                adapter = photosAdapter
            }

            photosAdapter.submitList(photosList)

        }
     }

}