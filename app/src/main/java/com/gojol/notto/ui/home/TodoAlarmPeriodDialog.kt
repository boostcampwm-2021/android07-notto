package com.gojol.notto.ui.home

import android.app.Dialog
import android.content.Context
import android.content.res.ColorStateList
import android.view.View
import android.view.Window
import android.view.LayoutInflater
import android.widget.RadioButton
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoAlarmPeriodBinding


class TodoAlarmPeriodDialog(context: Context) : View(context) {

    private val binding: DialogTodoAlarmPeriodBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.dialog_todo_alarm_period,
        null,
        false
    )
    private var dialog: Dialog = Dialog(context)

    init {

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_dialog))

        initClickListener()
        show()
    }

    private fun initClickListener() {
        binding.btnDialogAlarmPeriodConfirm.setOnClickListener {
            confirm()
        }
        binding.btnDialogAlarmPeriodReject.setOnClickListener {
            dismiss()
        }
    }

    fun show() {
        dialog.show()
    }

    private fun confirm() {

    }

    private fun dismiss() {
        dialog.dismiss()
    }
}