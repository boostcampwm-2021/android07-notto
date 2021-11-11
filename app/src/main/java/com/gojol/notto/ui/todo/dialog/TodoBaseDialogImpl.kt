package com.gojol.notto.ui.todo.dialog

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


    fun setBinding(binding: ViewDataBinding) {
        this.binding = binding
    }

    fun setDialog(widthRate: Float, heightRate: Float) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_dialog))

        val deviceWidth = resources.displayMetrics.widthPixels
        val deviceHeight = resources.displayMetrics.heightPixels
        // 가로 모드
        if (deviceWidth > deviceHeight) {
            dialog.window?.attributes?.width = (resources.displayMetrics.widthPixels * widthRate).toInt()
            dialog.window?.attributes?.height = (resources.displayMetrics.heightPixels * heightRate).toInt()
        }

        show()
    }

    fun setDialog() {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.bg_dialog))

        val deviceWidth = resources.displayMetrics.widthPixels
        val deviceHeight = resources.displayMetrics.heightPixels
        // 가로 모드
        if (deviceWidth > deviceHeight) {
            dialog.window?.attributes?.height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        }

        show()
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