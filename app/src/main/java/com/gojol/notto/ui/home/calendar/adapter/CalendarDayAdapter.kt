package com.gojol.notto.ui.home.calendar.adapter

import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.common.EMPTY_DATE
import com.gojol.notto.databinding.ItemCalendarDayBinding
import com.gojol.notto.model.data.DayWithSuccessLevelAndSelect

class CalendarDayAdapter(private val dayClickCallback: (Int) -> (Unit)) :
    ListAdapter<DayWithSuccessLevelAndSelect, CalendarDayAdapter.CalendarDayViewHolder>(
        CalendarDayDiff()
    ) {

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

        fun bind(item: DayWithSuccessLevelAndSelect) {
            binding.todoCount = when {
                item.todoCount in 1..9 -> item.todoCount.toString()
                item.todoCount > 9 -> "9+"
                else -> null
            }
            val date = item.day
            val achievement = item.successLevel.toIntAlpha()

            // 캘린더의 요일 위치를 맞추기 위해 넣은 EMPTY_DATE(0)을 제외한 위치부터 날짜뷰를 출력
            if (date != EMPTY_DATE) {
                setDayText(date, item.isSelected)
                setSuccessLevel(achievement)

                binding.root.setOnClickListener {
                    dayClickCallback(binding.tvCalendarDay.text.toString().toInt())
                }
            } else {
                binding.ivSuccessLevelBackground.visibility = View.INVISIBLE
                binding.ivSuccessLevel.visibility = View.INVISIBLE
                binding.tvCalendarDay.visibility = View.INVISIBLE
            }

            binding.executePendingBindings()
        }

        private fun setDayText(date: Int, isSelected: Boolean) {
            val color: Int
            val style: Typeface
            if (isSelected) {
                color = R.color.calendar_day_selected
                style = Typeface.DEFAULT_BOLD
            } else {
                color = R.color.calendar_day_default
                style = Typeface.DEFAULT
            }

            binding.tvCalendarDay.apply {
                text = date.toString()
                typeface = style
                setTextColor(ContextCompat.getColor(context, color))
            }
        }

        private fun setSuccessLevel(achievement: Int) {
            binding.ivSuccessLevel.backgroundTintList = if (achievement == 0) {
                ContextCompat.getColorStateList(
                    binding.root.context,
                    R.color.calendar_day_background_default
                )
            } else {
                ContextCompat.getColorStateList(binding.root.context, R.color.yellow_deep)
                    ?.withAlpha(achievement)
            }
            binding.ivSuccessLevel.visibility = View.VISIBLE
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
