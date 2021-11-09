package com.gojol.notto.ui.label

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.databinding.ItemEditLabelBinding
import com.gojol.notto.model.database.label.Label

class EditLabelAdapter(private val clickLabelCallback: (Label?) -> (Unit)) :
    ListAdapter<Label, EditLabelAdapter.EditLabelViewHolder>(EditLabelDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditLabelViewHolder {
        return EditLabelViewHolder(
            ItemEditLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            clickLabelCallback
        )
    }

    override fun onBindViewHolder(holder: EditLabelViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class EditLabelViewHolder(
        private val binding: ItemEditLabelBinding,
        private val clickLabelCallback: (Label?) -> (Unit)
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setClickListener {
                clickLabelCallback(binding.label)
            }
        }

        fun bind(item: Label) {
            binding.apply {
                label = item
                executePendingBindings()
            }
        }
    }

    class EditLabelDiffCallback : DiffUtil.ItemCallback<Label>() {

        override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem.labelId == newItem.labelId
        }

        override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem == newItem
        }
    }
}
