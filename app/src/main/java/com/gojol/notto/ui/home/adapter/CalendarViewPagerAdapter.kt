package com.gojol.notto.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gojol.notto.ui.home.CalendarFragment
import java.util.Calendar
import com.gojol.notto.util.getMonth
import com.gojol.notto.util.getYear

class CalendarViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    val firstFragmentPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val itemId = getItemId(position)

        return CalendarFragment.newInstance(itemId)
    }

    override fun getItemId(position: Int): Long {
        val today = Calendar.getInstance()
        val moveMonth = position - firstFragmentPosition

        today.add(Calendar.MONTH, moveMonth)

        // yyyyMM Format
        return (today.getYear() * 100 + today.getMonth()).toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemId in 20000102L..20991230L
    }
}
