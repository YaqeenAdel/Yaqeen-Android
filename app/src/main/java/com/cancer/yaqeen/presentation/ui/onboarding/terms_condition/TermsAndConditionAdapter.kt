package com.cancer.yaqeen.presentation.ui.onboarding.terms_condition

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.data.features.onboarding.models.TermsAndCondition
import com.cancer.yaqeen.databinding.ItemTermsConditionBinding

class TermsAndConditionAdapter() :
    ListAdapter<TermsAndCondition, TermsAndConditionAdapter.TermsAndConditionsViewHolder>(Companion) {

    companion object : DiffUtil.ItemCallback<TermsAndCondition>() {
        override fun areItemsTheSame(
            oldItem: TermsAndCondition,
            newItem: TermsAndCondition
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldOrder: TermsAndCondition,
            newOrder: TermsAndCondition
        ): Boolean {
            return oldOrder == newOrder
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TermsAndConditionsViewHolder {
        val binding =
            ItemTermsConditionBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return TermsAndConditionsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TermsAndConditionsViewHolder, position: Int) {
        val item = currentList[position]
        item?.let {
            holder.bind(it)
        }
    }

    inner class TermsAndConditionsViewHolder(
        private val itemBinding: ItemTermsConditionBinding
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(item: TermsAndCondition) {
            itemBinding.tvTitle.text = item.titleName
            itemBinding.tvDetails.text = item.details
        }
    }
}