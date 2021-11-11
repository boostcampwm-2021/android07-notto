package com.gojol.notto.ui.todo.dialog

import android.content.Context
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogDeletionBinding

class TodoDeletionDialog(context: Context) : TodoBaseDialogImpl(context) {
    private val binding: DialogDeletionBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_deletion,
            null,
            false
        )

    init {
        setBinding(binding)
        setDialog()
        initClickListener()
    }

    // TODO : 이것도 공통인데 어떻게 줄일까?
    private fun initClickListener() {
        binding.bvDialogDeletion.btnDialogBaseConfirm.setOnClickListener {
            confirm()
        }
        binding.bvDialogDeletion.btnDialogBaseReject.setOnClickListener {
            dismiss()
        }
    }
}