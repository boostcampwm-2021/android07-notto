package com.gojol.notto.ui.todo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nottokeyword.KeywordDatabase
import com.gojol.notto.common.Event
import com.gojol.notto.common.LABEL_ADD
import com.gojol.notto.common.RepeatType
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.common.TodoDeleteType
import com.gojol.notto.model.data.todo.ClickWrapper
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.TodoAlarmManager
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TodoEditViewModel @Inject constructor(
    private val repository: TodoLabelRepository,
    private val keywordDatabase: KeywordDatabase,
    private val todoAlarmManager: TodoAlarmManager
) : ViewModel() {

    private val _todo = MutableLiveData<Todo>()
    val todo: LiveData<Todo> = _todo

    private var _existedTodo = MutableLiveData<Todo>()
    val existedTodo: LiveData<Todo> = _existedTodo

    private var _selectedDate = MutableLiveData(LocalDate.now())
    val selectedDate: LiveData<LocalDate> = _selectedDate

    private var todoDeleteType: TodoDeleteType? = null

    private val _labelList = MutableLiveData<List<Label>>()
    val labelList: LiveData<List<Label>> = _labelList

    private val _selectedLabelList = MutableLiveData(listOf<Label>())
    val selectedLabelList: LiveData<List<Label>> = _selectedLabelList

    val clickWrapper = ClickWrapper.getClickWrapper()

    init {
        _todo.value = Todo(
            "",
            false,
            RepeatType.DAY,
            LocalDate.now(),
            false,
            LocalTime.now(),
            LocalTime.now(),
            TimeRepeatType.MINUTE_5,
            false,
            isFinished = false,
            FINISH_DATE ?: LocalDate.now()
        )
    }

    fun fillContentWithKeyword(keyword: String) {
        _todo.value = todo.value?.copy(content = keyword)
    }

    fun initLabelData() {
        viewModelScope.launch {
            _labelList.value = repository.getAllLabel().sortedBy { it.order }
        }
    }

    fun addLabelToSelectedLabelList(labelName: String) {
        if (labelName == LABEL_ADD) {
            onLabelAddClick()
            return
        }
        if (selectedLabelList.value?.find { it.name == labelName } != null) return

        val label = labelList.value?.find { it.name == labelName } ?: return
        val newLabelList = selectedLabelList.value?.plus(label) ?: return
        _selectedLabelList.value = newLabelList
    }

    fun removeLabelFromSelectedLabelList(label: Label) {
        val newLabelList = selectedLabelList.value?.minus(label) ?: return
        _selectedLabelList.value = newLabelList
    }

    private fun setupExistedTodo(existedTodo: Todo) {
        viewModelScope.launch {
            _selectedLabelList.value = repository.getTodosWithLabels().find { todoWithLabel ->
                existedTodo.todoId == todoWithLabel.todo.todoId
            }?.labels
        }

        _todo.value = existedTodo
    }

    fun updateIsTodoEditing(todo: Todo?) {
        todo?.let { existedTodo ->
            clickWrapper.isTodoEditing.value = true
            _existedTodo.value = existedTodo
            setupExistedTodo(existedTodo)
        } ?: run {
            clickWrapper.isTodoEditing.value = false
        }
    }

    fun updateTodoDeleteType(type: TodoDeleteType?) {
        val deleteType = type ?: return
        todoDeleteType = deleteType
        deleteTodo()
    }

    fun updateTodoContent(s: CharSequence, start: Int, before: Int, count: Int) {
        _todo.value = todo.value?.copy(content = s.toString())
    }

    fun updateDate(date: LocalDate?) {
        val isEditing = clickWrapper.isTodoEditing.value ?: return
        var selectedDate = date ?: return

        if (isEditing) {
            if (selectedDate.isBefore(LocalDate.now())) {
                clickWrapper.isBeforeToday.value = isEditing
            }
        } else {
            if (selectedDate.isBefore(LocalDate.now())) {
                selectedDate = LocalDate.now()
                clickWrapper.isBeforeToday.value = isEditing
            }
        }

        _selectedDate.value = selectedDate
    }

    fun updateIsRepeatChecked(isChecked: Boolean) {
        _todo.value = todo.value?.copy(isRepeated = isChecked)
    }

    fun updateRepeatType(repeatType: RepeatType) {
        _todo.value = todo.value?.copy(repeatType = repeatType)
    }

    fun updateStartDate(date: LocalDate?) {
        val startDate = date ?: return
        _todo.value = todo.value?.copy(startDate = startDate)
    }

    fun updateIsTimeChecked(isChecked: Boolean) {
        _todo.value = todo.value?.copy(hasAlarm = isChecked)
    }

    fun updateTimeStart(timeStart: LocalTime) {
        _todo.value = todo.value?.copy(startTime = timeStart)
    }

    fun updateTimeFinish(timeFinish: LocalTime) {
        _todo.value = todo.value?.copy(endTime = timeFinish)
    }

    fun updateTimeRepeat(timeRepeat: TimeRepeatType) {
        _todo.value = todo.value?.copy(periodTime = timeRepeat)
    }

    fun updateIsKeywordChecked(isChecked: Boolean) {
        _todo.value = todo.value?.copy(isKeywordOpen = isChecked)
    }

    private fun onLabelAddClick() {
        clickWrapper.labelAddClicked.value = Event(Unit)
    }

    fun onSaveButtonClick() {
        val isTodoContentNotEmpty = todo.value?.content?.isNotEmpty() ?: false
        val isEditing = clickWrapper.isTodoEditing.value ?: return
        clickWrapper.isSaveButtonClicked.value = Pair(isTodoContentNotEmpty, isEditing)
    }

    fun saveTodo() {
        if (todo.value?.content.isNullOrEmpty()) {
            clickWrapper.isSaveButtonEnabled.value = false
            return
        }

        when (clickWrapper.isTodoEditing.value) {
            true -> updateTodo()
            false -> saveNewTodo()
            else -> return
        }
    }

    private fun saveNewTodo() {
        val todoModel = todo.value ?: return
        val date = selectedDate.value ?: return

        if (todoModel.isKeywordOpen) {
            insertKeywords(todoModel.content)
        }

        viewModelScope.launch {
            // 투두 insert, 투두 알림 설정
            val generatedTodoId =
                repository.insertTodo(todoModel, date).toInt()
            todoAlarmManager.addAlarm(repository.getAllTodo().last())
            _todo.value = todoModel.copy(todoId = generatedTodoId)

            insertLabels()
        }
    }

    private suspend fun insertLabels() {
        _todo.value?.let { todo ->
            // 전체 라벨에 투두 넣기
            _labelList.value?.find { it.order == 0 }?.let {
                repository.insertTodo(todo, it)
            }

            // 선택된 라벨에 투두 넣기
            _selectedLabelList.value?.let { labels ->
                labels.forEach { label ->
                    repository.insertTodo(todo, label)
                }
            }

            clickWrapper.isSaveButtonEnabled.value = true
        }
    }

    private fun updateTodo() {
        val todoModel = todo.value ?: return
        val date = selectedDate.value ?: return

        if (todoModel.isKeywordOpen) {
            val oldTodo = existedTodo.value ?: return

            if (oldTodo.isKeywordOpen.not() || oldTodo.content != todoModel.content) {
                insertKeywords(todoModel.content)
            }
        }

        viewModelScope.launch {
            repository.updateTodo(todoModel, date)
            todoAlarmManager.updateAlarms()

            selectedLabelList.value?.let { list ->
                val newList = list.filterNot { it.order == 0 }
                repository.updateTodo(todoModel, newList)
            }
            clickWrapper.isSaveButtonEnabled.value = true
        }
    }

    private fun insertKeywords(content: String) {
        CoroutineScope(Dispatchers.IO).launch {
            val result = keywordDatabase.insertKeyword(content)

            Log.i(TAG, "insertKeyword result: $result")
        }
    }

    private fun deleteTodo() {
        val todoId = existedTodo.value?.todoId ?: return
        val date = selectedDate.value ?: return
        val deleteType = todoDeleteType ?: return

        viewModelScope.launch {
            when (deleteType) {
                TodoDeleteType.SELECTED -> repository.deleteSelectedTodo(
                    todoId,
                    date
                )
                TodoDeleteType.SELECTED_AND_FUTURE -> repository.deleteSelectedAndFutureTodo(
                    todoId,
                    date
                )
            }
            todoAlarmManager.deleteAlarms()
            clickWrapper.isDeletionExecuted.value = Event(Unit)
        }
    }

    companion object {
        val TAG: String = TodoEditViewModel::class.java.simpleName
        val FINISH_DATE: LocalDate? = LocalDate.of(2099, 12, 31)
    }
}
