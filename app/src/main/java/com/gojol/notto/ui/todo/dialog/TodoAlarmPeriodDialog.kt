package com.gojol.notto.ui.todo.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoAlarmPeriodBinding


class TodoAlarmPeriodDialog(context: Context) : TodoBaseDialogImpl(context) {
    private val binding: DialogTodoAlarmPeriodBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_todo_alarm_period,
            null,
            false
        )

    init {
        setBinding(binding)
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