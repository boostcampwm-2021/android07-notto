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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class CalendarAdapter(
    private val fragmentManager: FragmentManager,
    private val lifecycle: Lifecycle
) : RecyclerView.Adapter<CalendarAdapter.CustomCalendarViewHolder>() {

    private var date = LocalDate.now()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomCalendarViewHolder {
        return CustomCalendarViewHolder(
            ItemCalendarBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            fragmentManager,
            lifecycle
        )
    }

    override fun onBindViewHolder(holder: CustomCalendarViewHolder, position: Int) {
        holder.bind(date)
    }

    override fun getItemCount(): Int = 1

    override fun getItemViewType(position: Int): Int {
        return AdapterViewType.CALENDAR.viewType
    }

    fun setDate(newDate: LocalDate) {
        date = newDate
    }

    class CustomCalendarViewHolder(
        private val binding: ItemCalendarBinding,
        private val fragmentManager: FragmentManager,
        private val lifecycle: Lifecycle
    ) : RecyclerView.ViewHolder(binding.root) {

        private val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월")
        private val calendarViewPagerAdapter = CalendarViewPagerAdapter(fragmentManager, lifecycle)

        init {
            binding.vpCalendar.adapter = calendarViewPagerAdapter
            binding.vpCalendar.setCurrentItem(
                calendarViewPagerAdapter.firstFragmentPosition,
                false
            )
            setViewPagerDynamicHeight()
        }

        fun bind(date: LocalDate) {
            binding.date = date.format(formatter)
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
