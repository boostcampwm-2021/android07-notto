/**
 * Original Code
 * https://github.com/akexorcist/ConcatAdapterMultipleLayoutManager
 * modified by gojol
 */

package com.gojol.notto.ui.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.common.AdapterViewType
import com.gojol.notto.databinding.ItemLabelListBinding

class LabelWrapperAdapter(private val adapter: LabelAdapter) :
    RecyclerView.Adapter<LabelWrapperAdapter.LabelWrapperViewHolder>() {

    private var lastScrollX = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelWrapperViewHolder {
        return LabelWrapperViewHolder(
            ItemLabelListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemViewType(position: Int): Int {
        return AdapterViewType.LABELWRAPPER.viewType
    }

    override fun onBindViewHolder(holder: LabelWrapperViewHolder, position: Int) {
        holder.bind(adapter, lastScrollX) { x ->
            lastScrollX = x
        }
    }

    override fun getItemCount(): Int = 1

    class LabelWrapperViewHolder(private val binding: ItemLabelListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(adapter: LabelAdapter, lastScrollX: Int, onScrolled: (Int) -> Unit) {
            val context = binding.root.context

            binding.rvHomeLabel.apply {
                layoutManager =
                    LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                this.adapter = adapter
                doOnPreDraw {
                    scrollBy(lastScrollX, 0)
                }
                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        onScrolled(recyclerView.computeHorizontalScrollOffset())
                    }
                })
            }
        }
    }

    fun getLabelAdapter() = adapter
}
