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
import java.time.LocalTime
import java.time.ZoneId
import java.util.*

const val DATE_DATA = "dateData"
const val DATE = "date"

const val REPEAT_TIME_DATA = "repeatTimeData"
const val REPEAT_TIME = "repeatTime"

@AndroidEntryPoint
class TodoRepeatTimeDialog : TodoBaseDialogImpl() {
    private lateinit var contentBinding: DialogTodoRepeatTimeBinding

    companion object {
        var callback: ((LocalDate) -> Unit?)? = null
        var currentState: String? = null
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
            (arg.getSerializable(DATE_DATA) as LocalDate?)?.let{
                viewModel.setDate(it)
                arg.remove(DATE_DATA)
            }
        }
        arguments?.let { arg ->
            (arg.getSerializable(REPEAT_TIME_DATA) as LocalDate?)?.let{
                viewModel.setRepeatTime(it)
                arg.remove(REPEAT_TIME_DATA)
            }
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.date.observe(this) {
            if (currentState == DATE) {
                contentBinding.cvRepeatTime.setSelectedDate(
                    Date.from(
                        it.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    )
                )
            }
        }

        viewModel.repeatTime.observe(this) {
            if (currentState == REPEAT_TIME) {
                contentBinding.cvRepeatTime.setSelectedDate(
                    Date.from(
                        it.atStartOfDay(ZoneId.systemDefault()).toInstant()
                    )
                )
            }
        }
    }

    private fun initClickListener() {
        contentBinding.cvRepeatTime.setOnDateChangedListener { _, date, _ ->
            if (currentState == DATE) viewModel.setDate(
                date.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            )
            else if (currentState == REPEAT_TIME) viewModel.setRepeatTime(
                date.date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            )
        }
    }

    override fun confirmClick() {
        super.confirmClick()
        callback?.let {
            if (currentState == DATE) {
                viewModel.date.value?.let { date ->
                    it(date)
                }
            } else {
                viewModel.repeatTime.value?.let { date ->
                    it(date)
                }
            }
        }
    }
}
