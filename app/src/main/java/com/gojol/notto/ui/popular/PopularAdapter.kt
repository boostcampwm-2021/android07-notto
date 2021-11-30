package com.gojol.notto.ui.popular

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.nottokeyword.Keyword
import com.gojol.notto.databinding.ItemPopularKeywordBinding

class PopularAdapter(private val callback: (String?) -> Unit) :
    ListAdapter<Keyword, PopularAdapter.PopularViewHolder>(PopularDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularViewHolder {
        return PopularViewHolder(
            ItemPopularKeywordBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            callback
        )
    }

    override fun onBindViewHolder(holder: PopularViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class PopularViewHolder(
        private val binding: ItemPopularKeywordBinding,
        private val callback: (String?) -> Unit
    ): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.apply {
                setClickKeywordListener {
                    callback(keyword?.word)
                }
            }
        }

        fun bind(item: Keyword) {
            binding.apply {
                keyword = item
                place = absoluteAdapterPosition + 1
                executePendingBindings()
            }
        }
    }

    class PopularDiffUtil : DiffUtil.ItemCallback<Keyword>() {

        override fun areItemsTheSame(oldItem: Keyword, newItem: Keyword): Boolean {
            return oldItem.word == newItem.word
        }

        override fun areContentsTheSame(oldItem: Keyword, newItem: Keyword): Boolean {
            return oldItem == newItem
        }
    }
}
