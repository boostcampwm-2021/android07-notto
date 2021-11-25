package com.gojol.notto.ui.todo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nottokeyword.KeywordDatabase
import com.gojol.notto.common.Event
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.common.TodoDeleteType
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.TodoAlarmManager
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TodoEditViewModel @Inject constructor(
    private val repository: TodoLabelRepository,
    private val keywordDatabase: KeywordDatabase,
    private val todoAlarmManager: TodoAlarmManager
) : ViewModel() {

    private val _isTodoEditing = MutableLiveData<Boolean>()
    val isTodoEditing: LiveData<Boolean> = _isTodoEditing

    private val _popLabelAddDialog = MutableLiveData<Boolean>()
    val popLabelAddDialog: LiveData<Boolean> = _popLabelAddDialog

    private val _isCloseButtonClicked = MutableLiveData<Event<Boolean>>()
    val isCloseButtonCLicked: LiveData<Event<Boolean>> = _isCloseButtonClicked

    private val _existedTodo = MutableLiveData<Todo>()
    val existedTodo: LiveData<Todo> = _existedTodo

    private val _date = MutableLiveData<LocalDate>()
    val date: LiveData<LocalDate> = _date

    private val _todoDeleteType = MutableLiveData<TodoDeleteType>()
    val todoDeleteType: LiveData<TodoDeleteType> = _todoDeleteType

    private val _isDeletionExecuted = MutableLiveData<Event<Boolean>>()
    val isDeletionExecuted: LiveData<Event<Boolean>> = _isDeletionExecuted

    private val _labelList = MutableLiveData<List<Label>>()
    val labelList: LiveData<List<Label>> = _labelList

    private val _selectedLabelList = MutableLiveData(listOf<Label>())
    val selectedLabelList: LiveData<List<Label>> = _selectedLabelList

    val todoContent = MutableLiveData("")

    val isRepeatChecked = MutableLiveData(false)

    private val _repeatType = MutableLiveData(RepeatType.YEAR)
    val repeatType: LiveData<RepeatType> = _repeatType

    private val _repeatTypeClick = MutableLiveData<Event<Boolean>>()
    val repeatTypeClick: LiveData<Event<Boolean>> = _repeatTypeClick

    private val _repeatStart = MutableLiveData<LocalDate>()
    val repeatStart: LiveData<LocalDate> = _repeatStart

    private val _repeatStartClick = MutableLiveData<Event<Boolean>>()
    val repeatStartClick: LiveData<Event<Boolean>> = _repeatStartClick

    val isTimeChecked = MutableLiveData(false)

    private val _timeStart = MutableLiveData<LocalTime>()
    val timeStart: LiveData<LocalTime> = _timeStart

    private val _timeStartClick = MutableLiveData<Event<Boolean>>()
    val timeStartClick: LiveData<Event<Boolean>> = _timeStartClick

    private val _timeFinish = MutableLiveData<LocalTime>()
    val timeFinish: LiveData<LocalTime> = _timeFinish

    private val _timeFinishClick = MutableLiveData<Event<Boolean>>()
    val timeFinishClick: LiveData<Event<Boolean>> = _timeFinishClick

    private val _timeRepeat = MutableLiveData(TimeRepeatType.MINUTE_5)
    val timeRepeat: LiveData<TimeRepeatType> = _timeRepeat

    private val _timeRepeatClick = MutableLiveData<Event<Boolean>>()
    val timeRepeatClick: LiveData<Event<Boolean>> = _timeRepeatClick

    val isKeywordChecked = MutableLiveData(false)

    private val _isSaveButtonEnabled = MutableLiveData<Boolean>()
    val isSaveButtonEnabled: LiveData<Boolean> = _isSaveButtonEnabled

    init {
        _repeatType.value = RepeatType.DAY
        _timeRepeat.value = TimeRepeatType.MINUTE_5
        _repeatStart.value = LocalDate.now()
        _timeStart.value = LocalTime.now()
        _timeFinish.value = LocalTime.now()
    }

    fun setDummyLabelData() {
        viewModelScope.launch {
            _labelList.value = repository.getAllLabel()
        }
    }

    fun addLabelToSelectedLabelList(labelName: String) {
        if (selectedLabelList.value?.find { it.name == labelName } != null) return

        val label = labelList.value?.find { it.name == labelName } ?: return
        val newLabelList = selectedLabelList.value?.toMutableList()?.apply {
            add(label)
        } ?: return
        _selectedLabelList.value = newLabelList
    }

    fun removeLabelFromSelectedLabelList(label: Label) {
        val newLabelList = selectedLabelList.value?.toMutableList()?.apply {
            remove(label)
        } ?: return
        _selectedLabelList.value = newLabelList
    }

    fun setupExistedTodo() {
        val todo = existedTodo.value ?: return
        viewModelScope.launch {
            _selectedLabelList.value = repository.getTodosWithLabels().find { todoWithLabel ->
                todo.todoId == todoWithLabel.todo.todoId
            }?.labels
        }

        todoContent.value = todo.content
        isRepeatChecked.value = todo.isRepeated
        _repeatType.value = todo.repeatType
        _repeatStart.value = todo.startDate
        isTimeChecked.value = todo.hasAlarm
        _timeStart.value = todo.startTime
        _timeFinish.value = todo.endTime
        _timeRepeat.value = todo.periodTime
        isKeywordChecked.value = todo.isKeywordOpen
    }

    fun updateIsTodoEditing(todo: Todo?) {
        todo?.let {
            _isTodoEditing.value = true
            _existedTodo.value = it
        } ?: run {
            _isTodoEditing.value = false
        }
    }

    fun updateDate(date: LocalDate?) {
        val selectedDate = date ?: return
        _date.value = selectedDate
    }

    fun updateIsCloseButtonClicked() {
        _isCloseButtonClicked.value = Event(true)
    }

    fun updateTodoDeleteType(type: TodoDeleteType?) {
        val deleteType = type ?: return
        _todoDeleteType.value = deleteType
    }

    fun updateTodoContent(content: String) {
        todoContent.value = content
    }

    fun onRepeatTypeClick() {
        _repeatTypeClick.value = Event(true)
    }

    fun updateRepeatType(repeatType: RepeatType) {
        _repeatType.value = repeatType
    }

    fun onRepeatStartClick() {
        _repeatStartClick.value = Event(true)
    }

    fun updateRepeatTime(repeatTime: LocalDate) {
        _repeatStart.value = repeatTime
    }

    fun onTimeStartClick() {
        _timeStartClick.value = Event(true)
    }

    fun updateTimeStart(timeStart: LocalTime) {
        _timeStart.value = timeStart
    }

    fun onTimeFinishClick() {
        _timeFinishClick.value = Event(true)
    }

    fun updateTimeFinish(timeFinish: LocalTime) {
        _timeFinish.value = timeFinish
    }

    fun onTimeRepeatClick() {
        _timeRepeatClick.value = Event(true)
    }

    fun updateTimeRepeat(timeRepeat: TimeRepeatType) {
        _timeRepeat.value = timeRepeat
    }

    fun updatePopLabelAddDialog() {
        _popLabelAddDialog.value = true
    }

    fun saveTodo() {
        if (todoContent.value.isNullOrEmpty()) {
            _isSaveButtonEnabled.value = false
            return
        }

        when (isTodoEditing.value) {
            true -> updateTodo()
            false -> saveNewTodo()
            else -> return
        }
    }

    private fun saveNewTodo() {
        val content = todoContent.value ?: return
        val isKeywordOpen = isKeywordChecked.value ?: return
        val newTodo = Todo(
            content,
            isRepeatChecked.value ?: return,
            repeatType.value ?: return,
            repeatStart.value ?: return,
            isTimeChecked.value ?: return,
            timeStart.value ?: return,
            timeFinish.value ?: return,
            timeRepeat.value ?: return,
            isKeywordOpen,
            false,
            FINISH_DATE ?: return
        )

        if (isKeywordOpen) {
            CoroutineScope(Dispatchers.IO).launch {
                val result = keywordDatabase.insertKeyword(content)

                Log.i(this.javaClass.name, "insertKeyword result: $result")
            }
        }

        viewModelScope.launch {
            launch {
                repository.insertTodo(newTodo)
                todoAlarmManager.addAlarm(repository.getAllTodo().last())
            }.join()

            val saveTodo = withContext(Dispatchers.Default) {
                repository.getTodosWithLabels().find { it.labels.isEmpty() }?.todo
            }

            saveTodo?.let { todo ->
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

                _isSaveButtonEnabled.value = true
            }
        }
    }

    private fun updateTodo() {
        val content = todoContent.value ?: return
        val isKeywordOpen = isKeywordChecked.value ?: return
        val newTodo = Todo(
            content,
            isRepeatChecked.value ?: return,
            repeatType.value ?: return,
            repeatStart.value ?: return,
            isTimeChecked.value ?: return,
            timeStart.value ?: return,
            timeFinish.value ?: return,
            timeRepeat.value ?: return,
            isKeywordOpen,
            false,
            FINISH_DATE ?: return,
            existedTodo.value?.todoId ?: return
        )

        if (isKeywordOpen) {
            val oldTodo = existedTodo.value ?: return

            if (oldTodo.isKeywordOpen.not() || oldTodo.content != content) {
                CoroutineScope(Dispatchers.IO).launch {
                    val result = keywordDatabase.insertKeyword(content)

                    Log.i(TAG, "insertKeyword result: $result")
                }
            }
        }

        viewModelScope.launch {
            repository.updateTodo(newTodo)
            todoAlarmManager.addAlarm(newTodo)

            selectedLabelList.value?.let { list ->
                val newList = list.filterNot { it.order == 0 }
                repository.updateTodo(newTodo, newList)
            }
            _isSaveButtonEnabled.value = true
        }
    }

    fun deleteTodo() {
        val todoId = existedTodo.value?.todoId ?: return
        val deleteType = todoDeleteType.value ?: return
        val selectedDate = date.value ?: return

        viewModelScope.launch {
            when (deleteType) {
                TodoDeleteType.TODAY -> repository.deleteTodayTodo(todoId, selectedDate)
                TodoDeleteType.TODAY_AND_FUTURE -> repository.deleteTodayAndFutureTodo(
                    todoId,
                    selectedDate
                )
            }
            _isDeletionExecuted.postValue(Event(true))
        }
    }

    companion object {
        val TAG = TodoEditViewModel::class.java.simpleName
        val FINISH_DATE: LocalDate? = LocalDate.of(2099, 12, 31)
    }
}
