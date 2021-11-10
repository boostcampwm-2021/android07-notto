package com.gojol.notto.ui.label

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
import com.gojol.notto.databinding.DialogFragmentEditLabelBinding
import com.gojol.notto.model.database.label.Label
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditLabelDialogFragment : DialogFragment() {

    private lateinit var binding: DialogFragmentEditLabelBinding

    private val viewModel: EditLabelDialogViewModel by viewModels()

    private var label: Label? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_edit_label,
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

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val json = arguments?.getString("label")
        label = Gson().fromJson(json, Label::class.java)
        viewModel.setEditType(label)

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
            if (it == true) {
                dialog?.cancel()
            }
        })
    }
}
