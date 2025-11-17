package com.dzian1s.dailyplanner.ui.tasklist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dzian1s.dailyplanner.core.AppGraph
import com.dzian1s.dailyplanner.domain.model.Task
import com.dzian1s.dailyplanner.domain.model.TaskId
import com.dzian1s.dailyplanner.domain.model.TaskStatus
import com.dzian1s.dailyplanner.domain.repository.TaskRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.*

class TaskListViewModel(
    // упрощённо берём репозиторий из AppGraph по умолчанию
    private val taskRepository: TaskRepository = AppGraph.taskRepository,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault()
) : ViewModel() {

    // выбранная дата (по умолчанию — сегодня)
    private val selectedDate = MutableStateFlow(currentDate())

    // внутренний mutable-state
    private val _state = MutableStateFlow(
        TaskListUiState(
            selectedDate = selectedDate.value,
            hours = emptyList(),
            isLoading = true,
            initialHourIndex = currentHour()
        )
    )
    val state: StateFlow<TaskListUiState> = _state.asStateFlow()

    init {
        observeTasks()
    }

    /**
     * Подписываемся на поток задач для выбранной даты.
     * Каждый раз, когда меняется дата или сами задачи в БД,
     * мы пересобираем UiState.
     */
    private fun observeTasks() {
        viewModelScope.launch {
            selectedDate
                .flatMapLatest { date ->
                    taskRepository.getTasksForDateStream(date)
                        .map { tasks -> date to tasks }
                }
                .collect { (date, tasks) ->
                    val nowInfo = currentDateTime()
                    val newState = buildUiState(
                        date = date,
                        tasks = tasks,
                        nowDateTime = nowInfo
                    )
                    _state.value = newState.copy(
                        // initialHourIndex задаём только при самом первом построении
                        initialHourIndex = _state.value.initialHourIndex
                    )
                }
        }
    }

    /**
     * Пользователь выбрал другую дату.
     * Мы просто обновляем selectedDate, а Flow сам переключится (flatMapLatest).
     */
    fun onDateSelected(date: LocalDate) {
        selectedDate.value = date
        // при смене даты логично сбросить initialHourIndex
        _state.update { it.copy(initialHourIndex = null) }
    }

    fun onTaskClick(id: TaskId) {
        // позже можно сделать переход на экран деталей/редактирования
    }

    fun onToggleTaskCompleted(taskId: TaskId) {
        // здесь можно будет вызвать use case / репозиторий
        // для изменения статуса задачи (Active <-> Completed)
        // пока оставим заглушку
    }

    // ---- вспомогательные функции ниже ----

    private fun buildUiState(
        date: LocalDate,
        tasks: List<Task>,
        nowDateTime: LocalDateTime
    ): TaskListUiState {
        val today = nowDateTime.date
        val currentHour = nowDateTime.time.hour

        // группируем задачи по часу
        val tasksByHour: Map<Int, List<TaskItemUi>> =
            tasks
                .filter { it.time != null }
                .groupBy { it.time!!.hour }
                .mapValues { (_, hourTasks) ->
                    hourTasks.map { it.toUiItem() }
                }

        val hours = (0..23).map { hour ->
            val label = "%02d:00".format(hour)
            val hourTasks = tasksByHour[hour] ?: emptyList()

            val isCurrentHour = (date == today && hour == currentHour)
            val isPast = (date < today) || (date == today && hour < currentHour)

            HourSlotUi(
                hour = hour,
                label = label,
                tasks = hourTasks,
                isCurrentHour = isCurrentHour,
                isPast = isPast
            )
        }

        return TaskListUiState(
            selectedDate = date,
            hours = hours,
            isLoading = false,
            errorMessage = null,
            initialHourIndex = _state.value.initialHourIndex ?: currentHour
        )
    }

    private fun Task.toUiItem(): TaskItemUi {
        val timeText = this.time?.let { "%02d:%02d".format(it.hour, it.minute) }

        val isCompleted = this.status is TaskStatus.Completed

        val color = this.category?.colorHex
            ?.removePrefix("#")
            ?.toLongOrNull(16)
            ?.let { 0xFF000000 or it }  // добавляем непрозрачный альфа-канал
            ?: 0xFF90CAF9  // какой-нибудь дефолтный ARGB (синий-ish)

        return TaskItemUi(
            id = this.id ?: TaskId(-1), // временно, лучше всегда иметь id
            title = this.title,
            timeText = timeText,
            colorArgb = color,
            isCompleted = isCompleted
        )
    }

    private fun currentDate(): LocalDate = currentDateTime().date

    private fun currentHour(): Int = currentDateTime().time.hour

    private fun currentDateTime(): LocalDateTime =
        Clock.System.now().toLocalDateTime(timeZone)
}
