package com.gojol.notto.ui.home.util.todo

import com.gojol.notto.common.TodoState

interface ItemTouchHelperListener {
    fun onItemMove(from: Int, to: Int): Boolean
    fun onItemSwipe(position: Int, state: TodoState)
}
