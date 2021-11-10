package com.gojol.notto.ui.label

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivityEditLabelBinding
import com.gojol.notto.model.database.label.Label
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditLabelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditLabelBinding

    private val editLabelViewModel: EditLabelViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_label)
        binding.apply {
            lifecycleOwner = this@EditLabelActivity
            viewmodel = editLabelViewModel
            rvEditLabel.layoutManager = LinearLayoutManager(this@EditLabelActivity)
            rvEditLabel.adapter = EditLabelAdapter(::deleteLabel, ::showDialog)
        }

        initObservers()
    }

    override fun onResume() {
        super.onResume()

        binding.viewmodel?.loadLabels()
    }

    private fun initObservers() {
        editLabelViewModel.dialogToShow.observe(this, {
            showDialog(it)
        })
    }

    private fun deleteLabel(label: Label?) {
        // TODO: 삭제 확인 Dialog 구현
        if (label != null) {
            editLabelViewModel.deleteLabel(label)
        }
    }

    private fun showDialog(label: Label?) {
        val json = Gson().toJson(label)
        val bundle = Bundle().apply { putString("label", json) }
        val dialog = EditLabelDialogFragment(::onOkayCallback)

        dialog.arguments = bundle
        dialog.show(supportFragmentManager, "EditLabelDialogFragment")
    }

    private fun onOkayCallback() {
        editLabelViewModel.loadLabels()
    }
}
