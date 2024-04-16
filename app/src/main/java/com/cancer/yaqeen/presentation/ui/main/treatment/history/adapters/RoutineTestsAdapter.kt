package com.cancer.yaqeen.presentation.ui.main.treatment.history.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.R
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.ReminderBefore
import com.cancer.yaqeen.data.features.home.schedule.routine_test.models.RoutineTest
import com.cancer.yaqeen.databinding.ItemRoutineTestBinding
import com.cancer.yaqeen.presentation.util.binding_adapters.bindImage
import com.cancer.yaqeen.presentation.util.changeVisibility
import com.cancer.yaqeen.presentation.util.convertMilliSecondsToDate

class RoutineTestsAdapter(
    private var items: MutableList<RoutineTest> = arrayListOf(),
    private val onEditClick: (RoutineTest) -> Unit = {},
    private val onDeleteClick: (RoutineTest) -> Unit = {},
) :
    ListAdapter<RoutineTest, RoutineTestsAdapter.RoutineTestsViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<RoutineTest>() {
        override fun areItemsTheSame(
            oldItem: RoutineTest,
            newItem: RoutineTest
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: RoutineTest,
            newOrder: RoutineTest
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RoutineTestsViewHolder {
        val binding =
            ItemRoutineTestBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return RoutineTestsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RoutineTestsViewHolder, position: Int) {
        val item = getItem(position)
        item?.let {
            holder.bind(it)
        }
    }
    fun setList(list: List<RoutineTest>?) {
        submitList(list)

        if (items.isEmpty())
            items = (list)?.toMutableList() ?: arrayListOf()
    }

    fun deleteRoutineTest(RoutineTestId: Int) {
        val position = items.indexOfFirst { it.id == RoutineTestId }

        items.removeAt(position)
        super.submitList(items)
        notifyDataSetChanged()
    }

    private fun getReminderBeforeTime(context: Context, reminderBefore: ReminderBefore): String =
        if (reminderBefore.isMoreThanOrEqualHour)
            context.getString(R.string.before_time_hour, reminderBefore.timeDigits)
        else
            context.getString(R.string.before_time_min, reminderBefore.timeDigits)

    inner class RoutineTestsViewHolder(
        private val itemBinding: ItemRoutineTestBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private val _context = itemBinding.root.context
        private var reminderBeforeTime = ""

        var timingStr = ""

        fun bind(item: RoutineTest) {
            with(item){
                reminderBeforeTime = getReminderBeforeTime(_context, reminderBefore)
                bindImage(itemBinding.ivRoutineTest, photo?.url)
                bindImage(itemBinding.ivRoutineTest2, photo?.url)


                itemBinding.tvRoutineTestName.text = routineTestName
                itemBinding.tvRoutineTestName2.text = routineTestName
                itemBinding.tvNotesVal.text = notes
                reminderTime?.run {
                    timingStr = if (isAM) _context.getString(R.string.am) else _context.getString(R.string.pm)
                    itemBinding.tvTimeVal.text = "$text $timingStr"
                }
                itemBinding.tvReminderVal.text = reminderBeforeTime
                itemBinding.tvDateVal.text = convertMilliSecondsToDate(startDate)

                itemBinding.layoutLess.changeVisibility(show = true)
                itemBinding.layoutMore.changeVisibility(show = false, isGone = true)

            }

            setListener(item)
        }

        private fun setListener(item: RoutineTest) {
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
        }

     }

}