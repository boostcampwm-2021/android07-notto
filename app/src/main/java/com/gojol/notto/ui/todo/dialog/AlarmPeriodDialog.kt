package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.TIME_REPEAT_DATA
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.databinding.DialogTodoAlarmPeriodBinding
import com.gojol.notto.ui.BaseDialog
import com.gojol.notto.ui.todo.dialog.util.AlarmPeriodDialogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AlarmPeriodDialog : BaseDialog<DialogTodoAlarmPeriodBinding, AlarmPeriodDialogViewModel>() {

    override val viewModel: AlarmPeriodDialogViewModel by viewModels()
    override var layoutId: Int = R.layout.dialog_todo_alarm_period

    var alarmPeriodCallback: ((TimeRepeatType) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        alarmPeriodCallback?.let { viewModel.setAlarmPeriodCallback(it) }
        initObserver()
        initTimeRepeat()
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
        viewModel.alarmPeriodCallback.value?.let {
            viewModel.timeRepeat.value?.let(it)
        }
        this.dismiss()
    }

    override fun dismissClick() {
        this.dismiss()
    }

    override fun initObserver() {}

    companion object {
        fun newInstance(bundle: Bundle, callback: (TimeRepeatType) -> Unit): AlarmPeriodDialog {
            return AlarmPeriodDialog().apply {
                arguments = bundle
                alarmPeriodCallback = callback
            }
        }
    }
}