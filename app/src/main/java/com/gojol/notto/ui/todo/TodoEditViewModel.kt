package com.gojol.notto.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.nottokeyword.FirebaseDB
import com.gojol.notto.BuildConfig
import com.gojol.notto.common.Event
import com.gojol.notto.common.TimeRepeatType
import com.gojol.notto.common.TodoDeleteType
import com.gojol.notto.common.RepeatType
import com.gojol.notto.model.data.todo.ClickWrapper
import com.gojol.notto.model.data.todo.TodoWrapper
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.datasource.todo.TodoAlarmManager
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class TodoEditViewModel @Inject constructor(
    private val repository: TodoLabelRepository,
    private val todoAlarmManager: TodoAlarmManager
) : ViewModel() {

    private val _todoWrapper = MutableLiveData<TodoWrapper>()
    val todoWrapper: LiveData<TodoWrapper> = _todoWrapper

    private val _labelList = MutableLiveData<List<Label>>()
    val labelList: LiveData<List<Label>> = _labelList

    private val _selectedLabelList = MutableLiveData(listOf<Label>())
    val selectedLabelList: LiveData<List<Label>> = _selectedLabelList

    val clickWrapper = ClickWrapper(
        MutableLiveData(), MutableLiveData(), MutableLiveData(), MutableLiveData(), MutableLiveData(),
        MutableLiveData(), MutableLiveData(), MutableLiveData(), MutableLiveData(), MutableLiveData()
    )

    private val firebaseDB = FirebaseDB(BuildConfig.FIREBASE_DB_URL)

    init {
        _todoWrapper.value = TodoWrapper(
            Todo(
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
            ),
            null, LocalDate.now(), null
        )
    }

    fun setDummyLabelData() {
        viewModelScope.launch {
            _labelList.value = repository.getAllLabel()
        }
    }

    fun addLabelToSelectedLabelList(labelName: String) {
        if (selectedLabelList.value?.find { it.name == labelName } != null) return

        val label = labelList.value?.find { it.name == labelName } ?: return
        val newLabelList = selectedLabelList.value?.plus(label) ?: return
        _selectedLabelList.value = newLabelList
    }

    fun removeLabelFromSelectedLabelList(label: Label) {
        val newLabelList = selectedLabelList.value?.minus(label) ?: return
        _selectedLabelList.value = newLabelList
    }

    fun setupExistedTodo() {
        val todo = todoWrapper.value?.existedTodo ?: return
        viewModelScope.launch {
            _selectedLabelList.value = repository.getTodosWithLabels().find { todoWithLabel ->
                todo.todoId == todoWithLabel.todo.todoId
            }?.labels
        }

        _todoWrapper.value = todoWrapper.value?.copy(todo = todo)
    }

    fun updateIsTodoEditing(todo: Todo?) {
        todo?.let { existedTodo ->
            clickWrapper.isTodoEditing.value = true
            _todoWrapper.value = todoWrapper.value?.copy(existedTodo = existedTodo)
        } ?: run {
            clickWrapper.isTodoEditing.value = false
        }
    }

    fun updateDate(date: LocalDate?) {
        val selectedDate = date ?: return
        _todoWrapper.value = todoWrapper.value?.copy(selectedDate = selectedDate)
    }

    fun updateStartDate(date: LocalDate?) {
        val startDate = date ?: return
        val todo = todoWrapper.value?.todo ?: return
        _todoWrapper.value = todoWrapper.value?.copy(todo = todo.copy(startDate = startDate))
    }

    fun updateIsCloseButtonClicked() {
        clickWrapper.isCloseButtonCLicked.value = Event(Unit)
    }

    fun updateTodoDeleteType(type: TodoDeleteType?) {
        val deleteType = type ?: return
        val todoModel = todoWrapper.value ?: return
        _todoWrapper.value = todoModel.copy(todoDeleteType = deleteType)
        deleteTodo()
    }

    fun onRepeatTypeClick() {
        clickWrapper.repeatTypeClick.value = Event(true)
    }

    fun updateRepeatType(repeatType: RepeatType) {
        val todo = todoWrapper.value?.todo ?: return
        _todoWrapper.value = todoWrapper.value?.copy(todo = todo.copy(repeatType = repeatType))
    }

    fun onRepeatStartClick() {
        clickWrapper.repeatStartClick.value = Event(true)
    }

    fun onTimeStartClick() {
        clickWrapper.timeStartClick.value = Event(true)
    }

    fun updateTimeStart(timeStart: LocalTime) {
        val todo = todoWrapper.value?.todo ?: return
        _todoWrapper.value = todoWrapper.value?.copy(todo = todo.copy(startTime = timeStart))
    }

    fun onTimeFinishClick() {
        clickWrapper.timeFinishClick.value = Event(true)
    }

    fun updateTimeFinish(timeFinish: LocalTime) {
        val todo = todoWrapper.value?.todo ?: return
        _todoWrapper.value = todoWrapper.value?.copy(todo = todo.copy(endTime = timeFinish))
    }

    fun onTimeRepeatClick() {
        clickWrapper.timeRepeatClick.value = Event(true)
    }

    fun updateTimeRepeat(timeRepeat: TimeRepeatType) {
        val todo = todoWrapper.value?.todo ?: return
        _todoWrapper.value = todoWrapper.value?.copy(todo = todo.copy(periodTime = timeRepeat))
    }

    fun updatePopLabelAddDialog() {
        clickWrapper.popLabelAddDialog.value = true
    }

    fun saveTodo() {
        todoWrapper.value?.todo?.content ?: run {
            clickWrapper.isSaveButtonEnabled.value = false
            return
        }

        when (clickWrapper.isTodoEditing.value) {
            true -> updateTodo()
            false -> saveNewTodo()
            else -> return
        }
    }

    fun updateTodoContent(content: String) {
        val todo = todoWrapper.value?.todo ?: return
        _todoWrapper.value = todoWrapper.value?.copy(todo = todo.copy(content = content))
    }

    private fun saveNewTodo() {
        val todoModel = todoWrapper.value ?: return

        insertInFirebase(todoModel)

        viewModelScope.launch {
            // 투두 insert, 투두 알림 설정
            val generatedTodoId =
                repository.insertTodo(todoModel.todo, todoModel.selectedDate).toInt()
            todoAlarmManager.addAlarm(repository.getAllTodo().last())
            _todoWrapper.value = todoModel.copy(todo = todoModel.todo.copy(todoId = generatedTodoId))

            insertLabels()
        }
    }

    private fun insertInFirebase(todoModel: TodoWrapper) {
        if (todoModel.todo.isKeywordOpen) {
            firebaseDB.insertKeyword(todoModel.todo.content)
        }
    }

    private suspend fun insertLabels() {
        _todoWrapper.value?.todo?.let { todo ->
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

    fun updateIsRepeatChecked(isChecked: Boolean) {
        val todo = todoWrapper.value?.todo ?: return
        _todoWrapper.value = todoWrapper.value?.copy(todo = todo.copy(isRepeated = isChecked))
    }

    fun updateIsTimeChecked(isChecked: Boolean) {
        val todo = todoWrapper.value?.todo ?: return
        _todoWrapper.value = todoWrapper.value?.copy(todo = todo.copy(hasAlarm = isChecked))
    }

    fun updateIsKeywordChecked(isChecked: Boolean) {
        val todo = todoWrapper.value?.todo ?: return
        _todoWrapper.value = todoWrapper.value?.copy(todo = todo.copy(isKeywordOpen = isChecked))
    }

    private fun updateTodo() {
        val todoModel = todoWrapper.value ?: return

        if (todoModel.todo.isKeywordOpen) {
            val oldTodo = todoModel.existedTodo ?: return

            if (oldTodo.isKeywordOpen.not() || oldTodo.content != todoModel.todo.content) {
                firebaseDB.insertKeyword(todoModel.todo.content)
            }
        }

        viewModelScope.launch {
            repository.updateTodo(todoModel.todo, todoModel.selectedDate)
            todoAlarmManager.addAlarm(todoModel.todo)

            selectedLabelList.value?.let { list ->
                val newList = list.filterNot { it.order == 0 }
                repository.updateTodo(todoModel.todo, newList)
            }
            clickWrapper.isSaveButtonEnabled.value = true
        }
    }

    private fun deleteTodo() {
        val todoModel = todoWrapper.value ?: return
        val todoId = todoModel.existedTodo?.todoId ?: return
        val deleteType = todoModel.todoDeleteType ?: return

        viewModelScope.launch {
            when (deleteType) {
                TodoDeleteType.SELECTED -> repository.deleteSelectedTodo(
                    todoId,
                    todoModel.selectedDate
                )
                TodoDeleteType.SELECTED_AND_FUTURE -> repository.deleteSelectedAndFutureTodo(
                    todoId,
                    todoModel.selectedDate
                )
            }
            clickWrapper.isDeletionExecuted.value = Event(Unit)
        }
    }

    companion object {
        val FINISH_DATE: LocalDate? = LocalDate.of(2099, 12, 31)
    }
}
