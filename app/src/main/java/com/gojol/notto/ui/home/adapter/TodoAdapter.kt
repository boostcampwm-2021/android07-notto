package com.gojol.notto.ui.home.adapter

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
import com.gojol.notto.model.data.TodoWithTodayDailyTodo
import com.gojol.notto.model.database.todo.DailyTodo
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.ui.home.util.ItemTouchHelperListener

class TodoAdapter(
    private val swipeCallback: (DailyTodo) -> (Unit),
    private val editButtonCallback: (Todo) -> (Unit)
) : ListAdapter<TodoWithTodayDailyTodo, TodoAdapter.TodoViewHolder>(TodoDiff()), ItemTouchHelperListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            editButtonCallback
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

    override fun onItemSwipe(position: Int, state: TodoState) {
        val todoDailyTodo = currentList[position].todayDailyTodo.copy(todoState = state)
        swipeCallback(todoDailyTodo)

        notifyItemRemoved(position)
        notifyItemInserted(position)
    }

    class TodoViewHolder(private val binding: ItemTodoBinding,
                         private val editButtonCallback: (Todo) -> (Unit)) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var state: TodoState

        init {
            binding.btnHomeTodoEdit.setOnClickListener {
                binding.item?.let { todoWithTodayDailyTodo ->
                    editButtonCallback(todoWithTodayDailyTodo.todo)
                }
            }
        }

        fun bind(item: TodoWithTodayDailyTodo) {
            binding.item = item
            state = item.todayDailyTodo.todoState

            val color = when (state) {
                TodoState.NOTHING -> R.color.black
                else -> R.color.white
            }

            binding.tvHomeTodo.setTextColor(ContextCompat.getColor(binding.root.context, color))
            binding.executePendingBindings()
        }
    }

    class TodoDiff : DiffUtil.ItemCallback<TodoWithTodayDailyTodo>() {
        override fun areItemsTheSame(oldItem: TodoWithTodayDailyTodo, newItem: TodoWithTodayDailyTodo): Boolean {
            return oldItem.todo.todoId == newItem.todo.todoId
        }

        override fun areContentsTheSame(oldItem: TodoWithTodayDailyTodo, newItem: TodoWithTodayDailyTodo): Boolean {
            return oldItem == newItem
        }
    }
}
