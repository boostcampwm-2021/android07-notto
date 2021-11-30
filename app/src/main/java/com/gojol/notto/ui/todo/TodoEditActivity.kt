package com.gojol.notto.ui.todo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import com.gojol.notto.R
import com.gojol.notto.common.DELETE
import com.gojol.notto.common.EventObserver
import com.gojol.notto.common.LABEL_ADD
import com.gojol.notto.common.LabelEditType
import com.gojol.notto.common.POPULAR_KEYWORD
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
import com.gojol.notto.util.EditLabelDialogBuilder
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class TodoEditActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTodoEditBinding
    private val todoEditViewModel: TodoEditViewModel by viewModels()
    private lateinit var selectedLabelAdapter: SelectedLabelAdapter

    private lateinit var labelAddDialog: AlertDialog.Builder
    private lateinit var saveButtonDialog: AlertDialog.Builder
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
        initSelectedLabelRecyclerView()
        initObserver()
        initTodoDialog()
        initEditTextTouchListener()
    }

    override fun onResume() {
        super.onResume()
        todoEditViewModel.initLabelData()
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
        val date = intent.getSerializableExtra("date") as LocalDate?
        val todo = intent.getSerializableExtra("todo") as Todo?
        val keyword = intent.getStringExtra(POPULAR_KEYWORD)

        todoEditViewModel.updateDate(date)
        todoEditViewModel.updateIsTodoEditing(todo)

        if (keyword != null) todoEditViewModel.fillContentWithKeyword(keyword)
    }

    private fun initSelectedLabelRecyclerView() {
        selectedLabelAdapter = SelectedLabelAdapter(::setRecyclerViewCallback)
        binding.rvTodoEdit.adapter = selectedLabelAdapter
    }

    private fun setRecyclerViewCallback(label: Label) {
        if (todoEditViewModel.clickWrapper.isBeforeToday.value == true) return
        todoEditViewModel.removeLabelFromSelectedLabelList(label)
    }

    private fun initObserver() {
        todoEditViewModel.clickWrapper.isTodoEditing.observe(this) {
            if (it) {
                todoEditViewModel.setupExistedTodo()
                binding.tbTodoEdit.title = getString(R.string.todo_edit_title_edit)
            } else {
                binding.btnTodoEditDelete.visibility = View.GONE
            }
        }
        todoEditViewModel.clickWrapper.isBeforeToday.observe(this, {
            if (it) { //편집일 때
                binding.tbTodoEdit.title = getString(R.string.todo_edit_title_detail)
                blockClick()
            } else showBeforeTodayDisabled()
        })
        todoEditViewModel.clickWrapper.isCloseButtonCLicked.observe(this, EventObserver {
            finish()
        })
        todoEditViewModel.clickWrapper.deleteButtonClick.observe(this, EventObserver {
            todoDeletionDialog.show(supportFragmentManager, DELETE)
        })
        todoEditViewModel.clickWrapper.isDeletionExecuted.observe(this, EventObserver {
            finish()
            Toast.makeText(
                this,
                getString(R.string.todo_edit_delete_message),
                Toast.LENGTH_LONG
            ).show()
        })
        todoEditViewModel.labelList.observe(this) {
            val newList = it.filterNot { label -> label.order == 0 }
                .map { label -> label.name }
                .plus(LABEL_ADD)
                .toTypedArray()
            initDialog(newList)
        }
        todoEditViewModel.selectedLabelList.observe(this) {
            val newList = it.filterNot { label -> label.order == 0 }
            selectedLabelAdapter.submitList(newList)
        }
        todoEditViewModel.clickWrapper.isSaveButtonEnabled.observe(this) {
            if (!it) showSaveButtonDisabled()
            else finish()
        }
        todoEditViewModel.clickWrapper.popLabelAddDialog.observe(this, EventObserver {
            if (it) showLabelAddDialog()
        })
        todoEditViewModel.clickWrapper.labelAddClicked.observe(this, EventObserver {
            onLabelAddClick()
        })
        todoEditViewModel.clickWrapper.repeatTypeClick.observe(this, EventObserver {
            if (it) todoRepeatTypeDialog.show(supportFragmentManager, REPEAT_TYPE)
        })
        todoEditViewModel.clickWrapper.repeatStartClick.observe(this, EventObserver {
            if (it) todoRepeatTimeDialog.show(supportFragmentManager, REPEAT_TIME)
        })
        todoEditViewModel.clickWrapper.timeStartClick.observe(this, EventObserver {
            if (it) todoTimeStartDialog.show(supportFragmentManager, SET_TIME_START)
        })
        todoEditViewModel.clickWrapper.timeFinishClick.observe(this, EventObserver {
            if (it) todoTimeFinishDialog.show(supportFragmentManager, SET_TIME_FINISH)
        })
        todoEditViewModel.clickWrapper.timeRepeatClick.observe(this, EventObserver {
            if (it) todoAlarmPeriodDialog.show(supportFragmentManager, TIME_REPEAT)
        })
        todoEditViewModel.clickWrapper.isSaveButtonClicked.observe(this, {
            if (it.first.not()) { // 투두 내용이 없는 경우
                showSaveButtonDisabled()
                return@observe
            }
            if (it.second) showSaveButtonDialog() // 투두 편집인 경우
            else todoEditViewModel.saveTodo() //투두 생성인 경우
        })
    }

    private fun initDialog(items: Array<String>) {
        labelAddDialog =
            AlertDialog.Builder(this)
                .setTitle(getString(R.string.todo_edit_label_select_sentence))
                .setItems(items) { _, which ->
                    todoEditViewModel.addLabelToSelectedLabelList(items[which])
                }
        saveButtonDialog =
            AlertDialog.Builder(this, R.style.AlertDialogTheme)
                .setTitle(getString(R.string.todo_edit_save_button_title))
                .setMessage(getString(R.string.todo_edit_save_button_msg))
                .setPositiveButton(getString(R.string.okay)) { _, _ ->
                    todoEditViewModel.saveTodo()
                }
                .setNegativeButton(getString(R.string.cancel)) { _, _ -> }
    }

    private fun initTodoDialog() {
        todoDeletionDialog = DeletionDialog.newInstance(todoEditViewModel::updateTodoDeleteType)

        todoRepeatTypeDialog = RepeatTypeDialog.newInstance(
            bundleOf(REPEAT_TYPE_DATA to todoEditViewModel.todoWrapper.value?.todo?.repeatType),
            todoEditViewModel::updateRepeatType
        )

        todoRepeatTimeDialog = RepeatTimeDialog.newInstance(
            bundleOf(REPEAT_TIME_DATA to todoEditViewModel.todoWrapper.value?.todo?.startDate),
            todoEditViewModel::updateStartDate
        )

        todoAlarmPeriodDialog = AlarmPeriodDialog.newInstance(
            bundleOf(TIME_REPEAT_DATA to todoEditViewModel.todoWrapper.value?.todo?.periodTime),
            todoEditViewModel::updateTimeRepeat
        )

        todoTimeStartDialog = TimeStartDialog.newInstance(
            bundleOf(SET_TIME_DATA to todoEditViewModel.todoWrapper.value?.todo?.startTime),
            todoEditViewModel::updateTimeStart
        )

        todoTimeFinishDialog = TimeFinishDialog.newInstance(
            bundleOf(SET_TIME_DATA to todoEditViewModel.todoWrapper.value?.todo?.endTime),
            todoEditViewModel::updateTimeFinish
        )
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initEditTextTouchListener() {
        binding.etTodoEdit.setOnTouchListener { view, motionEvent ->
            if (view == binding.etTodoEdit) {
                view.parent.requestDisallowInterceptTouchEvent(true)
                when (motionEvent.action and MotionEvent.ACTION_MASK) {
                    MotionEvent.ACTION_UP -> view.parent.requestDisallowInterceptTouchEvent(false)
                }
            }
            false
        }
    }

    private fun showLabelAddDialog() {
        labelAddDialog
            .create()
            .window?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dialog))
        labelAddDialog.show()
    }

    private fun showSaveButtonDialog() {
        saveButtonDialog
            .create()
            .window?.setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg_dialog))
        saveButtonDialog.show()
    }

    private fun showSaveButtonDisabled() {
        Toast.makeText(
            this,
            getString(R.string.todo_edit_button_disabled_message),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun showBeforeTodayDisabled() {
        Toast.makeText(this, getString(R.string.todo_edit_before_today_msg), Toast.LENGTH_LONG).show()
    }

    private fun onLabelAddClick() {
        val fragmentManager = this.supportFragmentManager
        val dialog = EditLabelDialogBuilder.builder(LabelEditType.CREATE, null).apply {
            show(fragmentManager, "EditLabelDialogFragment")
        }

        fragmentManager.executePendingTransactions()

        dialog.dialog?.apply {
            setOnDismissListener {
                todoEditViewModel.initLabelData()
                dialog.dismiss()
            }
        }
    }

    private fun blockClick() {
        with(binding) {
            etTodoEdit.isEnabled = false
            btnTodoEditLabel.isClickable = false
            rvTodoEdit.isClickable = false
            tvTodoEditRepeatStartValue.isClickable = false
            tvTodoEditRepeatTypeValue.isClickable = false
            tvTodoEditTimeStartValue.isClickable = false
            tvTodoEditTimeFinishValue.isClickable = false
            tvTodoEditTimeRepeatValue.isClickable = false
            switchTodoEditRepeat.isEnabled = false
            switchTodoEditTime.isEnabled = false
            switchTodoEditKeyword.isEnabled = false
            btnTodoEditSave.visibility = View.GONE
        }
    }
}
