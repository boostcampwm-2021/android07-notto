package com.gojol.notto.ui.todo.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogDeletionBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoDeletionDialog : TodoBaseDialogImpl() {
    private lateinit var contentBinding: DialogDeletionBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        contentBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_deletion, container, false)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.clDialogContent.addView(contentBinding.root)
    }
}