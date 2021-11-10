package com.gojol.notto.ui.home.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoRepeatTimeBinding
import java.util.*


class TodoRepeatTimeDialog(context: Context) : TodoBaseDialogImpl(context) {
    private val binding: DialogTodoRepeatTimeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.dialog_todo_repeat_time,
        null,
        false
    )

    init {
        initClickListener()
        initCalendar()
    }

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
        val selectedDate = binding.cvRepeatTime.selectedDate
        super.confirm()
    }
}