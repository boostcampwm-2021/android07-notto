package com.gojol.notto.ui.home.dialog

import android.app.Dialog
import android.content.Context
import android.os.Build
import android.view.View
import android.view.Window
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoSetTimeBinding


class TodoSetTimeDialog(context: Context) : View(context) {

    private val binding: DialogTodoSetTimeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.dialog_todo_set_time,
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
        binding.btnDialogSetTimeConfirm.setOnClickListener {
            confirm()
        }
        binding.btnDialogSetTimeReject.setOnClickListener {
            dismiss()
        }
    }

    fun show() {
        dialog.show()
    }

    private fun confirm() {
        if (Build.VERSION.SDK_INT >= 23) {
            val hour = binding.tpSetTime.hour
            val minute = binding.tpSetTime.minute
        } else {
            val hour = binding.tpSetTime.currentHour
            val minute = binding.tpSetTime.currentMinute
        }
    }

    private fun dismiss() {
        dialog.dismiss()
    }
}