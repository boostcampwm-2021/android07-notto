package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.common.TodoDeleteType
import com.gojol.notto.databinding.DialogDeletionBinding
import dagger.hilt.android.AndroidEntryPoint

const val DELETE = "todoDelete"

@AndroidEntryPoint
class TodoDeletionDialog : TodoBaseDialogImpl() {
    private lateinit var contentBinding: DialogDeletionBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        contentBinding =
            DataBindingUtil.inflate(inflater, R.layout.dialog_deletion, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentBinding.lifecycleOwner = viewLifecycleOwner
        contentBinding.viewmodel = viewModel
        binding.clDialogContent.addView(contentBinding.root)
    }

    override fun confirmClick() {
        super.confirmClick()
        deleteTodoCallback?.let { it(viewModel.todoDeleteType.value) }
    }

    companion object {
        var deleteTodoCallback: ((TodoDeleteType?) -> Unit)? = null
    }
}
