package com.gojol.notto.ui.todo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.databinding.ActivityTodoEditBinding

class TodoEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityTodoEditBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_edit)

        initAppbar()
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
}
