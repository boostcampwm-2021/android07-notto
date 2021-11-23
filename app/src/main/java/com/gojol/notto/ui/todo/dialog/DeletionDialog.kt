package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.TodoDeleteType
import com.gojol.notto.databinding.DialogDeletionBinding
import com.gojol.notto.ui.todo.dialog.util.DeletionDialogViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeletionDialog : BaseDialog<DialogDeletionBinding, DeletionDialogViewModel>() {

    override val viewModel: DeletionDialogViewModel by viewModels()
    override var layoutId: Int = R.layout.dialog_deletion

    var deletionCallback: ((TodoDeleteType) -> Unit)? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deletionCallback?.let { viewModel.setDeletionCallback(it) }
        binding.viewmodel = viewModel
    }

    override fun confirmClick() {
        viewModel.deletionCallback.value?.let { it ->
            viewModel.todoDeleteType.value?.let(it)
        }
        this.dismiss()
    }

    override fun dismissClick() {
        this.dismiss()
    }

    override fun initObserver() {}

    companion object {
        fun newInstance(callback: (TodoDeleteType) -> Unit): DeletionDialog {
            return DeletionDialog().apply {
                deletionCallback = callback
            }
        }
    }
}
