package com.gojol.notto.ui.todo.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoRepeatTimeBinding
import com.gojol.notto.util.getDate
import com.gojol.notto.util.getDateString
import java.util.Date


class TodoRepeatTimeDialog(context: Context) : TodoBaseDialogImpl(context) {
    private val binding: DialogTodoRepeatTimeBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_todo_repeat_time,
            null,
            false
        )
    var data: String? = null
        set(value) {
            value?.let { binding.cvRepeatTime.setSelectedDate(it.getDate()) }
            field = value
        }

    init {
        setBinding(binding)
        setDialog()
        initClickListener()
        initCalendar()
    }
    var callback: ((String) -> Unit?)? = null

    private fun initClickListener() {
        binding.bvDialogDeletion.btnDialogBaseConfirm.setOnClickListener {
            confirm()
        }
        binding.bvDialogDeletion.btnDialogBaseReject.setOnClickListener {
            dismiss()
        }
    }

    private fun initCalendar() {
        binding.cvRepeatTime.setSelectedDate(Date(System.currentTimeMillis()))
    }

    override fun confirm() {
        val selectedDate: Date = binding.cvRepeatTime.selectedDate.date
        callback?.let { it(selectedDate.getDateString()) }
        super.confirm()
    }
}