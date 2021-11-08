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
import com.gojol.notto.common.TodoSuccessType
import com.gojol.notto.databinding.ItemTodoBinding
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.ui.home.util.ItemTouchHelperListener

class TodoAdapter(
    private val swipeCallback: (Todo) -> (Unit)
) : ListAdapter<Todo, TodoAdapter.TodoViewHolder>(TodoDiff()), ItemTouchHelperListener {

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
    override fun onItemSwipe(position: Int, successType: TodoSuccessType) {
        if (position < 0) return
        val todo = currentList[position].copy(isSuccess = successType)
        swipeCallback(todo)
        notifyDataSetChanged()
    }

    class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        lateinit var successType: TodoSuccessType

        fun bind(item: Todo) {
            binding.item = item
            successType = item.isSuccess

            val color = when (successType) {
                TodoSuccessType.NOTHING -> R.color.black
                else -> R.color.white
            }

            binding.tvHomeTodo.setTextColor(ContextCompat.getColor(binding.root.context, color))
            binding.executePendingBindings()
        }
    }

    class TodoDiff : DiffUtil.ItemCallback<Todo>() {
        override fun areItemsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem.todoId == newItem.todoId
        }

        override fun areContentsTheSame(oldItem: Todo, newItem: Todo): Boolean {
            return oldItem == newItem
        }
    }
}
