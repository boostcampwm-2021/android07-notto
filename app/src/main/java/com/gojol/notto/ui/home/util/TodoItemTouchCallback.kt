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
import com.gojol.notto.common.TodoState
import com.gojol.notto.ui.home.adapter.TodoAdapter
import android.view.View
import android.os.Build

class TodoItemTouchCallback(private val listener: ItemTouchHelperListener) :
    ItemTouchHelper.Callback() {

    private lateinit var state: TodoState
    private lateinit var currentState: TodoState

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
        state = currentState
        listener.onItemSwipe(viewHolder.bindingAdapterPosition, state)
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
        state = (viewHolder as TodoAdapter.TodoViewHolder).state

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            val itemView = viewHolder.itemView
            var text = TODO_CANCEL
            var backgroundColor = R.color.gray_light
            var swipeDirection = TODO_CANCEL
            when {
                dX < 0 -> {
                    swipeDirection = TODO_FAIL

                    if (state == TodoState.FAIL) {
                        currentState = TodoState.NOTHING
                    } else {
                        currentState = TodoState.FAIL
                        text = TODO_FAIL
                        backgroundColor = R.color.blue_normal
                    }
                }
                dX > 0 -> {
                    swipeDirection = TODO_SUCCESS

                    if (state == TodoState.SUCCESS) {
                        currentState = TodoState.NOTHING
                    } else {
                        currentState = TodoState.SUCCESS
                        text = TODO_SUCCESS
                        backgroundColor = R.color.yellow_normal
                    }
                }
            }

            drawItemBackground(itemView, backgroundColor, c)
            setDrawTextPaint(itemView, text, c, swipeDirection)

            // TODO: 이 방법은 성능 이슈가 있는 듯 하다. 너무 많은 drawRect의 호출로 인한 성능 저하인지,
            //  setLayerType으로 인한 성능 저하인지 확인할 것
            if (kotlin.math.abs(itemView.translationX).toInt() - itemView.width >= 0) {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                    c.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR)
                } else {
                    c.drawColor(Color.TRANSPARENT, BlendMode.CLEAR)
                }
            }
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
