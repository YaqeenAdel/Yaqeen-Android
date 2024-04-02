package com.cancer.yaqeen.presentation.ui.main.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Medication
import com.cancer.yaqeen.data.features.home.schedule.medication.models.Schedule
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType
import com.cancer.yaqeen.data.features.home.schedule.medication.models.ScheduleType.Companion.getTypeId
import com.cancer.yaqeen.databinding.ItemAppointmentTodayBinding
import com.cancer.yaqeen.databinding.ItemMedicationBinding
import com.cancer.yaqeen.databinding.ItemMedicationTodayBinding
import com.cancer.yaqeen.databinding.ItemRoutineTestTodayBinding
import com.cancer.yaqeen.presentation.ui.main.treatment.getMedicationType
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImageURI
import com.cancer.yaqeen.presentation.util.binding_adapters.bindResourceImage

class SchedulesAdapter(
    private val onMedicationClick: (Schedule) -> Unit,
) :
    ListAdapter<Schedule, RecyclerView.ViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<Schedule>() {
        override fun areItemsTheSame(
            oldItem: Schedule,
            newItem: Schedule
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: Schedule,
            newOrder: Schedule
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getTypeId(currentList[position].scheduleType)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder =
        when(viewType){
            ScheduleType.MEDICATION.id -> {
                val binding =
                    ItemMedicationTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TodayMedicationsViewHolder(binding)
            }
            ScheduleType.ROUTINE_TESTS.id -> {
                val binding =
                    ItemRoutineTestTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TodayMedicalRoutineTestsViewHolder(binding)
            }
            ScheduleType.MEDICAL_REMINDER.id -> {
                val binding =
                    ItemAppointmentTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TodayMedicalAppointmentsViewHolder(binding)
            }
            else -> {
                val binding =
                    ItemMedicationTodayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                TodayMedicationsViewHolder(binding)
            }
        }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            when (holder) {
                is TodayMedicationsViewHolder -> {
                    holder.bind(it)
                }
                is TodayMedicalRoutineTestsViewHolder -> {
                    holder.bind(it)
                }
                is TodayMedicalAppointmentsViewHolder -> {
                    holder.bind(it)
                }
                else -> {}
            }
        }
    }

    inner class TodayMedicationsViewHolder(
        private val itemBinding: ItemMedicationTodayBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        fun bind(item: Schedule) {
            with(item){
//                val timing = if(reminderTime?.isAM == true) _context.getString(R.string.am) else _context.getString(R.string.pm)
//                itemBinding.tvTime.text = "${reminderTime?.text} $timing"
                itemBinding.tvTime.text = scheduledTodayTime
                itemBinding.tvNotes.text = notes
                medication?.run {
                    itemBinding.tvMedicationDetails.text = "$medicationName, $strengthAmount $unitType"
                    itemBinding.tvDosageAmount.text = "$dosageAmount $medicationType"

                    getMedicationType(_context, medicationType)?.run { iconResId
                        bindResourceImage(itemBinding.ivMedicationIcon, iconResId)
                        bindResourceImage(itemBinding.ivMedicationIcon2, iconResId)
                    }
                }
            }

//            itemBinding.itemContainer.setOnClickListener {
//                onMedicationClick(item)
//            }
        }
     }

    inner class TodayMedicalAppointmentsViewHolder(
        private val itemBinding: ItemAppointmentTodayBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        fun bind(item: Schedule) {
            with(item){
//                val timing = if(reminderTime?.isAM == true) _context.getString(R.string.am) else _context.getString(R.string.pm)
//                itemBinding.tvTime.text = "${reminderTime?.text} $timing"
                itemBinding.tvTime.text = scheduledTodayTime
                itemBinding.tvNotes.text = notes
                appointment?.run {
                    itemBinding.tvDoctorName.text = doctorName
                }
            }

//            itemBinding.itemContainer.setOnClickListener {
//                onMedicationClick(item)
//            }
        }
     }

    inner class TodayMedicalRoutineTestsViewHolder(
        private val itemBinding: ItemRoutineTestTodayBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context

        fun bind(item: Schedule) {
            with(item){
//                val timing = if(reminderTime?.isAM == true) _context.getString(R.string.am) else _context.getString(R.string.pm)
//                itemBinding.tvTime.text = "${reminderTime?.text} $timing"
                itemBinding.tvTime.text = scheduledTodayTime
                itemBinding.tvNotes.text = notes
                routineTest?.run {
                    itemBinding.tvRoutineTestName.text = routineTestName
                }
            }

//            itemBinding.itemContainer.setOnClickListener {
//                onMedicationClick(item)
//            }
        }
     }

}