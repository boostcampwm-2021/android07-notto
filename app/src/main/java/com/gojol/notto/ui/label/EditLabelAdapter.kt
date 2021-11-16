package com.gojol.notto.ui.label

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.gojol.notto.common.DIALOG_LABEL_ITEM_KEY
import com.gojol.notto.databinding.ItemEditLabelBinding
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.ui.label.dialog.delete.DeleteLabelDialogFragment
import com.gojol.notto.ui.label.dialog.edit.EditLabelDialogFragment
import com.google.gson.Gson

class EditLabelAdapter(
    private val dialogCallback: (DialogFragment) -> Unit
) : RecyclerView.Adapter<EditLabelAdapter.EditLabelViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Label>() {

        override fun areItemsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem.labelId == newItem.labelId
        }

        override fun areContentsTheSame(oldItem: Label, newItem: Label): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditLabelViewHolder {
        return EditLabelViewHolder(
            ItemEditLabelBinding.inflate(LayoutInflater.from(parent.context), parent, false),
            dialogCallback
        )
    }

    override fun onBindViewHolder(holder: EditLabelViewHolder, position: Int) {
        holder.bind(differ.currentList[position])
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class EditLabelViewHolder(
        private val binding: ItemEditLabelBinding,
        private val dialogCallback: (DialogFragment) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Label) {
            val bundle = Bundle().apply {
                putString(DIALOG_LABEL_ITEM_KEY, Gson().toJson(item))
            }

            binding.apply {
                label = item

                setDeleteClickListener {
                    val dialog = DeleteLabelDialogFragment().apply {
                        arguments = bundle
                    }
                    dialogCallback(dialog)
                }

                setUpdateClickListener {
                    val dialog = EditLabelDialogFragment().apply {
                        arguments = bundle
                    }
                    dialogCallback(dialog)
                }

                executePendingBindings()
            }
        }
    }
}
