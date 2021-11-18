package com.gojol.notto.ui.home.adapter

import android.view.LayoutInflater
import android.view.View.MeasureSpec
import android.view.ViewGroup
import androidx.core.view.get
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.gojol.notto.common.AdapterViewType
import com.gojol.notto.databinding.ItemCalendarBinding
import com.gojol.notto.util.getMonth
import com.gojol.notto.util.getYear
import java.util.Calendar

class CalendarAdapter(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle
) : RecyclerView.Adapter<CalendarAdapter.CustomCalendarViewHolder>() {

    private val today = Calendar.getInstance()
    private var date = today

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCalendarViewHolder {
        return CustomCalendarViewHolder(
            ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            fragmentManager,
            lifecycle
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

    class CustomCalendarViewHolder(
        private val binding: ItemCalendarBinding,
        private val fragmentManager: FragmentManager,
        private val lifecycle: Lifecycle
    ) : RecyclerView.ViewHolder(binding.root) {

        private val calendarViewPagerAdapter = CalendarViewPagerAdapter(fragmentManager, lifecycle)

        init {
            binding.vpCalendar.adapter = calendarViewPagerAdapter
            setViewPagerDynamicHeight()
        }

        fun bind(today: Calendar, date: Calendar) {
            binding.date = date

            val movePosition =
                date.getMonth() - today.getMonth() + (date.getYear() - today.getYear()) * 12

            binding.vpCalendar.setCurrentItem(
                calendarViewPagerAdapter.firstFragmentPosition + movePosition,
                false
            )

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
