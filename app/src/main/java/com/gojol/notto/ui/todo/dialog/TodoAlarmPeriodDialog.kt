package com.gojol.notto.ui.todo.dialog

import android.content.Context
import android.view.LayoutInflater
import android.widget.RadioButton
import androidx.core.view.forEach
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.databinding.DialogTodoAlarmPeriodBinding


class TodoAlarmPeriodDialog(context: Context) : TodoBaseDialogImpl(context) {
    private val binding: DialogTodoAlarmPeriodBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_todo_alarm_period,
            null,
            false
        )
    var data: TimeRepeatType? = null
        get() = binding
            .rgDialogAlarmPeriod
            .findViewById<RadioButton>(binding.rgDialogAlarmPeriod.checkedRadioButtonId)
            .tag as TimeRepeatType?
        set(value) {
            binding.rgDialogAlarmPeriod.forEach {
                if (it.tag == value) {
                    (it as RadioButton).isChecked = true
                }
            }
            field = value
        }
    var callback: ((TimeRepeatType) -> Unit?)? = null

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