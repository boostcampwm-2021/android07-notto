package com.gojol.notto.ui.home.util

import android.graphics.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.common.TodoSuccessType
import com.gojol.notto.ui.home.adapter.TodoAdapter
import android.view.View


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

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onItemSwipe(viewHolder.bindingAdapterPosition, successType)
        successType = TodoSuccessType.NOTHING
    }

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

                val text = TODO_FAIL
                drawItemBackground(itemView, R.color.blue_normal, c)
                setDrawTextPaint(itemView, text, c)
            }
            // 성공 배경 그리기
            else if (dX > 0) {
                successType = TodoSuccessType.SUCCESS

                val text = TODO_SUCCESS
                drawItemBackground(itemView, R.color.yellow_normal, c)
                setDrawTextPaint(itemView, text, c)
            }

            if (kotlin.math.abs(itemView.translationX).toInt() - itemView.width >= 0) {
                drawItemBackground(itemView, R.color.white, c)
            }

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    private fun drawItemBackground(itemView: View, color: Int, canvas: Canvas) {
        val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.bg_todo_normal)
        drawable?.setTint(ContextCompat.getColor(itemView.context, color))
        drawable?.setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
        drawable?.draw(canvas)
    }

    private fun setDrawTextPaint(itemView: View, text: String, canvas: Canvas) {
        paint.color = ContextCompat.getColor(itemView.context, R.color.white)
        paint.textSize = itemView.resources.getDimensionPixelSize(R.dimen.text_median).toFloat()
        paint.textAlign = Paint.Align.CENTER

        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        val height = bounds.height()
        val width = bounds.width()

        val x: Float = if(text == TODO_FAIL) {
            itemView.right.toFloat() - width
        } else {
            itemView.left.toFloat() + width
        }

        canvas.drawText(
            text,
            x,
            ((itemView.top + itemView.bottom) / 2 + (height / 2)).toFloat(),
            paint
        )
    }

    companion object {
        const val TODO_SUCCESS = "성공"
        const val TODO_FAIL = "실패"
    }
}
