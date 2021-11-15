package com.gojol.notto.ui.label.dialog.delete

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.databinding.DialogFragmentDeleteLabelBinding
import com.gojol.notto.model.database.label.Label
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DeleteLabelDialogFragment(private val label: Label) : DialogFragment() {

    private lateinit var binding: DialogFragmentDeleteLabelBinding

    private val viewModel: DeleteLabelDialogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_delete_label,
            null,
            false
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        initViewModel()
        initObservers()

        viewModel.setLabel(label)

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun initViewModel() {
        binding.viewmodel = viewModel
    }

    private fun initObservers() {
        viewModel.close.observe(viewLifecycleOwner, {
            if (it) {
                dialog?.cancel()
            }
        })
    }
}
