package com.gojol.notto.ui.todo.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.RadioButton
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoRepeatTypeBinding
import com.gojol.notto.model.data.RepeatType


class TodoRepeatTypeDialog(context: Context) : TodoBaseDialogImpl(context) {
    private val binding: DialogTodoRepeatTypeBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_todo_repeat_type,
            null,
            false
        )
    var data: RepeatType? = null
        get() = binding
            .rgRepeatType
            .findViewById<RadioButton>(binding.rgRepeatType.checkedRadioButtonId)
            .tag as RepeatType?
        set(value) {
            binding.rgRepeatType.children.forEach {
                if (it.tag == value) {
                    (it as RadioButton).isChecked = true
                }
            }
            field = value
        }
    var callback: ((RepeatType) -> Unit?)? = null


    init {
        setBinding(binding)
        setDialog()
        initClickListener()
    }

    private fun initClickListener() {
        binding.bvDialogDeletion.btnDialogBaseConfirm.setOnClickListener {
            callback?.let { data?.let { d -> it(d) } }
            confirm()
        }
        binding.bvDialogDeletion.btnDialogBaseReject.setOnClickListener {
            dismiss()
        }
    }
}