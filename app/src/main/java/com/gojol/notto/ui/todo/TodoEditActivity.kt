package com.gojol.notto.ui.todo

import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.viewModels
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
    private lateinit var spinnerAdapter: ArrayAdapter<String>
    private lateinit var selectedLabelAdapter: SelectedLabelAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_edit)
        binding.lifecycleOwner = this
        binding.viewmodel = todoEditViewModel

        initAppbar()
        initSelectedLabelRecyclerView()
        initObserver()
        initSpinnerHandler()
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
            val newList = it.map { label -> label.name }.toMutableList()
            newList.add(0, "선택")
            spinnerAdapter =
                ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, newList)
            binding.spinnerTodoEdit.adapter = spinnerAdapter
        }

        todoEditViewModel.selectedLabelList.observe(this) {
            selectedLabelAdapter.submitList(it)
        }
    }

    private fun initSpinnerHandler() {
        binding.spinnerTodoEdit.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    todoEditViewModel.addLabelToSelectedLabelList(
                        binding.spinnerTodoEdit.getItemAtPosition(
                            position
                        ).toString()
                    )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    println("onNothingSelected")
                }
            }
    }
}
