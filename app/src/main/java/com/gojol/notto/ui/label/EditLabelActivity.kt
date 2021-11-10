package com.gojol.notto.ui.label

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivityEditLabelBinding
import com.gojol.notto.model.database.label.Label
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditLabelActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditLabelBinding
    private lateinit var editLabelAdapter: EditLabelAdapter

    private val editLabelViewModel: EditLabelViewModel by viewModels()
    private val itemTouchHelper by lazy {
        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
            0
        ) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                val adapter = recyclerView.adapter as EditLabelAdapter
                val from = viewHolder.bindingAdapterPosition
                val to = target.bindingAdapterPosition

                editLabelViewModel.moveItem(from, to)
                adapter.notifyItemMoved(from, to)

                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {}

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    viewHolder?.itemView?.alpha = 0.5f
                }
            }

            override fun clearView(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder
            ) {
                super.clearView(recyclerView, viewHolder)

                viewHolder.itemView.alpha = 1.0f
            }
        }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit_label)
        editLabelAdapter = EditLabelAdapter(::showDeleteDialog, ::showEditDialog)

        itemTouchHelper.attachToRecyclerView(binding.rvEditLabel)

        binding.apply {
            lifecycleOwner = this@EditLabelActivity
            viewmodel = editLabelViewModel

            rvEditLabel.layoutManager = LinearLayoutManager(this@EditLabelActivity)
            rvEditLabel.adapter = editLabelAdapter
        }

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

    private fun initObservers() {
        editLabelViewModel.dialogToShow.observe(this, {
            showEditDialog(it)
        })
    }

    private fun showDeleteDialog(label: Label?) {
        label ?: return

        val dialog = DeleteLabelDialogFragment(label)

        dialog.show(supportFragmentManager, "DeleteLabelDialogFragment")

        supportFragmentManager.executePendingTransactions()

        dialog.dialog?.apply {
            setOnShowListener {
                editLabelViewModel.updateLabels()
            }
            setOnDismissListener {
                editLabelViewModel.loadLabels()
            }
        }
    }

    private fun showEditDialog(label: Label?) {
        val dialog = EditLabelDialogFragment(label)

        dialog.show(supportFragmentManager, "EditLabelDialogFragment")

        supportFragmentManager.executePendingTransactions()

        dialog.dialog?.apply {
            setOnShowListener {
                editLabelViewModel.updateLabels()
            }
            setOnDismissListener {
                editLabelViewModel.loadLabels()
            }
        }
    }
}
