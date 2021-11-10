package com.gojol.notto.ui.label

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogFragmentDeleteLabelBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteLabelDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentDeleteLabelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_delete_label,
            null,
            false
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
