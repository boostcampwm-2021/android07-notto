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

const val TIME = "time"

class CalendarFragment : Fragment() {

    private lateinit var binding: FragmentCalendarBinding
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
        time?.let {
            val year = (it / 100).toInt()
            val month = (it % 100).toInt()

            val calendar = Calendar.getInstance().apply { set(year, month, 1) }
            val dateList = (calendar.getFirstDayOfMonth()..calendar.getLastDayOfMonth()).toList()

            val dayOfWeek = calendar.getDayOfWeek() - 1
            val prefixDateList = (0 until dayOfWeek).map { 0 }

            return prefixDateList + dateList
        } ?: kotlin.run {
            return emptyList()
        }
    }
}
