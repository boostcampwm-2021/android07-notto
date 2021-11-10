package com.gojol.notto.ui.home.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoRepeatTypeBinding


class TodoRepeatTypeDialog(context: Context) : TodoBaseDialogImpl(context) {
    private val binding: DialogTodoRepeatTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.dialog_todo_repeat_type,
        null,
        false
    )

    init {
        initClickListener()
    }

    private fun initClickListener() {
        binding.bvDialogDeletion.btnDialogBaseConfirm.setOnClickListener {
            confirm()
        }
        binding.bvDialogDeletion.btnDialogBaseReject.setOnClickListener {
            dismiss()
        }
    }
}