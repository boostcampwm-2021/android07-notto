package com.gojol.notto.ui.label.dialog.edit

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.DIALOG_LABEL_ITEM_KEY
import com.gojol.notto.databinding.DialogFragmentEditLabelBinding
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.ui.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditLabelDialogFragment : BaseDialog<DialogFragmentEditLabelBinding, EditLabelDialogViewModel>() {

    override val viewModel: EditLabelDialogViewModel by viewModels()
    override var layoutId: Int = R.layout.dialog_fragment_edit_label

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        requireArguments().getParcelable<Label>(DIALOG_LABEL_ITEM_KEY).let { label ->
            viewModel.setEditType(label)
        }
    }

    override fun initObserver() {}

    override fun confirmClick() {
        viewModel.clickOkay()
        this.dialog?.cancel()
    }

    override fun dismissClick() {
        this.dialog?.cancel()
    }

    companion object {
        fun newInstance(label: Label?): EditLabelDialogFragment {
            return EditLabelDialogFragment().apply {
                arguments = bundleOf(DIALOG_LABEL_ITEM_KEY to label)
            }
        }
    }
}

