package com.gojol.notto.ui.label

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.model.database.label.Label

object LabelsListBindings {

    @BindingAdapter("app:items")
    @JvmStatic
    fun setItems(recyclerView: RecyclerView, items: List<Label>?) {
        if (items.isNullOrEmpty().not()) {
            (recyclerView.adapter as EditLabelAdapter).differ.submitList(items)
        }
    }
}
