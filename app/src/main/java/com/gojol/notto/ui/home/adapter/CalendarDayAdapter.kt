package com.gojol.notto.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.databinding.ItemCalendarDayBinding
import com.gojol.notto.model.data.DateWithCountAndSelect

class CalendarDayAdapter(private val dayClickCallback: (Int) -> (Unit)) :
    ListAdapter<DateWithCountAndSelect, CalendarDayAdapter.CalendarDayViewHolder>(CalendarDayDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        return CalendarDayViewHolder(
            ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            dayClickCallback
        )
    }

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalendarDayViewHolder(
        private val binding: ItemCalendarDayBinding,
        private val dayClickCallback: (Int) -> (Unit)
    ) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tvCalendarDay.setOnClickListener {
                dayClickCallback(binding.tvCalendarDay.text.toString().toInt())
            }
        }

        fun bind(item: DateWithCountAndSelect) {
            val date = item.date
            val achievement = item.count

            if (date != 0) {
                binding.tvCalendarDay.text = date.toString()
                binding.tvCalendarDay.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.yellow_deep)
                        ?.withAlpha(51 * achievement)

                if (item.isSelected) {
                    binding.underline.visibility = View.VISIBLE
                } else {
                    binding.underline.visibility = View.INVISIBLE
                }
            }
        }
    }

    class CalendarDayDiff : DiffUtil.ItemCallback<DateWithCountAndSelect>() {
        override fun areItemsTheSame(
            oldItem: DateWithCountAndSelect,
            newItem: DateWithCountAndSelect
        ): Boolean {
            return oldItem.date == newItem.date
        }

        override fun areContentsTheSame(
            oldItem: DateWithCountAndSelect,
            newItem: DateWithCountAndSelect
        ): Boolean {
            return oldItem == newItem
        }
    }
}
