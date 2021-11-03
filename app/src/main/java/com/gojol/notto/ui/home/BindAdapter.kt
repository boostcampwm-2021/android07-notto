package com.gojol.notto.ui.home

import android.util.Log
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo

@BindingAdapter("item")
fun bindItems(recyclerview: RecyclerView, concatList: List<List<Any>>) {
    val adapter = recyclerview.adapter as ConcatAdapter
    adapter.adapters.forEach {
        Log.d("adapter", it.toString())
        when(it) {
            is TodoAdapter -> it.submitList(concatList[0] as List<Todo>)
            is LabelWrapperAdapter -> it.getLabelAdapter().submitList(concatList[1] as List<Label>)
        }
    }
}