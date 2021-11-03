package com.gojol.notto.ui.home.utils

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.ui.home.TodoAdapter
import android.graphics.RectF
import android.view.View
import androidx.core.content.ContextCompat
import com.gojol.notto.R
import com.gojol.notto.common.TodoSuccessType


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
                // TODO: room 데이터베이스에 successful 정보 update
                //   room 데이터 update와 view 디자인 변화 시점이 동일하게..
                successType = TodoSuccessType.FAIL

                paint.color = ContextCompat.getColor(itemView.context, R.color.blue_normal)
                c.drawRect(drawBackground(itemView), paint)

                paint.color = ContextCompat.getColor(itemView.context, R.color.white)
                paint.textSize = 70F
                paint.textAlign = Paint.Align.CENTER

                val text = "실패"
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

                paint.color = ContextCompat.getColor(itemView.context, R.color.yellow_normal)
                val background = RectF(
                    itemView.left.toFloat(), itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat()
                )
                c.drawRect(background, paint)

                paint.color = ContextCompat.getColor(itemView.context, R.color.white)
                paint.textSize = 70F
                paint.textAlign = Paint.Align.CENTER

                val text = "성공"
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
        }

        // TODO: success -> success or fail -> fail : 취소로 변경
        //  backgroundcolor로 바꾸니까 모양도 같이 바뀜. 수정할 것.
        if(!isCurrentlyActive) {
            if(successType == TodoSuccessType.SUCCESS) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.blue_normal))
            } else if(successType == TodoSuccessType.FAIL) {
                itemView.setBackgroundColor(ContextCompat.getColor(itemView.context, R.color.yellow_normal))
            }
            listener.onItemSwipe(viewHolder.bindingAdapterPosition, successType)
            successType = TodoSuccessType.NOTHING

            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
        return 2f
    }

    private fun drawBackground(itemView: View): RectF {
        return RectF(
            itemView.left.toFloat(),
            itemView.top.toFloat(),
            itemView.right.toFloat(),
            itemView.bottom.toFloat()
        )
    }
}