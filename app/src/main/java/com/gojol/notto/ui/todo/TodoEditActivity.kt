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
import com.gojol.notto.databinding.ActivityTodoEditBinding
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.ui.todo.dialog.TodoAlarmPeriodDialog
import com.gojol.notto.ui.todo.dialog.TodoDeletionDialog
import com.gojol.notto.ui.todo.dialog.TodoRepeatTimeDialog
import com.gojol.notto.ui.todo.dialog.TodoRepeatTypeDialog
import com.gojol.notto.ui.todo.dialog.TodoSetTimeDialog
import com.gojol.notto.model.database.todo.Todo
import dagger.hilt.android.AndroidEntryPoint

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

    companion object {
        const val TIME_START = "timeStart"
        const val TIME_FINISH = "timeFinish"
        const val TIME_REPEAT = "timeRepeat"
        const val REPEAT_TYPE = "repeatType"
        const val REPEAT_TIME = "repeatTime"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_todo_edit)
        binding.lifecycleOwner = this
        binding.viewmodel = todoEditViewModel
//        savedInstanceState?.getBoolean(TIME_START)?.let { todoEditViewModel.restoreOnTimeStartState(it) }
//        savedInstanceState?.getBoolean(TIME_FINISH)?.let { todoEditViewModel.restoreOnTimeFinishState(it) }
//        savedInstanceState?.getBoolean(TIME_REPEAT)?.let { todoEditViewModel.restoreOnTimeRepeatState(it) }
//        savedInstanceState?.getBoolean(REPEAT_TIME)?.let { todoEditViewModel.restoreOnRepeatStartState(it) }
//        savedInstanceState?.getBoolean(REPEAT_TYPE)?.let { todoEditViewModel.restoreOnRepeatTypeState(it) }

        initIntentExtra()
        initAppbar()
        initSelectedLabelRecyclerView()
        initObserver()
        initEditTextListener()
        todoEditViewModel.setDummyLabelData()
    }

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
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
        return super.dispatchTouchEvent(ev)
    }

    private fun initIntentExtra() {
        val todo = intent.getSerializableExtra("todo") as Todo?
        todoEditViewModel.updateIsTodoEditing(todo)
    }

    private fun initAppbar() {
        binding.tbTodoEdit.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.delete_todo -> {
                    //TODO todo 삭제
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
        todoEditViewModel.labelList.observe(this) {
            val newList = it.filterNot { label -> label.order == 0 }
                .map { label -> label.name }.toTypedArray()
            initDialog(newList)
        }
        todoEditViewModel.selectedLabelList.observe(this) {
            val newList = it.filterNot { label -> label.order == 0}
            selectedLabelAdapter.submitList(newList)
        }
        todoEditViewModel.isSaveButtonEnabled.observe(this) {
            if (!it) showSaveButtonDisabled()
            else finish()
        }
        todoEditViewModel.popLabelAddDialog.observe(this) {
            if (it) showLabelAddDialog()
        }
        todoEditViewModel.repeatTypeClick.observe(this) {
            if (it) {
                todoRepeatTypeDialog.data = todoEditViewModel.repeatType.value
                todoRepeatTypeDialog.callback = todoEditViewModel::updateRepeatType
                todoRepeatTypeDialog.show()
            }
        }
        todoEditViewModel.repeatStartClick.observe(this) {
            if (it) {
                todoRepeatTimeDialog.data = todoEditViewModel.repeatStart.value
                todoRepeatTimeDialog.callback = todoEditViewModel::updateRepeatTime
                todoRepeatTimeDialog.show()
            }
        }
        todoEditViewModel.timeStartClick.observe(this) {
            if (it) {
                todoSetTimeDialog.data = todoEditViewModel.timeStart.value
                todoSetTimeDialog.callback = todoEditViewModel::updateTimeStart
                todoSetTimeDialog.show()
            }
        }
        todoEditViewModel.timeFinishClick.observe(this) {
            if (it) {
                todoSetTimeDialog.data = todoEditViewModel.timeFinish.value
                todoSetTimeDialog.callback = todoEditViewModel::updateTimeFinish
                todoSetTimeDialog.show()
            }
        }
        todoEditViewModel.timeRepeatClick.observe(this) {
            if (it) {
                todoAlarmPeriodDialog.data = todoEditViewModel.timeRepeat.value
                todoAlarmPeriodDialog.callback = todoEditViewModel::updateTimeRepeat
                todoAlarmPeriodDialog.show()
            }
        }
    }


//    override fun onSaveInstanceState(outState: Bundle) {
//        super.onSaveInstanceState(outState)
//
//        todoEditViewModel.timeStartClick.value?.let { bool ->
//            outState.putBoolean(TIME_START, bool)
//        }
//        todoEditViewModel.timeFinishClick.value?.let { bool ->
//            outState.putBoolean(TIME_FINISH, bool)
//        }
//        todoEditViewModel.timeRepeatClick.value?.let { bool ->
//            outState.putBoolean(TIME_REPEAT, bool)
//        }
//        todoEditViewModel.repeatTypeClick.value?.let { bool ->
//            outState.putBoolean(REPEAT_TYPE, bool)
//        }
//        todoEditViewModel.repeatStartClick.value?.let { bool ->
//            outState.putBoolean(REPEAT_TIME, bool)
//        }
//    }

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

    private fun initDialog(items: Array<String>) {
        labelAddDialog =
            AlertDialog.Builder(this).setTitle(getString(R.string.todo_edit_label_select_sentence))
                .setItems(items) { _, which ->
                    todoEditViewModel.addLabelToSelectedLabelList(items[which])
                }
        todoDeletionDialog = TodoDeletionDialog(this)
        todoRepeatTypeDialog = TodoRepeatTypeDialog(this)
        todoRepeatTimeDialog = TodoRepeatTimeDialog(this)
        todoAlarmPeriodDialog = TodoAlarmPeriodDialog(this)
        todoSetTimeDialog = TodoSetTimeDialog(this)
    }

    private fun initEditTextListener() {
        binding.etTodoEdit.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                todoEditViewModel.updateTodoContent(p0.toString())
            }
        })
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
