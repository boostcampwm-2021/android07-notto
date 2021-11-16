package com.gojol.notto.ui.home.adapter

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
import com.gojol.notto.ui.home.CalendarFragment
import com.gojol.notto.util.getMonth
import com.gojol.notto.util.getYear
import com.gojol.notto.util.toYearMonthDate
import java.util.Calendar

class CalendarAdapter(private val requireActivity: FragmentActivity) :
    RecyclerView.Adapter<CalendarAdapter.CustomCalendarViewHolder>() {

    private val today = Calendar.getInstance()
    private var date = today

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCalendarViewHolder {
        return CustomCalendarViewHolder(
            ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            requireActivity
        )
    }

    override fun onBindViewHolder(holder: CustomCalendarViewHolder, position: Int) {
        holder.bind(today, date)
    }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return AdapterViewType.CALENDAR.viewType
    }

    fun setDate(newDate: Calendar) {
        date = newDate
        notifyItemChanged(0)
    }

    fun refreshAdapter() {
        notifyItemChanged(0)
    }

    class CustomCalendarViewHolder(
        private val binding: ItemCalendarBinding,
        private val requireActivity: FragmentActivity
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(today: Calendar, date: Calendar) {
            binding.date = date

            val movePosition =
                date.getMonth() - today.getMonth() + (date.getYear() - today.getYear()) * 12

            val calendarViewPagerAdapter = CalendarViewPagerAdapter(requireActivity)
            binding.vpCalendar.adapter = calendarViewPagerAdapter
            binding.vpCalendar.setCurrentItem(
                calendarViewPagerAdapter.firstFragmentPosition + movePosition,
                false
            )
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
