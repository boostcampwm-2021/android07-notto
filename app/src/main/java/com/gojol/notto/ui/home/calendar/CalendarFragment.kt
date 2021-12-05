package com.gojol.notto.ui.home.calendar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentCalendarBinding
import com.gojol.notto.ui.home.HomeFragment
import com.gojol.notto.ui.home.calendar.adapter.CalendarDayAdapter
import com.gojol.notto.ui.home.util.DayClickListener
import com.gojol.notto.ui.home.util.MonthSwipeListener
import com.gojol.notto.ui.home.util.TodayClickListener
import com.gojol.notto.ui.home.util.TodoSwipeListener
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class CalendarFragment : Fragment(), TodayClickListener, TodoSwipeListener {

    private lateinit var viewPool: RecyclerView.RecycledViewPool
    private lateinit var binding: FragmentCalendarBinding
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val calendarDayAdapter = CalendarDayAdapter(::dayClickCallback)
    private val dayClickListener: DayClickListener?
        get() = parentFragment as? DayClickListener
    private val monthSwipeListener: MonthSwipeListener?
        get() = parentFragment as? MonthSwipeListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initObserver()
    }

    override fun onResume() {
        super.onResume()

        (parentFragment as? HomeFragment)?.setCalendarListener(this)
        binding.progressCircular.isVisible = true

        setMonthlyData()
    }

    override fun onSwipe() {
        swipeUpdate()
    }

    override fun onClick() {
        setMonthlyData()
    }

    private fun initRecyclerView() {
        binding.rvCalendar.apply {
            adapter = calendarDayAdapter
            itemAnimator = null
            if (::viewPool.isInitialized) {
                setRecycledViewPool(viewPool)
                (layoutManager as GridLayoutManager).recycleChildrenOnDetach = true
            }
        }
    }

    private fun initObserver() {
        calendarViewModel.monthlyAchievement.observe(viewLifecycleOwner, { itemList ->
            calendarDayAdapter.submitList(itemList)
            binding.progressCircular.isVisible = false
            monthSwipeListener?.onSwipe()
        })
        calendarViewModel.monthlyCalendar.observe(viewLifecycleOwner, {
            dayClickListener?.onClick(LocalDate.of(it.year, it.month, it.selectedDay))
        })
    }

    private fun setMonthlyData() {
        calendarViewModel.initData()
        calendarViewModel.setMonthlyDailyTodos()
    }

    private fun swipeUpdate() {
        calendarViewModel.setMonthlyDailyTodos()
    }

    private fun dayClickCallback(date: Int) {
        calendarViewModel.updateSelectedDay(date)
    }

    companion object {
        const val ITEM_ID_ARGUMENT = "item id"
        fun newInstance(
            itemId: Long,
            calendarPool: RecyclerView.RecycledViewPool
        ): CalendarFragment {
            return CalendarFragment().apply {
                arguments = bundleOf(ITEM_ID_ARGUMENT to itemId)
                viewPool = calendarPool
            }
        }
    }
}
