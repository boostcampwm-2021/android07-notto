package com.gojol.notto.ui.label

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.gojol.notto.R
import com.gojol.notto.databinding.FragmentCreateOrEditLabelBinding

class CreateOrEditLabelFragment : DialogFragment() {

    lateinit var binding: FragmentCreateOrEditLabelBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.inflate(
            LayoutInflater.from(context),
            R.layout.fragment_create_or_edit_label,
            null,
            false
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        initTitle()
        initOnClickListeners()

        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setView(binding.root)
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun initTitle() {
        val labelName = this.tag

        binding.apply {
            tvEditLabelDialogTitle.text = if (labelName == null) "라벨 추가" else "라벨 수정"
            etEditLabel.setText(labelName)
        }
    }

    private fun initOnClickListeners() {
        binding.tvEditLabelCancel.setOnClickListener {
            dialog?.cancel()
        }

        binding.tvEditLabelOkay.setOnClickListener {
            // TODO: DB에 라벨 삽입
            dialog?.cancel()
        }
    }
}
