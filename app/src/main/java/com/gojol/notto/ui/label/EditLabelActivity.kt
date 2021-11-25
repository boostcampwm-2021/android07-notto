package com.gojol.notto.ui.label

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.ItemTouchHelper
import com.gojol.notto.R
import com.gojol.notto.common.LabelEditType
import com.gojol.notto.databinding.ActivityEditLabelBinding
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.ui.label.util.ItemTouchCallback
import com.gojol.notto.util.EditLabelDialogBuilder
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditLabelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditLabelBinding
    private lateinit var editLabelAdapter: EditLabelAdapter

    private val editLabelViewModel: EditLabelViewModel by viewModels()
    private val itemTouchHelper = ItemTouchHelper(ItemTouchCallback(::moveItem, ::onClearView))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_label)
        editLabelAdapter = EditLabelAdapter(::showDialog)

        itemTouchHelper.attachToRecyclerView(binding.rvEditLabel)

        binding.apply {
            lifecycleOwner = this@EditLabelActivity
            rvEditLabel.adapter = editLabelAdapter
        }

        initToolbar()
        initObservers()
    }

    override fun onResume() {
        super.onResume()

        editLabelViewModel.loadLabels()
    }

    override fun onPause() {
        super.onPause()

        editLabelViewModel.updateLabels()
    }

    private fun moveItem(from: Int, to: Int) {
        editLabelViewModel.moveItem(from, to)
    }

    private fun onClearView() {
        binding.rvEditLabel.itemAnimator = null
        editLabelAdapter.differ.submitList(editLabelViewModel.updatedItems.value)
    }

    private fun initToolbar() {
        binding.toolbarEditLabel.setNavigationOnClickListener {
            finish()
        }

        binding.toolbarEditLabel.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit_label_menu_create -> {
                    showDialog(LabelEditType.CREATE, null)
                    true
                }
                else -> {
                    false
                }
            }
        }
    }

    private fun initObservers() {
        editLabelViewModel.items.observe(this, {
            editLabelAdapter.differ.submitList(it)
        })

        editLabelViewModel.close.observe(this, {
            if (it) {
                finish()
            }
        })
    }

    private fun showDialog(type: LabelEditType, label: Label?) {
        val dialog = EditLabelDialogBuilder.builder(type, label).apply {
            show(supportFragmentManager, "EditLabelDialogFragment")
        }

        supportFragmentManager.executePendingTransactions()

        dialog.dialog?.apply {
            setOnShowListener {
                editLabelViewModel.updateLabels()
            }
            setOnDismissListener {
                binding.rvEditLabel.itemAnimator = DefaultItemAnimator()
                editLabelViewModel.loadLabels()
                dialog.dismiss()
            }
        }
    }
}
