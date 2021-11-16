package com.gojol.notto.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentCalendarBinding
import com.gojol.notto.ui.home.adapter.CalendarDayAdapter
import com.gojol.notto.ui.home.util.GridSpacingDecoration
import dagger.hilt.android.AndroidEntryPoint

const val TIME = "time"

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val calendarDayAdapter = CalendarDayAdapter(::dayClickCallback)
    private var time: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)

        arguments?.let {
            time = it.getLong(TIME)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initObserver()
        initRecyclerView()
        initViewModelData()
    }

    override fun onResume() {
        super.onResume()

        initViewModelData()
    }

    private fun initViewModelData() {
        time?.let {
            val year = (it / 100).toInt()
            val month = (it % 100).toInt()

            calendarViewModel.setMonthDate(year, month)
            calendarViewModel.setMonthlyDailyTodos()
        }
    }

    private fun initObserver() {
        calendarViewModel.monthlyAchievement.observe(viewLifecycleOwner, { itemList ->
            calendarDayAdapter.submitList(itemList)
        })
    }

    private fun initRecyclerView() {
        binding.rvCalendar.apply {
            adapter = calendarDayAdapter
            context?.resources?.displayMetrics?.widthPixels?.let {
                addItemDecoration(GridSpacingDecoration(it, 7))
            }
            itemAnimator = null
        }
    }

    private fun dayClickCallback(date: Int) {
        calendarViewModel.setMonthlyAchievement(date)
    }
}
