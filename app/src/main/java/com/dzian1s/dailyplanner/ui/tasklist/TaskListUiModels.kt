package com.dzian1s.dailyplanner.ui.tasklist

import com.dzian1s.dailyplanner.domain.model.TaskId
import kotlinx.datetime.LocalDate

/**
 * Одна задача в UI-слоте.
 * Это уже "подготовленный" вариант Task для отображения.
 */
data class TaskItemUi(
    val id: TaskId,
    val title: String,
    val timeText: String?,   // например "14:30", может быть null если без времени
    val colorArgb: Long,     // цвет задачи в формате ARGB (или 0xFF…)
    val isCompleted: Boolean
)

/**
 * Один час в "ленте дня".
 * Например: 14:00 и задачи, которые в этот час начинаются.
 */
data class HourSlotUi(
    val hour: Int,                 // 0..23
    val label: String,             // "06:00", "14:00"
    val tasks: List<TaskItemUi>,   // задачи в этот час
    val isCurrentHour: Boolean,    // true, если сейчас этот час
    val isPast: Boolean            // true, если час уже прошёл для выбранной даты
)

/**
 * Полное состояние экрана списка задач.
 * View рисует то, что здесь лежит.
 */
data class TaskListUiState(
    val selectedDate: LocalDate,
    val hours: List<HourSlotUi>,
    val isLoading: Boolean,
    val errorMessage: String? = null,
    val initialHourIndex: Int? = null   // с какого часа скроллить при первом открытии
)
