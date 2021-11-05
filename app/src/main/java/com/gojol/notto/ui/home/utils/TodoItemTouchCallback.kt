package com.gojol.notto.ui.home.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.common.TodoSuccessType
import com.gojol.notto.ui.home.adapter.TodoAdapter

class TodoItemTouchCallback(private val listener: ItemTouchHelperListener) : ItemTouchHelper.Callback() {

    private val paint = Paint()
    private var successType = TodoSuccessType.NOTHING

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return if (viewHolder is TodoAdapter.TodoViewHolder) {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN
            val swipeFlags = ItemTouchHelper.START or ItemTouchHelper.END
            makeMovementFlags(dragFlags, swipeFlags)
        } else 0
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) { }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            // 실패 배경 그리기
            if (dX < 0) {
                successType = TodoSuccessType.FAIL

                val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.bg_todo_normal)
                drawable?.setTint(ContextCompat.getColor(itemView.context, R.color.blue_normal))
                drawable?.setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
                drawable?.draw(c)

                paint.color = ContextCompat.getColor(itemView.context, R.color.white)
                paint.textSize = 70F
                paint.textAlign = Paint.Align.CENTER

                val text = TODO_FAIL
                val bounds = Rect()
                paint.getTextBounds(text, 0, text.length, bounds)
                val height = bounds.height()
                val width = bounds.width()

                c.drawText(
                    text,
                    itemView.right.toFloat() - width,
                    ((itemView.top + itemView.bottom) / 2 + (height / 2)).toFloat(),
                    paint
                )
            }
            // 성공 배경 그리기
            else if (dX > 0) {
                successType = TodoSuccessType.SUCCESS

                val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.bg_todo_normal)
                drawable?.setTint(ContextCompat.getColor(itemView.context, R.color.yellow_normal))
                drawable?.setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
                drawable?.draw(c)

                paint.color = ContextCompat.getColor(itemView.context, R.color.white)
                paint.textSize = 70F
                paint.textAlign = Paint.Align.CENTER

                val text = TODO_SUCCESS
                val bounds = Rect()
                paint.getTextBounds(text, 0, text.length, bounds)
                val height = bounds.height()
                val width = bounds.width()

                c.drawText(
                    text,
                    itemView.left.toFloat() + width,
                    ((itemView.top + itemView.bottom) / 2 + (height / 2)).toFloat(),
                    paint
                )
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            if (!isCurrentlyActive) {
                if (kotlin.math.abs(itemView.translationX).toInt() - itemView.width == 0) {
                    if (successType == TodoSuccessType.SUCCESS) {
                        val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.bg_todo_normal)
                        drawable?.setTint(ContextCompat.getColor(itemView.context, R.color.yellow_normal))
                        itemView.background = drawable
                    } else if (successType == TodoSuccessType.FAIL) {
                        val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.bg_todo_normal)
                        drawable?.setTint(ContextCompat.getColor(itemView.context, R.color.blue_normal))
                        itemView.background = drawable
                    }
                    listener.onItemSwipe(viewHolder.bindingAdapterPosition, successType)
                    successType = TodoSuccessType.NOTHING
                }
            }
        }
    }

//    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
//        return defaultValue * 10
//    }
//
//    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
//        return 2f
//    }

    companion object {
        const val TODO_SUCCESS = "성공"
        const val TODO_FAIL = "실패"
    }
}
