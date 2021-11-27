package com.gojol.notto.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.viewModelScope
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentCalendarBinding
import com.gojol.notto.ui.home.HomeFragment.Companion.TODAY_BUTTON_CLICK_KEY
import com.gojol.notto.ui.home.HomeFragment.Companion.TODO_SWIPE_KEY
import com.gojol.notto.ui.home.adapter.CalendarDayAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.isActive
import java.time.LocalDate

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val calendarDayAdapter = CalendarDayAdapter(::dayClickCallback)

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

        binding.progressCircular.isVisible = true
        initRecyclerView()
        initObserver()
        setMonthlyData()
    }

    override fun onResume() {
        super.onResume()

        setMonthlyData()

        setFragmentResultListener(TODO_SWIPE_KEY) { _, _ ->
            swipeUpdate()
        }
        setFragmentResultListener(TODAY_BUTTON_CLICK_KEY) { _, _ ->
            setMonthlyData()
        }
    }

    private fun initRecyclerView() {
        binding.rvCalendar.apply {
            adapter = calendarDayAdapter
            itemAnimator = null
        }
    }

    private fun initObserver() {
        calendarViewModel.monthlyAchievement.observe(viewLifecycleOwner, { itemList ->
            calendarDayAdapter.submitList(itemList)
            binding.progressCircular.isVisible = false
            sendFragmentResultForHeightUpdate()
        })
        calendarViewModel.monthlyCalendar.observe(viewLifecycleOwner, {
            sendFragmentResultWithClickDate()
        })
    }

    private fun sendFragmentResultForHeightUpdate() {
        setFragmentResult(UPDATE_HEIGHT_KEY, bundleOf())
    }

    private fun sendFragmentResultWithClickDate() {
        calendarViewModel.monthlyCalendar.value?.apply {
            setFragmentResult(
                DATE_CLICK_KEY,
                bundleOf(
                    DATE_CLICK_BUNDLE_KEY to LocalDate.of(
                        this.year,
                        this.month,
                        this.selectedDay
                    )
                )
            )
        }
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

    fun getItemId() = arguments?.getLong(ITEM_ID_ARGUMENT)

    companion object {
        const val UPDATE_HEIGHT_KEY = "update_height"
        const val DATE_CLICK_KEY = "date_click"
        const val DATE_CLICK_BUNDLE_KEY = "selected_date"

        const val ITEM_ID_ARGUMENT = "item id"
        fun newInstance(itemId: Long): CalendarFragment {
            return CalendarFragment().apply {
                arguments = bundleOf(ITEM_ID_ARGUMENT to itemId)
            }
        }
    }
}
