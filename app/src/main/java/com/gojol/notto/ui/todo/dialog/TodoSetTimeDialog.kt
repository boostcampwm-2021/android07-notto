package com.gojol.notto.ui.todo.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoSetTimeBinding
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

const val TIME_START = "timeStart"
const val TIME_START_DATE = "timeStartDate"

const val TIME_FINISH = "timeFinish"
const val TIME_FINISH_DATE = "timeFinishDate"
const val CURRENT = ""

@AndroidEntryPoint
class TodoSetTimeDialog : TodoBaseDialogImpl() {
    private lateinit var contentBinding: DialogTodoSetTimeBinding

    companion object {
        var callback: ((LocalTime) -> Unit?)? = null
        var currentState: String? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contentBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_todo_set_time, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initDate()
        setClickListener()
        binding.clDialogContent.addView(contentBinding.root)
    }

    private fun initDate() {
        arguments?.let { arg ->
            (arg.getSerializable(TIME_START_DATE) as LocalTime?)?.let{
                viewModel.setTimeStart(it)
                arg.remove(TIME_START_DATE)
            }
            (arg.getSerializable(TIME_FINISH_DATE) as LocalTime?)?.let {
                viewModel.setTimeFinish(it)
                arg.remove(TIME_FINISH_DATE)
            }
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.timeStart.observe(this) {
            if (currentState == TIME_START) {
                setTime(it.hour, it.minute)
            }
        }

        viewModel.timeFinish.observe(this) {
            if (currentState == TIME_FINISH) {
                setTime(it.hour, it.minute)
            }
        }
    }

    private fun setTime(hour: Int, minute: Int) {
        with(contentBinding.tpSetTime) {
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
        contentBinding.tpSetTime.setOnTimeChangedListener { timePicker, hour, minute ->
            if (currentState == TIME_START) {
                viewModel.setTimeStart(LocalTime.of(hour, minute))
            } else if (currentState == TIME_FINISH) {
                viewModel.setTimeFinish(LocalTime.of(hour, minute))
            }
        }
    }

    override fun confirmClick() {
        super.confirmClick()
        callback?.let {
            with(contentBinding.tpSetTime) {
                if (Build.VERSION.SDK_INT >= 23) {
                    it(LocalTime.of(hour, minute))
                } else {
                    it(LocalTime.of(currentHour, currentMinute))
                }
            }
        }
    }
}
