package com.gojol.notto.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentCalendarBinding
import com.gojol.notto.ui.home.HomeFragment.Companion.TODO_SWIPE_KEY
import com.gojol.notto.ui.home.adapter.CalendarDayAdapter
import com.gojol.notto.ui.home.util.GridSpacingDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
    private val calendarViewModel: CalendarViewModel by viewModels()
    private val calendarDayAdapter = CalendarDayAdapter(::dayClickCallback)
    private var time: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setFragmentResultListener(TODO_SWIPE_KEY) { _, _ ->
            initViewModelData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calendar, container, false)

        time = arguments?.get(ITEM_ID_ARGUMENT) as Long?

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
        time?.let { time ->
            val year = (time / 100).toInt()
            val month = (time % 100).toInt()

            callback?.let { it(year, month, date) }
            selectedYear = year
            selectedMonth = month
            selectedDate = date

            calendarViewModel.setMonthlyAchievement()

            setFragmentResult(DATE_CLICK_KEY, bundleOf())
        }
    }

    companion object {
        var selectedYear: Int? = null
        var selectedMonth: Int? = null
        var selectedDate: Int? = null
        var callback: ((Int, Int, Int) -> (Unit))? = null

        const val DATE_CLICK_KEY = "date_click"

        private const val ITEM_ID_ARGUMENT = "item id"
        fun newInstance(itemId: Long): CalendarFragment {
            return CalendarFragment().apply {
                arguments = bundleOf(ITEM_ID_ARGUMENT to itemId)
            }
        }
    }
}
