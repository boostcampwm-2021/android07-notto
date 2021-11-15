package com.gojol.notto.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.databinding.ItemCalendarDayBinding

class CalendarDayAdapter :
    ListAdapter<Pair<Int, Int>, CalendarDayAdapter.CalendarDayViewHolder>(CalendarDayDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarDayViewHolder {
        return CalendarDayViewHolder(
            ItemCalendarDayBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CalendarDayViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class CalendarDayViewHolder(private val binding: ItemCalendarDayBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Pair<Int, Int>) {
            val date = item.first
            val achievement = item.second
            if (date != 0) {
                binding.tvCalendarDay.text = date.toString()
                binding.tvCalendarDay.backgroundTintList =
                    ContextCompat.getColorStateList(binding.root.context, R.color.yellow_deep)
                        ?.withAlpha(51 * achievement)
            }
        }
    }

    class CalendarDayDiff : DiffUtil.ItemCallback<Pair<Int, Int>>() {
        override fun areItemsTheSame(oldItem: Pair<Int, Int>, newItem: Pair<Int, Int>): Boolean {
            return oldItem.first == newItem.first
        }

        override fun areContentsTheSame(oldItem: Pair<Int, Int>, newItem: Pair<Int, Int>): Boolean {
            return oldItem == newItem
        }
    }
}
