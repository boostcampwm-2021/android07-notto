package com.gojol.notto.ui.todo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivityTodoEditBinding
import com.gojol.notto.model.database.label.Label
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoEditBinding
    private val todoEditViewModel: TodoEditViewModel by viewModels()
    private lateinit var selectedLabelAdapter: SelectedLabelAdapter
    private lateinit var labelAddDialog: AlertDialog.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_edit)
        binding.lifecycleOwner = this
        binding.viewmodel = todoEditViewModel

        initAppbar()
        initSelectedLabelRecyclerView()
        initObserver()
        initSwitchListener()
        initButtonListener()
        initEditTextListener()
        todoEditViewModel.setDummyLabelData()
    }

    private fun initAppbar() {
        binding.tbTodoEdit.setNavigationOnClickListener {
            finish()
        }

        binding.tbTodoEdit.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_todo -> {
                    //TODO todo 삭제
                    println("delete todo")
                    true
                }
                else -> false
            }
        }
    }

    private fun initSelectedLabelRecyclerView() {
        selectedLabelAdapter = SelectedLabelAdapter(::setRecyclerViewCallback)
        binding.rvTodoEdit.adapter = selectedLabelAdapter
    }

    private fun setRecyclerViewCallback(label: Label) {
        todoEditViewModel.removeLabelFromSelectedLabelList(label)
    }

    private fun initObserver() {
        todoEditViewModel.labelList.observe(this) {
            val newList = it.map { label -> label.name }.toTypedArray()
            initLabelAddDialog(newList)
        }
        todoEditViewModel.selectedLabelList.observe(this) {
            selectedLabelAdapter.submitList(it)
        }
        todoEditViewModel.isSaveButtonEnabled.observe(this) {
            if (!it) showSaveButtonDisabled()
            else finish()
        }
    }

    private fun initSwitchListener() {
        binding.switchTodoEditRepeat.setOnCheckedChangeListener { _, isChecked ->
            todoEditViewModel.updateIsRepeatChecked(isChecked)
        }
        binding.switchTodoEditTime.setOnCheckedChangeListener { _, isChecked ->
            todoEditViewModel.updateIsTimeChecked(isChecked)
        }
        binding.switchTodoEditKeyword.setOnCheckedChangeListener { _, isChecked ->
            todoEditViewModel.updateIsKeywordChecked(isChecked)
        }
    }

    private fun initButtonListener() {
        binding.btnTodoEditLabel.setOnClickListener {
            labelAddDialog.show()
        }
        binding.btnTodoEditSave.setOnClickListener {
            todoEditViewModel.saveTodo()
        }
    }

    private fun initLabelAddDialog(items: Array<String>) {
        labelAddDialog =
            AlertDialog.Builder(this).setTitle(getString(R.string.todo_edit_label_select_sentence))
                .setItems(items) { _, which ->
                    todoEditViewModel.addLabelToSelectedLabelList(items[which])
                }
    }

    private fun initEditTextListener() {
        binding.etTodoEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                todoEditViewModel.updateTodoContent(p0.toString())
            }

        })
    }

    private fun showSaveButtonDisabled() {
        Toast.makeText(this, "부족한 항목을 채워주세요!", Toast.LENGTH_LONG).show()
    }
}
