package com.gojol.notto.ui.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gojol.notto.ui.home.CalendarFragment
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.abs

class CalendarViewPagerAdapter(
    private val fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {

    val firstFragmentPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val itemId = getItemId(position)

        fragmentManager.fragments.forEach {
            if (it is CalendarFragment && it.getItemId() == itemId) {
                fragmentManager.beginTransaction().remove(it).commitNow()
                return CalendarFragment.newInstance(itemId)
            }
        }
        return CalendarFragment.newInstance(itemId)
    }

    override fun getItemId(position: Int): Long {
        val moveMonth: Long = (position - firstFragmentPosition).toLong()

        val today = if (moveMonth > 0) {
            LocalDate.now().plusMonths(moveMonth)
        } else {
            LocalDate.now().minusMonths(abs(moveMonth))
        }

        return today.format(DateTimeFormatter.ofPattern("yyyyMM")).toLong()
    }

    override fun containsItem(itemId: Long): Boolean {
        return itemId in MIN_DATE..MAX_DATE
    }

    companion object {
        const val MIN_DATE = 20000102L
        const val MAX_DATE = 20991230L
    }
}
