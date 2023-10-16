package com.cancer.yaqeen.presentation.ui.onboarding.intro.user_type.doctor.specialization

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.cancer.yaqeen.databinding.ItemDropdownSpinnerBinding
import java.util.*


class UniversityAutoCompleteAdapter(
    context: Context,
    private var items: List<String> = listOf()
) :
    ArrayAdapter<String>(context, 0, items), Filterable {
    override fun getCount(): Int {
        return items.size
    }
    override fun getItem(i: Int): String {
        return items[i]
    }
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder = AutoCompleteViewHolder(
            ItemDropdownSpinnerBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

        holder.bind(items[position])
        return holder.binding.root
    }

    fun setList(items: List<String>){
        this.items = items
        notifyDataSetChanged()
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                if (filterResults.values != null)
                    items = filterResults.values as List<String>
                notifyDataSetChanged()
            }
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val queryString = charSequence?.toString()?.toLowerCase(Locale.ROOT)
                val filterResults = FilterResults()
                filterResults.values = if (queryString == null || queryString.isEmpty())
                    items
                else
                    items.filter {
                        it.trim().toLowerCase(Locale.ROOT).startsWith(queryString) ||
                                it.trim().toLowerCase(Locale.ROOT)
                                    .startsWith(queryString)
                    }

                return filterResults
            }
        }
    }
    inner class AutoCompleteViewHolder(
        val binding: ItemDropdownSpinnerBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.textItem = item
        }
    }
}