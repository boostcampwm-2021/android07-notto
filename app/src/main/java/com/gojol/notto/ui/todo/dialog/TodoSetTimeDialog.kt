package com.gojol.notto.ui.todo.dialog

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoSetTimeBinding
import com.gojol.notto.util.get24Time

class TodoSetTimeDialog(context: Context) : TodoBaseDialogImpl(context) {
    private val binding: DialogTodoSetTimeBinding =
        DataBindingUtil.inflate(
            LayoutInflater.from(context), R.layout.dialog_todo_set_time,
            null,
            false
        )
    var data: String? = null
        set(value) {
            value?.let {
                it.get24Time().split(":").apply {
                    if (Build.VERSION.SDK_INT >= 23) {
                        binding.tpSetTime.hour = get(0).toInt()
                        binding.tpSetTime.minute = get(1).toInt()
                    } else {
                        binding.tpSetTime.currentHour = get(0).toInt()
                        binding.tpSetTime.currentMinute = get(1).toInt()
                    }
                }
            }
            field = value
        }
    var callback: ((String) -> Unit?)? = null


    init {
        setBinding(binding)
        setDialog(WIDTH, HEIGHT)
        initClickListener()
    }

    companion object {
        const val WIDTH = 0.8f
        const val HEIGHT = 0.8f
    }

    private fun initClickListener() {
        binding.bvDialogDeletion.btnDialogBaseConfirm.setOnClickListener {
            confirm()
        }
        binding.bvDialogDeletion.btnDialogBaseReject.setOnClickListener {
            dismiss()
        }
    }

    override fun confirm() {
        callback?.let {
            if (Build.VERSION.SDK_INT >= 23) {
                it("${binding.tpSetTime.hour}:${binding.tpSetTime.minute}")
            } else {
                it("${binding.tpSetTime.currentHour}:${binding.tpSetTime.currentMinute}")
            }
        }

        // TODO : 얻은 정보 Todo 편집 화면으로 보내기
        super.confirm()
    }
}