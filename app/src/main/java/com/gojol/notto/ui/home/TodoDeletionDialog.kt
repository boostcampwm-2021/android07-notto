package com.gojol.notto.ui.home

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import android.view.LayoutInflater
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogDeletionBinding


class TodoDeletionDialog(context: Context) : View(context) {

    private val binding: DialogDeletionBinding = DataBindingUtil.inflate(
        LayoutInflater.from(context), R.layout.dialog_deletion,
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
        binding.btnDialogDeletionConfirm.setOnClickListener {
            confirm()
        }
        binding.btnDialogDeletionReject.setOnClickListener {
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