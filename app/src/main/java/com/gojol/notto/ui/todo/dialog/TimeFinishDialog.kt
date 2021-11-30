package com.gojol.notto.ui.todo.dialog

import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.TIME_FINISH_DATA
import com.gojol.notto.databinding.DialogTodoTimeFinishBinding
import com.gojol.notto.ui.BaseDialog
import com.gojol.notto.ui.todo.dialog.util.TimeFinishDialogViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalTime

@AndroidEntryPoint
class TimeFinishDialog : BaseDialog<DialogTodoTimeFinishBinding, TimeFinishDialogViewModel>() {

    override val viewModel: TimeFinishDialogViewModel by viewModels()
    override var layoutId: Int = R.layout.dialog_todo_time_finish

    private var startTime: LocalTime? = null

    var setTimeCallback: ((LocalTime) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        setTimeCallback?.let { viewModel.setTimeFinishCallback(it) }
        initDate()
        setClickListener()
    }

    override fun onStart() {
        super.onStart()
        resetDialogSize()
    }

    fun show(manager: FragmentManager, tag: String?, startTime: LocalTime?) {
        super.show(manager, tag)
        this.startTime = startTime
    }

    private fun resetDialogSize() {
        val deviceWidth = resources.displayMetrics.widthPixels
        val deviceHeight = resources.displayMetrics.heightPixels

        if (deviceWidth > deviceHeight) {
            val dialogWidth = deviceWidth * 0.8
            val dialogHeight = deviceHeight * 0.8
            dialog?.window?.setLayout(dialogWidth.toInt(), dialogHeight.toInt())
        }
    }

    private fun initDate() {
        arguments?.let { arg ->
            (arg.getSerializable(TIME_FINISH_DATA) as LocalTime?)?.let {
                viewModel.setTimeFinish(it)
                arg.remove(TIME_FINISH_DATA)
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
                val currTime = LocalTime.of(hour, minute)
                if (!isCorrectTime(currTime)) return
                viewModel.setTimeFinishCallback.value?.let { it(currTime) }
            } else {
                val currTime = LocalTime.of(currentHour, currentMinute)
                if (!isCorrectTime(currTime)) return
                viewModel.setTimeFinishCallback.value?.let { it(currTime) }
            }
        }
        this.dismiss()
    }

    private fun isCorrectTime(currTime: LocalTime): Boolean {
        val context = requireContext()
        startTime?.let {
            if (currTime <= startTime) {
                Toast.makeText(
                    context,
                    context.getString(R.string.dialog_end_exception),
                    Toast.LENGTH_SHORT
                ).show()
                return false
            }
        }
        return true
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
