package com.gojol.notto.ui.home.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.gojol.notto.common.AdapterViewType
import com.gojol.notto.databinding.ItemCalendarBinding
import java.util.Calendar

class CalendarAdapter(private val requireActivity: FragmentActivity) :
    RecyclerView.Adapter<CalendarAdapter.CustomCalendarViewHolder>() {

    private var date: Calendar = Calendar.getInstance()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCalendarViewHolder {
        return CustomCalendarViewHolder(
            ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: CustomCalendarViewHolder, position: Int) {
        holder.bind(requireActivity, date)
    }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return AdapterViewType.CALENDAR.viewType
    }

    fun setDate(newDate: Calendar) {
        date = newDate
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshAdapter() {
//        TODO 애니메이션 문제 해결 필요
//        notifyItemRemoved(0)
//        notifyItemInserted(0)
        notifyDataSetChanged()
    }

    class CustomCalendarViewHolder(private val binding: ItemCalendarBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(requireActivity: FragmentActivity, date: Calendar) {
            binding.date = date
            val calendarViewPagerAdapter = CalendarViewPagerAdapter(requireActivity)
            binding.vpCalendar.adapter = calendarViewPagerAdapter
            binding.vpCalendar.setCurrentItem(calendarViewPagerAdapter.firstFragmentPosition, false)
            setViewPagerDynamicHeight()
            binding.executePendingBindings()
        }

        private fun setViewPagerDynamicHeight() {
            binding.vpCalendar.registerOnPageChangeCallback(object :
                ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    val viewPager = binding.vpCalendar
                    val view =
                        (viewPager[0] as RecyclerView).layoutManager?.findViewByPosition(position)

                    view?.post {
                        val widthMeasureSpec =
                            MeasureSpec.makeMeasureSpec(view.width, MeasureSpec.EXACTLY)
                        val heightMeasureSpec =
                            MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                        view.measure(widthMeasureSpec, heightMeasureSpec)

                        if (viewPager.layoutParams.height != view.measuredHeight) {
                            viewPager.layoutParams = (viewPager.layoutParams).also {
                                it.height = view.measuredHeight
                            }
                        }
                    }
                }
            })
        }
    }
}
