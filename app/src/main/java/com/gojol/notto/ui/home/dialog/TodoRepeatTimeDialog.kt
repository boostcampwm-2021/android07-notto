package com.gojol.notto.ui.home.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoRepeatTimeBinding
import java.util.*


class TodoRepeatTimeDialog(context: Context) : View(context) {

    private val binding: DialogTodoRepeatTimeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.dialog_todo_repeat_time,
        null,
        false
    )
    private var dialog: Dialog = Dialog(context)

    init {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_dialog))

        initClickListener()
        initCalendar()
        show()
    }

    private fun initClickListener() {
        binding.btnDialogRepeatTimeConfirm.setOnClickListener {
            confirm()
        }
        binding.btnDialogRepeatTimeReject.setOnClickListener {
            dismiss()
        }
    }

    private fun initCalendar() {
        binding.cvRepeatTime.setSelectedDate(Date(System.currentTimeMillis()))
    }

    fun show() {
        dialog.show()
    }

    private fun confirm() {
        val selectedDate = binding.cvRepeatTime.selectedDate
    }

    private fun dismiss() {
        dialog.dismiss()
    }
}