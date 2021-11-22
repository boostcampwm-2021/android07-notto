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
import com.gojol.notto.model.data.DayWithSuccessLevelAndSelect

class CalendarDayAdapter(private val dayClickCallback: (Int) -> (Unit)) :
    ListAdapter<DayWithSuccessLevelAndSelect, CalendarDayAdapter.CalendarDayViewHolder>(CalendarDayDiff()) {

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
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.tvCalendarDay.setOnClickListener {
                dayClickCallback(binding.tvCalendarDay.text.toString().toInt())
            }
        }

        fun bind(item: DayWithSuccessLevelAndSelect) {
            val date = item.day
            val achievement = item.successLevel.toIntAlpha()

            if (date != 0) {
                binding.tvCalendarDay.text = date.toString()
                binding.tvCalendarDay.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.yellow_deep)
                        ?.withAlpha(achievement)

                if (item.isSelected) {
                    binding.underline.visibility = View.VISIBLE
                } else {
                    binding.underline.visibility = View.INVISIBLE
                }
            }

            binding.executePendingBindings()
        }
    }

    class CalendarDayDiff : DiffUtil.ItemCallback<DayWithSuccessLevelAndSelect>() {
        override fun areItemsTheSame(
            oldItem: DayWithSuccessLevelAndSelect,
            newItem: DayWithSuccessLevelAndSelect
        ): Boolean {
            return oldItem.day == newItem.day
        }

        override fun areContentsTheSame(
            oldItem: DayWithSuccessLevelAndSelect,
            newItem: DayWithSuccessLevelAndSelect
        ): Boolean {
            return oldItem == newItem
        }
    }
}
