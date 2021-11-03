package com.gojol.notto.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.databinding.ItemMonthlyCalendarBinding

class CalendarAdapter(private var date: String) :
    RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(
            ItemMonthlyCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bind(date)
    }

    override fun getItemCount(): Int = 1

    fun setDate(date: String) {
        this.date = date
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE
    }

    companion object {
        const val VIEW_TYPE = 1
    }

    class CalendarViewHolder(private val binding: ItemMonthlyCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.date = item
            binding.executePendingBindings()
        }
    }
}
