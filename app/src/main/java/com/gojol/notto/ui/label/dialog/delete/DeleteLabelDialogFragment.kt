package com.gojol.notto.ui.label.dialog.delete

import android.os.Bundle
import android.view.View
import androidx.core.os.bundleOf
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.DIALOG_LABEL_ITEM_KEY
import com.gojol.notto.databinding.DialogFragmentDeleteLabelBinding
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.ui.BaseDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteLabelDialogFragment : BaseDialog<DialogFragmentDeleteLabelBinding, DeleteLabelDialogViewModel>() {

    override val viewModel: DeleteLabelDialogViewModel by viewModels()
    override var layoutId: Int = R.layout.dialog_fragment_delete_label

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewmodel = viewModel
        requireArguments().getParcelable<Label>(DIALOG_LABEL_ITEM_KEY)?.let { label ->
            viewModel.setLabel(label)
        }
    }

    override fun initObserver() { }

    override fun confirmClick() {
        viewModel.clickOkay()
        this.dialog?.cancel()
    }

    override fun dismissClick() {
        this.dialog?.cancel()
    }

    companion object {
        fun newInstance(label: Label): DeleteLabelDialogFragment {
            return DeleteLabelDialogFragment().apply {
                arguments = bundleOf(DIALOG_LABEL_ITEM_KEY to label)
            }
        }
    }
}
