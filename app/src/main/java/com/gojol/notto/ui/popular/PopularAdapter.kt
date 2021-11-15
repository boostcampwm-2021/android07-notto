package com.gojol.notto.ui.popular

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.databinding.ItemPopularKeywordBinding

class PopularAdapter : ListAdapter<PopularKeyword, PopularAdapter.PopularViewHolder>(PopularDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(
            ItemPopularKeywordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PopularViewHolder(private val binding: ItemPopularKeywordBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: PopularKeyword) {
            binding.apply {
                keyword = item
                executePendingBindings()
            }
        }
    }

    class PopularDiffUtil : DiffUtil.ItemCallback<PopularKeyword>() {

        override fun areItemsTheSame(oldItem: PopularKeyword, newItem: PopularKeyword): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: PopularKeyword, newItem: PopularKeyword): Boolean {
            return oldItem == newItem
        }
    }
}
