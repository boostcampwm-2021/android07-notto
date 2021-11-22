package com.gojol.notto.ui.todo.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.gojol.notto.databinding.DialogBaseBinding
import dagger.hilt.android.AndroidEntryPoint
import com.gojol.notto.R
import com.gojol.notto.common.EventObserver
import com.gojol.notto.ui.todo.dialog.util.TodoBaseDialog
import com.gojol.notto.ui.todo.dialog.util.TodoBaseDialogViewModel


@AndroidEntryPoint
open class TodoBaseDialogImpl : DialogFragment(), TodoBaseDialog {

    var fragment: Fragment? = null

    lateinit var binding: DialogBaseBinding
    val viewModel: TodoBaseDialogViewModel by viewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.dialog_base, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel
        initObserver()
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

    override fun initObserver() {
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

    override fun confirmClick() {
        this.dismiss()
    }

    override fun dismissClick() {
        this.dismiss()
    }
}
