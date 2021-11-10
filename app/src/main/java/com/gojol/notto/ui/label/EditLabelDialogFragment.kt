package com.gojol.notto.ui.label

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.gojol.notto.R
import com.gojol.notto.common.EditType
import com.gojol.notto.databinding.DialogFragmentEditLabelBinding
import com.gojol.notto.model.database.label.Label
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditLabelDialogFragment(private val onOkayCallback: () -> Unit) : DialogFragment() {

    private lateinit var binding: DialogFragmentEditLabelBinding
    private lateinit var editType: EditType

    private var label: Label? = null

    private val viewModel: EditLabelDialogViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.dialog_fragment_edit_label,
            null,
            false
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val json = arguments?.getString("label")
        label = Gson().fromJson(json, Label::class.java)

        initEditType()
        initOnClickListeners()

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun initEditType() {
        editType = if (label == null) EditType.CREATE else EditType.UPDATE

        binding.apply {
             when (editType) {
                EditType.CREATE -> {
                    tvEditLabelDialogTitle.text = "라벨 추가"
                }
                EditType.UPDATE -> {
                    tvEditLabelDialogTitle.text = "라벨 수정"
                    etEditLabel.setText(label!!.name)
                }
            }
        }
    }

    private fun initOnClickListeners() {
        binding.tvEditLabelCancel.setOnClickListener {
            dialog?.cancel()
        }

        binding.tvEditLabelOkay.setOnClickListener {
            val labelName = binding.etEditLabel.text.toString()

            when (editType) {
                EditType.CREATE -> {
                    // TODO: Label order 기본값 설정
                    val newLabel = Label(1000, labelName)
                    viewModel.createLabel(newLabel, onOkayCallback)
                }
                EditType.UPDATE -> {
                    val newLabel = label!!.copy(name = labelName)
                    viewModel.updateLabel(newLabel, onOkayCallback)
                }
            }

            dialog?.cancel()
        }
    }
}
