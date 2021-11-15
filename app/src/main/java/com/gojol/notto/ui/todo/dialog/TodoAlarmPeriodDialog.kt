package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.databinding.DialogTodoAlarmPeriodBinding
import dagger.hilt.android.AndroidEntryPoint

const val TIME_REPEAT_DATA = "timeRepeatData"
const val TIME_REPEAT = "timeRepeat"

@AndroidEntryPoint
class TodoAlarmPeriodDialog : TodoBaseDialogImpl() {
    private lateinit var contentBinding: DialogTodoAlarmPeriodBinding

    companion object {
        var callback: ((TimeRepeatType) -> Unit?)? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contentBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_todo_alarm_period, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding.lifecycleOwner = viewLifecycleOwner
        contentBinding.viewmodel = viewModel
        initTimeRepeat()
        binding.clDialogContent.addView(contentBinding.root)
    }

    private fun initTimeRepeat() {
        arguments?.let { arg ->
            arg.getSerializable(TIME_REPEAT_DATA)?.let {
                viewModel.setTimeRepeat(it as TimeRepeatType)
                arg.remove(TIME_REPEAT_DATA)
            }
        }
    }

    override fun confirmClick() {
        super.confirmClick()
        callback?.let {
            viewModel.timeRepeat.value?.let(it)
        }
    }
}