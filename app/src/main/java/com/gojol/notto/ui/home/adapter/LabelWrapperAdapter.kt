/**
 * Original Code
 * https://github.com/akexorcist/ConcatAdapterMultipleLayoutManager
 * modified by gojol
 */

package com.gojol.notto.ui.home.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.common.AdapterViewType
import com.gojol.notto.databinding.ItemLabelListBinding

class LabelWrapperAdapter(
    private val adapter: LabelAdapter,
    private val onClickMenu: (View) -> Unit
) : RecyclerView.Adapter<LabelWrapperAdapter.LabelWrapperViewHolder>() {

    private var lastScrollX = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LabelWrapperViewHolder {
        return LabelWrapperViewHolder(
            ItemLabelListBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            onClickMenu
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

    fun onSaveState(outState: Bundle) {
        outState.putInt(KEY_SCROLL_X, lastScrollX)
    }

    fun onRestoreState(savedState: Bundle) {
        lastScrollX = savedState.getInt(KEY_SCROLL_X)
    }

    class LabelWrapperViewHolder(
        private val binding: ItemLabelListBinding,
        private val onClickMenu: (View) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.setEditClickListener {
                onClickMenu(it)
            }
        }

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

            binding.executePendingBindings()
        }
    }

    companion object {
        private const val KEY_SCROLL_X = "horizontal.wrapper.adapter.key_scroll_x"
    }
}
