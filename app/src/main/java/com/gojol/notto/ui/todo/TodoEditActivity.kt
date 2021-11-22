package com.gojol.notto.ui.todo

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.common.DELETE
import com.gojol.notto.common.EventObserver
import com.gojol.notto.databinding.ActivityTodoEditBinding
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.ui.todo.dialog.REPEAT_TIME
import com.gojol.notto.ui.todo.dialog.REPEAT_TIME_DATA
import com.gojol.notto.ui.todo.dialog.REPEAT_TYPE
import com.gojol.notto.ui.todo.dialog.REPEAT_TYPE_DATA
import com.gojol.notto.ui.todo.dialog.TIME_FINISH
import com.gojol.notto.ui.todo.dialog.TIME_FINISH_DATE
import com.gojol.notto.ui.todo.dialog.TIME_REPEAT
import com.gojol.notto.ui.todo.dialog.TIME_REPEAT_DATA
import com.gojol.notto.ui.todo.dialog.TIME_START
import com.gojol.notto.ui.todo.dialog.TIME_START_DATE
import com.gojol.notto.ui.todo.dialog.TodoAlarmPeriodDialog
import com.gojol.notto.ui.todo.dialog.TodoDeletionDialog
import com.gojol.notto.ui.todo.dialog.TodoRepeatTimeDialog
import com.gojol.notto.ui.todo.dialog.TodoRepeatTypeDialog
import com.gojol.notto.ui.todo.dialog.TodoSetTimeDialog
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

@AndroidEntryPoint
class TodoEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoEditBinding
    private val todoEditViewModel: TodoEditViewModel by viewModels()
    private lateinit var selectedLabelAdapter: SelectedLabelAdapter

    private lateinit var labelAddDialog: AlertDialog.Builder
    private lateinit var todoDeletionDialog: TodoDeletionDialog
    private lateinit var todoRepeatTypeDialog: TodoRepeatTypeDialog
    private lateinit var todoRepeatTimeDialog: TodoRepeatTimeDialog
    private lateinit var todoSetTimeDialog: TodoSetTimeDialog
    private lateinit var todoAlarmPeriodDialog: TodoAlarmPeriodDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_edit)
        binding.lifecycleOwner = this
        binding.viewmodel = todoEditViewModel

        initIntentExtra()
        initAppbar()
        initSelectedLabelRecyclerView()
        initObserver()
        initTodoDialog()
        todoEditViewModel.setDummyLabelData()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        hideKeyboardWhenOutsideTouched(ev)
        return super.dispatchTouchEvent(ev)
    }

    private fun hideKeyboardWhenOutsideTouched(ev: MotionEvent) {
        val view = currentFocus
        if (view != null && (ev.action == MotionEvent.ACTION_UP || ev.action == MotionEvent.ACTION_MOVE) &&
            view is EditText && !view.javaClass.name.startsWith("android.webkit.")
        ) {
            val scrcoords = IntArray(2)
            view.getLocationOnScreen(scrcoords)
            val x = ev.rawX + view.getLeft() - scrcoords[0]
            val y = ev.rawY + view.getTop() - scrcoords[1]
            if (x < view.getLeft() || x > view.getRight() || y < view.getTop() || y > view.getBottom())
                (this.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(this.window.decorView.applicationWindowToken, 0)
        }
    }

    private fun initIntentExtra() {
        val todo = intent.getSerializableExtra("todo") as Todo?
        val date = intent.getSerializableExtra("date") as Calendar?
        todoEditViewModel.updateIsTodoEditing(todo)
        todoEditViewModel.updateDate(date)
    }

    private fun initAppbar() {
        binding.tbTodoEdit.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_todo -> {
                    // TODO: 새로 생성하는 경우면 ??
                    if (todoEditViewModel.isTodoEditing.value == true) {
                        TodoDeletionDialog.deleteTodoCallback =
                            todoEditViewModel::updateTodoDeleteType
                        todoDeletionDialog.show(supportFragmentManager, DELETE)
                    }
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
        todoEditViewModel.isTodoEditing.observe(this) {
            if (it) {
                todoEditViewModel.setupExistedTodo()
                binding.tbTodoEdit.title = getString(R.string.todo_edit_title_edit)
            }
        }
        todoEditViewModel.isCloseButtonCLicked.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                finish()
            }
        }
        todoEditViewModel.todoDeleteType.observe(this) {
            todoEditViewModel.deleteTodo()
        }
        todoEditViewModel.isDeletionExecuted.observe(this) { event ->
            event.getContentIfNotHandled()?.let {
                if (it) {
                    finish()
                    Toast.makeText(
                        this,
                        getString(R.string.todo_edit_delete_message),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
        todoEditViewModel.labelList.observe(this) {
            val newList = it.filterNot { label -> label.order == 0 }
                .map { label -> label.name }.toTypedArray()
            initDialog(newList)
        }
        todoEditViewModel.selectedLabelList.observe(this) {
            val newList = it.filterNot { label -> label.order == 0 }
            selectedLabelAdapter.submitList(newList)
        }
        todoEditViewModel.isSaveButtonEnabled.observe(this) {
            if (!it) showSaveButtonDisabled()
            else finish()
        }
        todoEditViewModel.popLabelAddDialog.observe(this) {
            if (it) showLabelAddDialog()
        }
        todoEditViewModel.repeatTypeClick.observe(this, EventObserver {
            if (it) {
                val bundle = Bundle()
                bundle.putSerializable(REPEAT_TYPE_DATA, todoEditViewModel.repeatType.value)
                TodoRepeatTypeDialog.callback = todoEditViewModel::updateRepeatType
                todoRepeatTypeDialog.arguments = bundle
                todoRepeatTypeDialog.show(supportFragmentManager, REPEAT_TYPE)
            }
        })
        todoEditViewModel.repeatStartClick.observe(this, EventObserver {
            if (it) {
                val bundle = Bundle()
                bundle.putString(REPEAT_TIME_DATA, todoEditViewModel.repeatStart.value)
                TodoRepeatTimeDialog.callback = todoEditViewModel::updateRepeatTime
                todoRepeatTimeDialog.arguments = bundle
                todoRepeatTimeDialog.show(supportFragmentManager, REPEAT_TIME)
            }
        })

        todoEditViewModel.timeStartClick.observe(this, EventObserver {
            if (it) {
                val bundle = Bundle()
                bundle.putString(TIME_START_DATE, todoEditViewModel.timeStart.value)
                TodoSetTimeDialog.callback = todoEditViewModel::updateTimeStart
                TodoSetTimeDialog.currentState = TIME_START
                todoSetTimeDialog.arguments = bundle
                todoSetTimeDialog.show(supportFragmentManager, TIME_START)
            }
        })

        todoEditViewModel.timeFinishClick.observe(this, EventObserver {
            if (it) {
                val bundle = Bundle()
                bundle.putString(TIME_FINISH_DATE, todoEditViewModel.timeFinish.value)
                TodoSetTimeDialog.callback = todoEditViewModel::updateTimeFinish
                TodoSetTimeDialog.currentState = TIME_FINISH
                todoSetTimeDialog.arguments = bundle
                todoSetTimeDialog.show(supportFragmentManager, TIME_FINISH)
            }
        })

        todoEditViewModel.timeRepeatClick.observe(this, EventObserver {
            if (it) {
                val bundle = Bundle()
                bundle.putSerializable(TIME_REPEAT_DATA, todoEditViewModel.timeRepeat.value)
                TodoAlarmPeriodDialog.callback = todoEditViewModel::updateTimeRepeat
                todoAlarmPeriodDialog.arguments = bundle
                todoAlarmPeriodDialog.show(supportFragmentManager, TIME_REPEAT)
            }
        })
    }

    private fun initDialog(items: Array<String>) {
        labelAddDialog =
            AlertDialog.Builder(this).setTitle(getString(R.string.todo_edit_label_select_sentence))
                .setItems(items) { _, which ->
                    todoEditViewModel.addLabelToSelectedLabelList(items[which])
                }
    }

    private fun initTodoDialog() {
        todoDeletionDialog = TodoDeletionDialog()
        todoRepeatTypeDialog = TodoRepeatTypeDialog()
        todoRepeatTimeDialog = TodoRepeatTimeDialog()
        todoAlarmPeriodDialog = TodoAlarmPeriodDialog()
        todoSetTimeDialog = TodoSetTimeDialog()
    }

    private fun showLabelAddDialog() {
        labelAddDialog.show()
    }

    private fun showSaveButtonDisabled() {
        Toast.makeText(
            this,
            getString(R.string.todo_edit_button_disabled_message),
            Toast.LENGTH_LONG
        ).show()
    }
}
