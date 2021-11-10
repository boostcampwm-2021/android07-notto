package com.gojol.notto.ui.home.dialog

import android.app.Dialog
import android.content.Context
import android.view.View
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.databinding.ViewDataBinding
import com.gojol.notto.R

open class TodoBaseDialogImpl(context: Context) : View(context), TodoBaseDialog {
    private var dialog: Dialog = Dialog(context)
    private lateinit var binding: ViewDataBinding

    init {
        show()
    }

    fun setBinding(binding: ViewDataBinding) {
        this.binding = binding
        setDialog()
    }

    private fun setDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_dialog))
    }

    final override fun show() {
        dialog.show()
    }

    override fun confirm() {
        dialog.dismiss()
    }

    override fun dismiss() {
        dialog.dismiss()
    }
}