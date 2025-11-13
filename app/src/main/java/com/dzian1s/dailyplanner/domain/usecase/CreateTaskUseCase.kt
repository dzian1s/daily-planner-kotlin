package com.dzian1s.dailyplanner.domain.usecase

import com.dzian1s.dailyplanner.domain.model.*
import com.dzian1s.dailyplanner.domain.repository.TaskRepository

class CreateTaskUseCase(
    private val repository: TaskRepository,
    private val clock: () -> Long
) {

    suspend operator fun invoke(params: Params): TaskId {
        val now = clock()

        val task = Task(
            title = params.title,
            description = params.description,
            date = params.date,
            time = params.time,
            priority = params.priority,
            repeatPattern = params.repeatPattern,
            category = params.category,
            createdAt = now
        )

        return repository.upsertTask(task)
    }

    data class Params(
        val title: String,
        val description: String?,
        val date: kotlinx.datetime.LocalDate,
        val time: kotlinx.datetime.LocalTime?,
        val priority: TaskPriority,
        val repeatPattern: RepeatPattern,
        val category: TaskCategory?
    )
}
