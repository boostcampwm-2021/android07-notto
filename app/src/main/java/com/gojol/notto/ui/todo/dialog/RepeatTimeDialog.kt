package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.REPEAT_TIME_DATA
import com.gojol.notto.databinding.DialogTodoRepeatTimeBinding
import com.gojol.notto.ui.BaseDialog
import com.gojol.notto.ui.todo.dialog.util.RepeatTimeDialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

@AndroidEntryPoint
class RepeatTimeDialog : BaseDialog<DialogTodoRepeatTimeBinding, RepeatTimeDialogViewModel>() {

    override val viewModel: RepeatTimeDialogViewModel by viewModels()
    override var layoutId: Int = R.layout.dialog_todo_repeat_time

    var repeatTimeCallback: ((LocalDate) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        repeatTimeCallback?.let { viewModel.setRepeatTimeCallback(it) }

        binding.cvRepeatTime.state().edit().setMinimumDate(
            Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant())
        ).commit()

        initObserver()
        initRepeatTime()
        initClickListener()
    }

    private fun initRepeatTime() {
        arguments?.let { arg ->
            arg.getSerializable(REPEAT_TIME_DATA)?.let {
                viewModel.setRepeatTime(it as LocalDate)
                arg.remove(REPEAT_TIME_DATA)
            }
        }
    }

    private fun initClickListener() {
        binding.cvRepeatTime.setOnDateChangedListener { _, date, _ ->
            viewModel.setRepeatTime(
                date.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            )
        }
    }

    override fun initObserver() {
        viewModel.repeatTime.observe(this) {
            println(it)
            binding.cvRepeatTime.setSelectedDate(
                Date.from(
                    it.atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
            )
        }
    }

    override fun confirmClick() {
        viewModel.repeatTime.value?.let { date ->
            viewModel.repeatTimeCallback.value?.let { it(date) }
        }
        this.dismiss()
    }

    override fun dismissClick() {
        this.dismiss()
    }

    companion object {
        fun newInstance(bundle: Bundle, callback: (LocalDate) -> Unit): RepeatTimeDialog {
            return RepeatTimeDialog().apply {
                arguments = bundle
                repeatTimeCallback = callback
            }
        }
    }
}
