package com.gojol.notto.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.common.AdapterViewType
import com.gojol.notto.databinding.ItemLabelBinding
import com.gojol.notto.model.data.LabelWithCheck

class LabelAdapter(
    private val labelClickCallback: (LabelWithCheck) -> (Unit)
) : ListAdapter<LabelWithCheck, LabelAdapter.LabelViewHolder>(LabelDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelViewHolder {
        return LabelViewHolder(
            ItemLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: LabelViewHolder, position: Int) {
        holder.bind(getItem(position), labelClickCallback = labelClickCallback)
        holder.binding.item = currentList[position].labelWithTodo.label
    }

    override fun getItemViewType(position: Int): Int {
        return AdapterViewType.LABEL.viewType
    }

    class LabelViewHolder(val binding: ItemLabelBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(labelWithCheck: LabelWithCheck, labelClickCallback: (LabelWithCheck) -> (Unit)) {
            with(binding) {
                chipHomeLabel.isChecked = labelWithCheck.isChecked
                when (labelWithCheck.labelWithTodo.label.order) {
                    0 -> setHeaderLabel(labelWithCheck, labelClickCallback)
                    else -> setItemLabel(labelWithCheck, labelClickCallback)
                }
                executePendingBindings()
            }
        }

        private fun setHeaderLabel(labelWithCheck: LabelWithCheck, labelClickCallback: (LabelWithCheck) -> (Unit)) {
            with(binding) {
                chipHomeLabel.setOnClickListener {
                    chipHomeLabel.isChecked = true
                    labelClickCallback(labelWithCheck.copy(isChecked = true))
                }
            }
        }

        private fun setItemLabel(labelWithCheck: LabelWithCheck, labelClickCallback: (LabelWithCheck) -> (Unit)) {
            binding.chipHomeLabel.setOnClickListener {
                binding.chipHomeLabel.isChecked = !labelWithCheck.isChecked
                labelClickCallback(labelWithCheck.copy(isChecked = !labelWithCheck.isChecked))
            }
        }
    }

    class LabelDiff : DiffUtil.ItemCallback<LabelWithCheck>() {
        override fun areItemsTheSame(oldItem: LabelWithCheck, newItem: LabelWithCheck): Boolean {
            return oldItem.labelWithTodo.label.labelId == newItem.labelWithTodo.label.labelId
        }

        override fun areContentsTheSame(oldItem: LabelWithCheck, newItem: LabelWithCheck): Boolean {
            return oldItem == newItem
        }
    }
}

