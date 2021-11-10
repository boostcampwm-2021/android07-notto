package com.gojol.notto.ui.home.util

import android.graphics.BlendMode
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.Rect
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.common.TodoSuccessType
import com.gojol.notto.ui.home.adapter.TodoAdapter
import android.view.View
import android.os.Build

class TodoItemTouchCallback(private val listener: ItemTouchHelperListener) :
    ItemTouchHelper.Callback() {

    private lateinit var successType: TodoSuccessType
    private lateinit var currentSuccessType: TodoSuccessType

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
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
        successType = currentSuccessType
        listener.onItemSwipe(viewHolder.bindingAdapterPosition, successType)
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
        successType = (viewHolder as TodoAdapter.TodoViewHolder).successType

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            var text = TODO_CANCEL
            var backgroundColor = R.color.gray_light
            var swipeDirection = TODO_CANCEL
            when {
                dX < 0 -> {
                    swipeDirection = TODO_FAIL

                    if (successType == TodoSuccessType.FAIL) {
                        currentSuccessType = TodoSuccessType.NOTHING
                    } else {
                        currentSuccessType = TodoSuccessType.FAIL
                        text = TODO_FAIL
                        backgroundColor = R.color.blue_normal
                    }
                }
                dX > 0 -> {
                    swipeDirection = TODO_SUCCESS

                    if (successType == TodoSuccessType.SUCCESS) {
                        currentSuccessType = TodoSuccessType.NOTHING
                    } else {
                        currentSuccessType = TodoSuccessType.SUCCESS
                        text = TODO_SUCCESS
                        backgroundColor = R.color.yellow_normal
                    }
                }
            }

            drawItemBackground(itemView, backgroundColor, c)
            setDrawTextPaint(itemView, text, c, swipeDirection)
        }

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }

    private fun drawItemBackground(itemView: View, color: Int, canvas: Canvas) {
        val drawable = ContextCompat.getDrawable(itemView.context, R.drawable.bg_todo_normal)
        drawable?.apply {
            setTint(ContextCompat.getColor(itemView.context, color))
            setBounds(itemView.left, itemView.top, itemView.right, itemView.bottom)
            draw(canvas)
        }
    }

    private fun setDrawTextPaint(
        itemView: View,
        text: String,
        canvas: Canvas,
        swipeDirection: String
    ) {
        val bounds = Rect()
        val paint = Paint().apply {
            color = when (text) {
                TODO_CANCEL -> ContextCompat.getColor(itemView.context, R.color.black)
                else -> ContextCompat.getColor(itemView.context, R.color.white)
            }

            textSize = itemView.resources.getDimensionPixelSize(R.dimen.text_x_small).toFloat()
            textAlign = Paint.Align.CENTER
            getTextBounds(text, 0, text.length, bounds)
        }

        val height = bounds.height()
        val width = bounds.width()

        val x = when (swipeDirection) {
            TODO_FAIL -> itemView.right.toFloat() - width
            else -> itemView.left.toFloat() + width
        }
        val y = ((itemView.top + itemView.bottom) / 2 + (height / 2)).toFloat()

        canvas.drawText(text, x, y, paint)
    }

    companion object {
        const val TODO_SUCCESS = "성공"
        const val TODO_FAIL = "실패"
        const val TODO_CANCEL = "취소"
    }
}

