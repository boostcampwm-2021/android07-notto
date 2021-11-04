package com.gojol.notto.ui.home

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.model.data.BindingData

@BindingAdapter("item")
fun bindItems(recyclerview: RecyclerView, concatList: BindingData?) {
    val adapter = recyclerview.adapter as ConcatAdapter
    adapter.adapters.forEach {
        when(it) {
            is TodoAdapter -> {
                concatList?.todoList?.let { list ->
                    it.submitList(list)
                }
            }
            is LabelWrapperAdapter -> {
                concatList?.labelList?.let { list ->
                    it.getLabelAdapter().submitList(list)
                }
            }
        }
    }
}

