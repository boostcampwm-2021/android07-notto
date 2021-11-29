package com.gojol.notto.ui.label.util

import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.ui.label.EditLabelAdapter

class ItemTouchCallback(
    private val moveItemCallback: (Int, Int) -> Unit,
    private val onClearView: () -> Unit
) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
    0
) {

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return if (viewHolder is EditLabelAdapter.EditLabelViewHolder) {
            val dragFlags = ItemTouchHelper.UP or ItemTouchHelper.DOWN

            makeMovementFlags(dragFlags, 0)
        } else 0
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        val adapter = recyclerView.adapter as EditLabelAdapter
        val from = viewHolder.bindingAdapterPosition
        val to = target.bindingAdapterPosition

        moveItemCallback(from, to)

        recyclerView.itemAnimator = DefaultItemAnimator()
        adapter.notifyItemMoved(from, to)

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        super.onSelectedChanged(viewHolder, actionState)

        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            viewHolder?.itemView?.alpha = 0.5f
        }
    }

    override fun clearView(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ) {
        super.clearView(recyclerView, viewHolder)

        viewHolder.itemView.alpha = 1.0f
        onClearView()
    }
}
