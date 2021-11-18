package com.gojol.notto.ui.todo

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.databinding.ItemEditTodoLabelBinding
import com.gojol.notto.model.database.label.Label

class SelectedLabelAdapter(private val labelCallback: (Label) -> (Unit)) :
    ListAdapter<Label, SelectedLabelAdapter.SelectedLabelViewHolder>(LabelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SelectedLabelViewHolder {
        return SelectedLabelViewHolder(
            ItemEditTodoLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            labelCallback
        )
    }

    override fun onBindViewHolder(holder: SelectedLabelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class SelectedLabelViewHolder(
        private val binding: ItemEditTodoLabelBinding,
        private val callback: (Label) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.chipHomeLabel.setOnClickListener {
                binding.item?.let { label ->
                    callback(label)
                }
            }
        }

        fun bind(item: Label) {
            binding.item = item
            binding.chipHomeLabel.isChecked = false
            binding.executePendingBindings()
        }
    }

    class LabelDiffCallback : DiffUtil.ItemCallback<Label>() {
        override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem.labelId == newItem.labelId
        }

        override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem == newItem
        }

    }
}
