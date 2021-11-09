package com.gojol.notto.ui.label

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivityEditLabelBinding
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
        }
    }

    override fun onResume() {
        super.onResume()

        binding.viewmodel?.loadLabels()
    }
}
