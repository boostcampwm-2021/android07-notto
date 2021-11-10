package com.gojol.notto.ui.home.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.common.AdapterViewType
import com.gojol.notto.R
import com.gojol.notto.common.TodoState
import com.gojol.notto.databinding.ItemTodoBinding
import com.gojol.notto.model.data.TodoWithTodayDateState
import com.gojol.notto.model.database.todo.DateState
import com.gojol.notto.ui.home.util.ItemTouchHelperListener

class TodoAdapter(
    private val swipeCallback: (DateState) -> (Unit)
) : ListAdapter<TodoWithTodayDateState, TodoAdapter.TodoViewHolder>(TodoDiff()), ItemTouchHelperListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return AdapterViewType.TODO.viewType
    }

    override fun onItemMove(from: Int, to: Int): Boolean {
        return false
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onItemSwipe(position: Int, state: TodoState) {
        if (position < 0) return

        val todoDateState = currentList[position].todayDateState.copy(todoState = state)
        swipeCallback(todoDateState)

        notifyDataSetChanged()
    }

    class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var state: TodoState

        fun bind(item: TodoWithTodayDateState) {
            binding.item = item
            state = item.todayDateState.todoState

            val color = when (state) {
                TodoState.NOTHING -> R.color.black
                else -> R.color.white
            }

            binding.tvHomeTodo.setTextColor(ContextCompat.getColor(binding.root.context, color))
            binding.executePendingBindings()
        }
    }

    class TodoDiff : DiffUtil.ItemCallback<TodoWithTodayDateState>() {
        override fun areItemsTheSame(oldItem: TodoWithTodayDateState, newItem: TodoWithTodayDateState): Boolean {
            return oldItem.todo.todoId == newItem.todo.todoId
        }

        override fun areContentsTheSame(oldItem: TodoWithTodayDateState, newItem: TodoWithTodayDateState): Boolean {
            return oldItem == newItem
        }
    }
}
