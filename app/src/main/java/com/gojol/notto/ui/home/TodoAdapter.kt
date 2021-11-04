package com.gojol.notto.ui.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.common.TodoSuccessType
import com.gojol.notto.databinding.ItemTodoBinding
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.ui.home.utils.ItemTouchHelperListener

class TodoAdapter : ListAdapter<Todo, TodoAdapter.TodoViewHolder>(TodoDiff()), ItemTouchHelperListener {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        return TodoViewHolder(
            ItemTodoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE
    }

    companion object {
        const val VIEW_TYPE = 4
    }

    class TodoViewHolder(private val binding: ItemTodoBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Todo) {
            binding.item = item
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

    override fun onItemMove(from_position: Int, to_position: Int): Boolean {
        return false
    }

    override fun onItemSwipe(position: Int, successType: TodoSuccessType) {
        //notifyItemChanged(position)
        notifyDataSetChanged()
    }
}
