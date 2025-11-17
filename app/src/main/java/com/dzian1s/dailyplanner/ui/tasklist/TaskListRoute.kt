package com.dzian1s.dailyplanner.ui.tasklist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun TaskListRoute(
    viewModel: TaskListViewModel = viewModel()
) {
    // Подписываемся на StateFlow из ViewModel
    val uiState by viewModel.state.collectAsState()

    // Состояние списка для управления скроллом
    val listState = rememberLazyListState()

    // Один раз при первом показе проскроллить к текущему часу
    LaunchedEffect(uiState.initialHourIndex) {
        val index = uiState.initialHourIndex
        if (index != null && index in uiState.hours.indices) {
            listState.scrollToItem(index)
        }
    }

    TaskListScreen(
        state = uiState,
        listState = listState,
        onDateSelected = viewModel::onDateSelected,
        onTaskClick = viewModel::onTaskClick,
        onAddTaskClick = { /* пока пусто, потом добавим навигацию */ },
        onToggleTaskCompleted = viewModel::onToggleTaskCompleted
    )
}
