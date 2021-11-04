package com.gojol.notto.ui.home

import android.content.Context
import androidx.room.Room
import com.gojol.notto.model.data.RepeatType
import com.gojol.notto.model.database.TodoLabelDatabase
import com.gojol.notto.model.database.label.Label
import com.gojol.notto.model.database.todo.Todo
import com.gojol.notto.model.database.todolabel.TodoLabelCrossRef

class Dummy(context: Context) {

    private val todoDao = Room.databaseBuilder(
        context.applicationContext,
        TodoLabelDatabase::class.java,
        "notto-database"
    ).build().todoLabelDao()

    var dummyLabels = listOf(
        Label(1, "동아리", 2),
        Label(2, "집", 3),
        Label(3, "회사", 4),
        Label(4, "운동", 5),
        Label(5, "평일", 6),
        Label(6, "주말1", 7),
        Label(7, "주말2", 8),
        Label(8, "주말3", 9),
    )

    private val dummyTodos = listOf(
        Todo(false, "11안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 1),
        Todo(false, "22안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 2),
        Todo(false, "33안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 3),
        Todo(false, "44안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 4),
        Todo(false, "55안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 5),
        Todo(false, "66안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 6),
        Todo(false, "77안녕하세요", "1", false, RepeatType.DAY, false, "1:00", "2:00", "1:00", false, 7),
    )

    private val dummyRef = listOf(
        TodoLabelCrossRef(dummyTodos[0].todoId, dummyLabels[1].labelId),
        TodoLabelCrossRef(dummyTodos[0].todoId, dummyLabels[2].labelId),
        TodoLabelCrossRef(dummyTodos[1].todoId, dummyLabels[1].labelId),
        TodoLabelCrossRef(dummyTodos[2].todoId, dummyLabels[1].labelId),
        TodoLabelCrossRef(dummyTodos[4].todoId, dummyLabels[4].labelId),
        TodoLabelCrossRef(dummyTodos[4].todoId, dummyLabels[5].labelId),
        TodoLabelCrossRef(dummyTodos[4].todoId, dummyLabels[6].labelId),
    )

//    init {
//        CoroutineScope(Dispatchers.IO).launch {
//            dummyLabels.forEach {
//                todoDao.insertLabel(it)
//            }
//            dummyTodos.forEach {
//                todoDao.insertTodo(it)
//            }
//            dummyRef.forEach {
//                todoDao.insert(it)
//            }
//        }
//    }

    suspend fun getLabel(): List<Label> {
        return todoDao.getAllLabel()
    }
}
