package com.gojol.notto.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.common.AdapterViewType
import com.gojol.notto.databinding.ItemCalendarBinding

class CalendarAdapter(private val requireActivity: FragmentActivity) :
    RecyclerView.Adapter<CalendarAdapter.CustomCalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCalendarViewHolder {
        return CustomCalendarViewHolder(
            ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CustomCalendarViewHolder, position: Int) {
        holder.bind(requireActivity)
    }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return AdapterViewType.CALENDAR.viewType
    }

    class CustomCalendarViewHolder(private val binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(requireActivity: FragmentActivity) {
            val calendarViewPagerAdapter = CalendarViewPagerAdapter(requireActivity)
            binding.vpCalendar.adapter = calendarViewPagerAdapter
            binding.vpCalendar.setCurrentItem(calendarViewPagerAdapter.firstFragmentPosition, false)
            binding.tvCalendarTitle.text = "2021년 11월" // sample text
            binding.executePendingBindings()
        }
    }
}
