package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoRepeatTimeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

const val REPEAT_TIME_DATA = "repeatTimeData"
const val REPEAT_TIME = "repeatTime"

@AndroidEntryPoint
class TodoRepeatTimeDialog : TodoBaseDialogImpl() {
    private lateinit var contentBinding: DialogTodoRepeatTimeBinding

    companion object {
        var callback: ((LocalDate) -> Unit?)? = null
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        contentBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_todo_repeat_time, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding.lifecycleOwner = viewLifecycleOwner
        contentBinding.viewmodel = viewModel
        initRepeatTime()
        initClickListener()
        binding.clDialogContent.addView(contentBinding.root)
    }

    private fun initRepeatTime() {
        arguments?.let { arg ->
            (arg.getSerializable(REPEAT_TIME_DATA) as LocalDate).apply {
                viewModel.setRepeatTime(this)
                arg.remove(REPEAT_TIME_DATA)
            }
        }
    }

    private fun initClickListener() {
        contentBinding.cvRepeatTime.setOnDateChangedListener { _, date, _ ->
            viewModel.setRepeatTime(
                date.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            )
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.repeatTime.observe(this) {
            contentBinding.cvRepeatTime.setSelectedDate(
                Date.from(
                    it.atStartOfDay(ZoneId.systemDefault()).toInstant()
                )
            )
        }
    }

    override fun confirmClick() {
        super.confirmClick()
        callback?.let {
            viewModel.repeatTime.value?.let { date ->
                it(date)
            }
        }
    }
}
