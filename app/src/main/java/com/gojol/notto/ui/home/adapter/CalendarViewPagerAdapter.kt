package com.gojol.notto.ui.home.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.gojol.notto.ui.home.CalendarFragment
import com.gojol.notto.ui.home.TIME
import java.util.Calendar
import com.gojol.notto.util.getMonth
import com.gojol.notto.util.getYear

class CalendarViewPagerAdapter(
    fragmentActivity: FragmentActivity
) : FragmentStateAdapter(fragmentActivity) {

    val firstFragmentPosition = Int.MAX_VALUE / 2

    override fun getItemCount(): Int = Int.MAX_VALUE

    override fun createFragment(position: Int): Fragment {
        val itemId = getItemId(position)
        val fragment = CalendarFragment()

        val bundle = Bundle()
        bundle.putLong(TIME, itemId)
        fragment.arguments = bundle

        return fragment
    }

    override fun getItemId(position: Int): Long {
        val today = Calendar.getInstance()
        val moveMonth = position - firstFragmentPosition

        today.add(Calendar.MONTH, moveMonth)

        // yyyyMM Format
        return (today.getYear() * 100 + today.getMonth()).toLong()
    }
}
