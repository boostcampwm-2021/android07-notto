package com.gojol.notto.ui.label

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivityEditLabelBinding
import com.gojol.notto.model.database.label.Label
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
            rvEditLabel.adapter = EditLabelAdapter(::showDialog)
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

    private fun showDialog(label: Label?) {
        CreateOrEditLabelFragment().show(supportFragmentManager, label?.name)
    }
}
