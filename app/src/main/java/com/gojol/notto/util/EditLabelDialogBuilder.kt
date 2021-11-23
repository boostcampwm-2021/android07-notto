package com.gojol.notto.util

import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.gojol.notto.common.LabelEditType
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.ui.label.dialog.delete.DeleteLabelDialogFragment
import com.gojol.notto.ui.label.dialog.edit.EditLabelDialogFragment

class EditLabelDialogBuilder(private val manager: FragmentManager) {

    fun show(type: LabelEditType, label: Label?) : DialogFragment {
        val dialog = when (type) {
            LabelEditType.DELETE -> {
                DeleteLabelDialogFragment.newInstance(label!!)
            }
            else -> EditLabelDialogFragment.newInstance(label)
        }.apply {
            show(manager, "EditLabelDialogFragment")
        }

        manager.executePendingTransactions()

        return dialog
    }
}
