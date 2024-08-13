package com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medical_reminder.models.MedicalReminder
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.databinding.ItemMedicalAppointmentBinding
import com.cancer.yaqeen.databinding.ItemMedicalAppointmentWithSymptomsBinding
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate

class MedicalRemindersAdapter(
    private var items: MutableList<MedicalReminder> = arrayListOf(),
    private val onEditClick: (MedicalReminder) -> Unit = {},
    private val onDeleteClick: (MedicalReminder) -> Unit = {},
) :
    ListAdapter<MedicalReminder, RecyclerView.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<MedicalReminder>() {
        override fun areItemsTheSame(
            oldItem: MedicalReminder,
            newItem: MedicalReminder
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: MedicalReminder,
            newOrder: MedicalReminder
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        when(viewType){
            0 -> {
                val binding =
                    ItemMedicalAppointmentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MedicalRemindersViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemMedicalAppointmentWithSymptomsBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                MedicalRemindersWithSymptomViewHolder(binding)
            }
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            when (holder) {
                is MedicalRemindersViewHolder -> {
                    holder.bind(it)
                }
                is MedicalRemindersWithSymptomViewHolder -> {
                    holder.bind(it)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position).symptom){
            null -> 0
            else -> 1
        }
    }

    fun setList(list: List<MedicalReminder>?) {
        submitList(list)

        if (items.isEmpty())
            items = (list)?.toMutableList() ?: arrayListOf()
    }

    fun deleteMedicalReminder(medicalReminderId: Int) {
        val position = items.indexOfFirst { it.id == medicalReminderId }

        if (position == -1)
            return

        if (items.size == 1)
            items = arrayListOf()
        else
            items.removeAt(position)
        super.submitList(items)
        notifyDataSetChanged()
    }

    private fun getReminderBeforeTime(context: Context, reminderBefore: ReminderBefore): String =
        if (reminderBefore.isMoreThanOrEqualHour)
            context.getString(R.string.before_time_hour, reminderBefore.timeDigits)
        else
            context.getString(R.string.before_time_min, reminderBefore.timeDigits)

    inner class MedicalRemindersViewHolder(
        private val itemBinding: ItemMedicalAppointmentBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context
        private var reminderBeforeTime = ""
        private var date = ""

        fun bind(item: MedicalReminder) {
            with(item){
                reminderBeforeTime = getReminderBeforeTime(_context, reminderBefore)
                date = convertMilliSecondsToDate(startDate)

                itemBinding.tvDoctorName.text = doctorName
                itemBinding.tvDoctorName2.text = doctorName
                itemBinding.tvDate.text = date
                itemBinding.tvNotesVal.text = notes
                itemBinding.tvLocationVal.text = location
                itemBinding.tvDaysVal.text = date
                itemBinding.tvTimeVal.text = reminderTime?.run {
                    val timing = if (isAM) _context.getString(R.string.am) else _context.getString(R.string.pm)
                    "$text $timing"
                }
                itemBinding.tvReminderVal.text = reminderBeforeTime


                itemBinding.layoutLess.changeVisibility(show = true)
                itemBinding.layoutMore.changeVisibility(show = false, isGone = true)
            }

            setListener(item)
        }

        private fun setListener(item: MedicalReminder) {
            itemBinding.tvShowMore.setOnClickListener {
                itemBinding.layoutLess.changeVisibility(show = false, isGone = true)
                itemBinding.layoutMore.changeVisibility(show = true)
            }

            itemBinding.tvShowLess.setOnClickListener {
                itemBinding.layoutMore.changeVisibility(show = false, isGone = true)
                itemBinding.layoutLess.changeVisibility(show = true)
            }

            itemBinding.btnEdit.setOnClickListener {
                onEditClick(item)
            }

            itemBinding.btnDelete.setOnClickListener {
                onDeleteClick(item)
            }
        }
     }

    inner class MedicalRemindersWithSymptomViewHolder(
        private val itemBinding: ItemMedicalAppointmentWithSymptomsBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context
        var symptomTypes = ""
        private var symptomImgUrl: String? = ""
        private var reminderBeforeTime = ""

        fun bind(item: MedicalReminder) {
            with(item){
                reminderBeforeTime = getReminderBeforeTime(_context, reminderBefore)
                symptomTypes = symptom?.symptomTypes?.joinToString(separator = "\n"){ it.name } ?: ""
                symptomImgUrl = symptom?.photosList?.firstOrNull()?.url

                itemBinding.tvDoctorName.text = doctorName
                itemBinding.tvDoctorName2.text = doctorName
                itemBinding.tvSymptomTypes.text = symptomTypes
                itemBinding.tvSymptomTypes2.text = symptomTypes
                bindImage(itemBinding.ivSymptom, symptomImgUrl)
                bindImage(itemBinding.ivSymptom2, symptomImgUrl)
                itemBinding.tvNotesVal.text = notes
                itemBinding.tvLocationVal.text = location
                itemBinding.tvDaysVal.text = convertMilliSecondsToDate(startDate)
                itemBinding.tvTimeVal.text = reminderTime?.run {
                    val timing = if (isAM) _context.getString(R.string.am) else _context.getString(R.string.pm)
                    "$text $timing"
                }
                itemBinding.tvReminderVal.text = reminderBeforeTime


                itemBinding.layoutLess.changeVisibility(show = true)
                itemBinding.layoutMore.changeVisibility(show = false, isGone = true)
            }

            setListener(item)
        }

        private fun setListener(item: MedicalReminder) {
            itemBinding.tvShowMore.setOnClickListener {
                itemBinding.layoutLess.changeVisibility(show = false, isGone = true)
                itemBinding.layoutMore.changeVisibility(show = true)
            }

            itemBinding.tvShowLess.setOnClickListener {
                itemBinding.layoutMore.changeVisibility(show = false, isGone = true)
                itemBinding.layoutLess.changeVisibility(show = true)
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