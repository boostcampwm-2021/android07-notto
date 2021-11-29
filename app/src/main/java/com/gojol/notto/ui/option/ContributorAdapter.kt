package com.gojol.notto.ui.option

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.gojol.notto.databinding.ItemContributorBinding
import com.gojol.notto.network.Contributor

class ContributorAdapter :
    ListAdapter<Contributor, ContributorAdapter.ContributorViewHolder>(ContributorDiffUtil()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributorViewHolder {
        return ContributorViewHolder(
            ItemContributorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ContributorViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ContributorViewHolder(private val binding: ItemContributorBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Contributor) {
            binding.item = item
            Glide.with(binding.ivContributor)
                .load(item.avatarUrl)
                .into(binding.ivContributor)
            binding.executePendingBindings()
        }
    }

    class ContributorDiffUtil : DiffUtil.ItemCallback<Contributor>() {
        override fun areItemsTheSame(oldItem: Contributor, newItem: Contributor): Boolean {
            return oldItem.login == newItem.login
        }

        override fun areContentsTheSame(
            oldItem: Contributor,
            newItem: Contributor
        ): Boolean {
            return oldItem == newItem
        }

    }
}
