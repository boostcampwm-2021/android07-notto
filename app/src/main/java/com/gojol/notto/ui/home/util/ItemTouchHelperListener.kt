package com.gojol.notto.ui.home.util

import com.gojol.notto.common.TodoSuccessType

interface ItemTouchHelperListener {
    fun onItemMove(from: Int, to: Int): Boolean
    fun onItemSwipe(position: Int, successType: TodoSuccessType)
}
