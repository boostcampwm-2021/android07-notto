package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.REPEAT_TYPE_DATA
import com.gojol.notto.common.RepeatType
import com.gojol.notto.databinding.DialogTodoRepeatTypeBinding
import com.gojol.notto.ui.BaseDialog
import com.gojol.notto.ui.todo.dialog.util.RepeatTypeDialogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RepeatTypeDialog : BaseDialog<DialogTodoRepeatTypeBinding, RepeatTypeDialogViewModel>() {

    override val viewModel: RepeatTypeDialogViewModel by viewModels()
    override var layoutId: Int = R.layout.dialog_todo_repeat_type

    var repeatTypeCallback: ((RepeatType) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        repeatTypeCallback?.let { viewModel.setRepeatTypeCallback(it) }
        initRepeatType()
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
        viewModel.repeatType.value?.let{
            viewModel.repeatTypeCallback.value?.let { callback ->
                callback(it)
            }
        }
        this.dismiss()
    }

    override fun dismissClick() {
        this.dismiss()
    }

    override fun initObserver() {}

    companion object {
        fun newInstance(bundle: Bundle, callback: (RepeatType) -> Unit): RepeatTypeDialog {
            return RepeatTypeDialog().apply {
                arguments = bundle
                this.repeatTypeCallback = callback
            }
        }
    }
}