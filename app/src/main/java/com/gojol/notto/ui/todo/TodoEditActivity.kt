package com.gojol.notto.ui.todo

import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.common.DELETE
import com.gojol.notto.common.EventObserver
import com.gojol.notto.common.REPEAT_TIME
import com.gojol.notto.common.REPEAT_TIME_DATA
import com.gojol.notto.common.REPEAT_TYPE
import com.gojol.notto.common.REPEAT_TYPE_DATA
import com.gojol.notto.common.SET_TIME_DATA
import com.gojol.notto.common.SET_TIME_FINISH
import com.gojol.notto.common.SET_TIME_START
import com.gojol.notto.common.TIME_REPEAT
import com.gojol.notto.common.TIME_REPEAT_DATA
import com.gojol.notto.databinding.ActivityTodoEditBinding
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.ui.todo.dialog.AlarmPeriodDialog
import com.gojol.notto.ui.todo.dialog.DeletionDialog
import com.gojol.notto.ui.todo.dialog.RepeatTimeDialog
import com.gojol.notto.ui.todo.dialog.RepeatTypeDialog
import com.gojol.notto.ui.todo.dialog.TimeFinishDialog
import com.gojol.notto.ui.todo.dialog.TimeStartDialog
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class TodoEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoEditBinding
    private val todoEditViewModel: TodoEditViewModel by viewModels()
    private lateinit var selectedLabelAdapter: SelectedLabelAdapter

    private lateinit var labelAddDialog: AlertDialog.Builder
    private lateinit var todoDeletionDialog: DeletionDialog
    private lateinit var todoRepeatTypeDialog: RepeatTypeDialog
    private lateinit var todoRepeatTimeDialog: RepeatTimeDialog
    private lateinit var todoTimeStartDialog: TimeStartDialog
    private lateinit var todoTimeFinishDialog: TimeFinishDialog
    private lateinit var todoAlarmPeriodDialog: AlarmPeriodDialog

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
        val date = intent.getSerializableExtra("date") as LocalDate?
        todoEditViewModel.updateIsTodoEditing(todo)
        todoEditViewModel.updateDate(date)
    }

    private fun initAppbar() {
        binding.tbTodoEdit.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_todo -> {
                    // TODO: 새로 생성하는 경우면 ??
                    if (todoEditViewModel.isTodoEditing.value == true) {
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
            if (it) todoEditViewModel.setupExistedTodo()
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
            if (it) todoRepeatTypeDialog.show(supportFragmentManager, REPEAT_TYPE)
        })

        todoEditViewModel.repeatStartClick.observe(this, EventObserver {
            if (it) todoRepeatTimeDialog.show(supportFragmentManager, REPEAT_TIME)
        })

        todoEditViewModel.timeStartClick.observe(this, EventObserver {
            if (it) todoTimeStartDialog.show(supportFragmentManager, SET_TIME_START)
        })

        todoEditViewModel.timeFinishClick.observe(this, EventObserver {
            if (it) todoTimeFinishDialog.show(supportFragmentManager, SET_TIME_FINISH)
        })

        todoEditViewModel.timeRepeatClick.observe(this, EventObserver {
            if (it) todoAlarmPeriodDialog.show(supportFragmentManager, TIME_REPEAT)
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
        todoDeletionDialog = DeletionDialog.newInstance(todoEditViewModel::updateTodoDeleteType)

        todoRepeatTypeDialog = RepeatTypeDialog.newInstance(
            bundleOf(REPEAT_TYPE_DATA to todoEditViewModel.repeatType.value),
            todoEditViewModel::updateRepeatType
        )

        todoRepeatTimeDialog = RepeatTimeDialog.newInstance(
            bundleOf(REPEAT_TIME_DATA to todoEditViewModel.repeatStart.value),
            todoEditViewModel::updateRepeatTime
        )

        todoAlarmPeriodDialog = AlarmPeriodDialog.newInstance(
            bundleOf(TIME_REPEAT_DATA to todoEditViewModel.timeRepeat.value),
            todoEditViewModel::updateTimeRepeat
        )

        todoTimeStartDialog = TimeStartDialog.newInstance(
            bundleOf(SET_TIME_DATA to todoEditViewModel.timeStart.value),
            todoEditViewModel::updateTimeStart
        )

        todoTimeFinishDialog = TimeFinishDialog.newInstance(
            bundleOf(SET_TIME_DATA to todoEditViewModel.timeFinish.value),
            todoEditViewModel::updateTimeFinish
        )
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
