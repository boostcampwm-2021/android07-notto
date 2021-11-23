package com.gojol.notto.ui.todo.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.gojol.notto.databinding.DialogBaseBinding
import com.gojol.notto.R
import com.gojol.notto.common.EventObserver
import com.gojol.notto.ui.todo.dialog.util.DialogViewModel

abstract class BaseDialog<B : ViewDataBinding, VM : DialogViewModel> : DialogFragment() {

    private lateinit var rootBinding: DialogBaseBinding

    protected lateinit var binding: B private set
    protected abstract val viewModel: VM
    protected abstract var layoutId: Int

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        rootBinding = DataBindingUtil.inflate(inflater, R.layout.dialog_base, container, false)
        rootBinding.lifecycleOwner = this
        rootBinding.viewModel = viewModel

        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        binding.lifecycleOwner = this
        rootBinding.clDialogContent.addView(binding.root)
        return rootBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initBaseObserver()
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window?.setBackgroundDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.bg_dialog))

        val deviceWidth = resources.displayMetrics.widthPixels
        val deviceHeight = resources.displayMetrics.heightPixels
        // 가로 모드
        if (deviceWidth > deviceHeight) {
            dialog.window?.attributes?.height = (resources.displayMetrics.heightPixels * 0.8).toInt()
        }
        return dialog
    }

    abstract fun initObserver()

    abstract fun confirmClick()

    abstract fun dismissClick()

    private fun initBaseObserver() {
        viewModel.isConfirmClicked.observe(this, EventObserver {
            if (it) {
                confirmClick()
            }
        })
        viewModel.isDismissClicked.observe(this, EventObserver {
            if (it) {
                dismissClick()
            }
        })
    }
}
