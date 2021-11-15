package com.gojol.notto.ui.todo.dialog

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoSetTimeBinding
import com.gojol.notto.util.get24Hour
import com.gojol.notto.util.timeSplitFormatter
import dagger.hilt.android.AndroidEntryPoint

const val TIME_START = "timeStart"
const val TIME_START_DATE = "timeStartDate"

const val TIME_FINISH = "timeFinish"
const val TIME_FINISH_DATE = "timeFinishDate"
const val CURRENT = ""

@AndroidEntryPoint
class TodoSetTimeDialog : TodoBaseDialogImpl() {
    private lateinit var contentBinding: DialogTodoSetTimeBinding

    companion object {
        var callback: ((String) -> Unit?)? = null
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
            arg.getString(TIME_START_DATE)?.let {
                viewModel.setTimeStart(it.get24Hour())
                arg.remove(TIME_START_DATE)
            }
            arg.getString(TIME_FINISH_DATE)?.let {
                viewModel.setTimeFinish(it.get24Hour())
                arg.remove(TIME_FINISH_DATE)
            }
        }
    }

    override fun initObserver() {
        super.initObserver()
        viewModel.timeStart.observe(this) {
            if (currentState == TIME_START) {
                setTime(it.timeSplitFormatter())
            }
        }

        viewModel.timeFinish.observe(this) {
            if (currentState == TIME_FINISH) {
                setTime(it.timeSplitFormatter())
            }
        }
    }

    private fun setTime(list: List<String>) {
        with(contentBinding.tpSetTime) {
            if (Build.VERSION.SDK_INT >= 23) {
                hour = list[0].toInt()
                minute = list[1].toInt()
            } else {
                currentHour = list[0].toInt()
                currentMinute = list[1].toInt()
            }
        }
    }

    private fun setClickListener() {
        contentBinding.tpSetTime.setOnTimeChangedListener { timePicker, hour, minute ->
            if (currentState == TIME_START) {
                viewModel.setTimeStart("$hour:$minute")
            } else if (currentState == TIME_FINISH) {
                viewModel.setTimeFinish("$hour:$minute")
            }
        }
    }

    override fun confirmClick() {
        super.confirmClick()
        callback?.let {
            with(contentBinding.tpSetTime) {
                if (Build.VERSION.SDK_INT >= 23) {
                    it("${hour}:${minute}")
                } else {
                    it("${currentHour}:${currentMinute}")
                }
            }
        }
    }
}