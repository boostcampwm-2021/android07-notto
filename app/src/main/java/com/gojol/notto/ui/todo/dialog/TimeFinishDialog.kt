package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.SET_TIME_FINISH
import com.gojol.notto.databinding.DialogTodoTimeFinishBinding
import com.gojol.notto.ui.BaseDialog
import com.gojol.notto.ui.todo.dialog.util.TimeFinishDialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
class TimeFinishDialog : BaseDialog<DialogTodoTimeFinishBinding, TimeFinishDialogViewModel>() {

    override val viewModel: TimeFinishDialogViewModel by viewModels()
    override var layoutId: Int = R.layout.dialog_todo_time_finish

    var setTimeCallback: ((LocalTime) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        setTimeCallback?.let { viewModel.setTimeFinishCallback(it) }
        initDate()
        setClickListener()
    }

    private fun initDate() {
        arguments?.let { arg ->
            (arg.getSerializable(SET_TIME_FINISH) as LocalTime?)?.let {
                viewModel.setTimeFinish(it)
                arg.remove(SET_TIME_FINISH)
            }
        }
    }

    override fun initObserver() {
        viewModel.timeFinish.observe(this) {
            setTime(it.hour, it.minute)
        }
    }

    private fun setTime(hour: Int, minute: Int) {
        with(binding.tpTimeFinish) {
            if (Build.VERSION.SDK_INT >= 23) {
                setHour(hour)
                setMinute(minute)
            } else {
                currentHour = hour
                currentMinute = minute
            }
        }
    }

    private fun setClickListener() {
        binding.tpTimeFinish.setOnTimeChangedListener { timePicker, hour, minute ->
            viewModel.setTimeFinish(LocalTime.of(hour, minute))
        }
    }

    override fun confirmClick() {
        with(binding.tpTimeFinish) {
            if (Build.VERSION.SDK_INT >= 23) {
                viewModel.setTimeFinishCallback.value?.let { it(LocalTime.of(hour, minute)) }
            } else {
                viewModel.setTimeFinishCallback.value?.let { it(LocalTime.of(currentHour, currentMinute)) }
            }
        }
        this.dismiss()
    }

    override fun dismissClick() {
        this.dismiss()
    }

    companion object {
        fun newInstance(bundle: Bundle, callback: (LocalTime) -> Unit): TimeFinishDialog {
            return TimeFinishDialog().apply {
                arguments = bundle
                setTimeCallback = callback
            }
        }
    }
}
