package com.gojol.notto.ui.home

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo

@BindingAdapter("item")
fun bindItems(recyclerview: RecyclerView, concatList: List<List<Any>?>) {
    val adapter = recyclerview.adapter as ConcatAdapter
    adapter.adapters.forEach {
        Log.d("adapter", it.toString())
        when(it) {
            is TodoAdapter -> {
                concatList.forEach { list ->
                    list?.let { li ->
                        val nList = li.filterIsInstance<Todo>()
                        if(nList.isNotEmpty()) {
                            it.submitList(nList)
                        }
                    }
                }
            }
            is LabelWrapperAdapter -> {
                concatList.forEach { list ->
                    list?.let { li ->
                        val nList = li.filterIsInstance<Label>()
                        if(nList.isNotEmpty()) {
                            it.getLabelAdapter().submitList(nList)
                        }
                    }
                }
            }
        }
    }
}

