package com.dzian1s.dailyplanner.ui.tasklist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.Color
import com.dzian1s.dailyplanner.domain.model.TaskId
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    state: TaskListUiState,
    listState: LazyListState,
    onDateSelected: (LocalDate) -> Unit,
    onTaskClick: (TaskId) -> Unit,
    onAddTaskClick: () -> Unit,
    onToggleTaskCompleted: (TaskId) -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = formatDate(state.selectedDate),
                        style = MaterialTheme.typography.titleLarge
                    )
                },
                // потом можно добавить кнопки навигации по датам
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddTaskClick) {
                Text("+")
            }
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (state.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            if (state.errorMessage != null) {
                Text(
                    text = state.errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Лента дня по часам
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState
            ) {
                items(state.hours) { hourSlot ->
                    HourSlotRow(
                        slot = hourSlot,
                        onTaskClick = onTaskClick,
                        onToggleTaskCompleted = onToggleTaskCompleted
                    )
                }
            }
        }
    }
}

/**
 * Одна строка "час + задачи".
 */
@Composable
private fun HourSlotRow(
    slot: HourSlotUi,
    onTaskClick: (TaskId) -> Unit,
    onToggleTaskCompleted: (TaskId) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        verticalAlignment = Alignment.Top
    ) {
        // Левая колонка — метка часа
        Text(
            text = slot.label,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontWeight = if (slot.isCurrentHour) FontWeight.Bold else FontWeight.Normal
            ),
            modifier = Modifier
                .width(56.dp)
                .padding(end = 4.dp),
            color = when {
                slot.isCurrentHour -> MaterialTheme.colorScheme.primary
                slot.isPast -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.onSurface
            }
        )

        // Правая часть — список задач в этом часе
        if (slot.tasks.isEmpty()) {
            // Пустой слот — можно нарисовать тонкую линию или ничего
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp)
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                    )
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                slot.tasks.forEach { task ->
                    TaskRow(
                        task = task,
                        onClick = { onTaskClick(task.id) },
                        onToggleCompleted = { onToggleTaskCompleted(task.id) }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}

/**
 * Одна задачка в слоте.
 */
@Composable
private fun TaskRow(
    task: TaskItemUi,
    onClick: () -> Unit,
    onToggleCompleted: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(task.colorArgb.toInt()).copy(alpha = if (task.isCompleted) 0.4f else 1f),
                shape = MaterialTheme.shapes.medium
            )
            .clickable(onClick = onClick)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = task.isCompleted,
            onCheckedChange = { onToggleCompleted() }
        )

        Column(
            modifier = Modifier.padding(start = 8.dp)
        ) {
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (task.isCompleted) FontWeight.Normal else FontWeight.SemiBold
            )
            if (task.timeText != null) {
                Text(
                    text = task.timeText,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

/**
 * Простое форматирование даты вида "Сегодня, 26 января 2025".
 * Пока можно сделать что-то простое, потом при желании оформить по-локальному.
 */
private fun formatDate(date: LocalDate): String {
    // Можно по-простому: YYYY-MM-DD или потом сделать локализованно
    return date.toString()
}
