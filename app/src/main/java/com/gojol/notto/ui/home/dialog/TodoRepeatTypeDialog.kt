package com.gojol.notto.ui.home.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoRepeatTypeBinding


class TodoRepeatTypeDialog(context: Context) : View(context) {

    private val binding: DialogTodoRepeatTypeBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.dialog_todo_repeat_type,
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
        binding.btnDialogRepeatTypeConfirm.setOnClickListener {
            confirm()
        }
        binding.btnDialogRepeatTypeReject.setOnClickListener {
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