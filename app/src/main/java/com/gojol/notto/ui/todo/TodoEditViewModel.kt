package com.gojol.notto.ui.todo

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.datasource.todo.FakeTodoLabelRepository
import com.gojol.notto.model.datasource.todo.TodoLabelRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TodoEditViewModel @Inject constructor(private val repository: TodoLabelRepository) :
    ViewModel() {

    private val fakeRepository = FakeTodoLabelRepository()

    private val _labelList = MutableLiveData<List<Label>>()
    val labelList: LiveData<List<Label>> = _labelList
}
