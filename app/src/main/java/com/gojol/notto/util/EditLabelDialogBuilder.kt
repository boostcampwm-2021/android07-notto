package com.gojol.notto.util

import androidx.fragment.app.DialogFragment
import com.gojol.notto.common.LabelEditType
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.ui.label.dialog.delete.DeleteLabelDialogFragment
import com.gojol.notto.ui.label.dialog.edit.EditLabelDialogFragment

object EditLabelDialogBuilder {

    fun builder(type: LabelEditType, label: Label?): DialogFragment {
        return when (type) {
            LabelEditType.DELETE -> {
                DeleteLabelDialogFragment.newInstance(label!!)
            }
            else -> EditLabelDialogFragment.newInstance(label)
        }
    }
}
