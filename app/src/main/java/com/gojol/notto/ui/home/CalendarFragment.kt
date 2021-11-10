package com.gojol.notto.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentCalendarBinding
import com.gojol.notto.ui.home.adapter.CalendarDayAdapter
import com.gojol.notto.ui.home.util.GridSpacingDecoration
import java.util.Calendar
import com.gojol.notto.util.getDayOfWeek
import com.gojol.notto.util.getFirstDayOfMonth
import com.gojol.notto.util.getLastDayOfMonth

class CalendarFragment(private val year: Int, private val month: Int) : Fragment() {

    private lateinit var binding: FragmentCalendarBinding

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
    }

    private fun initRecyclerView() {
        val dateList = setCalendarDateList()
        binding.rvCalendar.apply {
            adapter = CalendarDayAdapter().apply { submitList(dateList) }
            context?.resources?.displayMetrics?.widthPixels?.let {
                addItemDecoration(GridSpacingDecoration(it, 7))
            }
        }
    }

    private fun setCalendarDateList(): List<Int> {
        val calendar = Calendar.getInstance().apply { set(year, month, 1) }
        val dateList = (calendar.getFirstDayOfMonth()..calendar.getLastDayOfMonth()).toList()

        val dayOfWeek = calendar.getDayOfWeek() - 1
        val prefixDateList = (0 until dayOfWeek).map { 0 }

        return prefixDateList + dateList
    }
}
