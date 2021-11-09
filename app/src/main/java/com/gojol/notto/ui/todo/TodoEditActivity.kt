package com.gojol.notto.ui.todo

import android.os.Bundle
import android.widget.ArrayAdapter
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivityTodoEditBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TodoEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoEditBinding
    private val todoEditViewModel: TodoEditViewModel by viewModels()
    private lateinit var spinnerAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_edit)
        binding.lifecycleOwner = this
        binding.viewmodel = todoEditViewModel

        initAppbar()
        initObserver()
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

    private fun initObserver() {
        todoEditViewModel.labelList.observe(this) {
            val newList = it.map { label -> label.name }.toMutableList()
            newList.add(0, "선택")
            spinnerAdapter =
                ArrayAdapter<String>(this, R.layout.support_simple_spinner_dropdown_item, newList)
            binding.spinnerTodoEdit.adapter = spinnerAdapter
        }
    }
}
