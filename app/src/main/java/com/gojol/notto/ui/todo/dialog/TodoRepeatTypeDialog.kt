package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.core.view.children
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogTodoRepeatTypeBinding
import com.gojol.notto.model.data.RepeatType
import dagger.hilt.android.AndroidEntryPoint

const val REPEAT_TYPE_DATA = "repeatTypeValue"
const val REPEAT_TYPE = "repeatType"

@AndroidEntryPoint
class TodoRepeatTypeDialog : TodoBaseDialogImpl() {
    private lateinit var contentBinding: DialogTodoRepeatTypeBinding

    companion object {
        var callback: ((RepeatType) -> Unit?)? = null
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contentBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_todo_repeat_type, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding.lifecycleOwner = viewLifecycleOwner
        contentBinding.viewmodel = viewModel
        initRepeatType()
        binding.clDialogContent.addView(contentBinding.root)
    }

    private fun initRepeatType() {
        arguments?.let { arg ->
            arg.getSerializable(REPEAT_TYPE_DATA)?.let {
                viewModel.setRepeatType(it as RepeatType)
                arg.remove(REPEAT_TYPE_DATA)
            }
        }
    }

    override fun confirmClick() {
        super.confirmClick()
        callback?.let {
            viewModel.repeatType.value?.let(it)
        }
    }
}