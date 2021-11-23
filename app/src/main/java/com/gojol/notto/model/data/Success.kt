package com.gojol.notto.model.data

import com.gojol.notto.common.TodoState
import com.gojol.notto.model.database.todo.DailyTodo

class Success(
    private val dailyTodos: List<DailyTodo>
) {
    enum class Level(val value: Int, val maxValue: Float) {
        ZERO(0, 0f), ONE(1, 0.25f), TWO(2, 0.5f),
        THREE(3, 0.75f), FOUR(4, 1f), FIVE(5, 1f);

        fun toIntAlpha(): Int {
            return 51 * value
        }
    }

    fun getSuccessLevel(): Level {
        val successCount = dailyTodos.count { it.todoState == TodoState.SUCCESS }
        val totalCount = dailyTodos.size

        val successRate = if (totalCount == 0) {
            Level.ZERO.maxValue
        } else {
            successCount.toFloat() / totalCount.toFloat()
        }

        val successLevel = when {
            totalCount == Level.ZERO.value || successCount == Level.ZERO.value -> Level.ZERO
            successRate <= Level.ONE.maxValue -> Level.ONE
            successRate <= Level.TWO.maxValue -> Level.TWO
            successRate <= Level.THREE.maxValue -> Level.THREE
            successRate < Level.FOUR.maxValue -> Level.FOUR
            else -> Level.FIVE
        }

        return successLevel
    }
}
