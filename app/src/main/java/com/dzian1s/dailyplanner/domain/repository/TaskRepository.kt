package com.dzian1s.dailyplanner.domain.repository

import com.dzian1s.dailyplanner.domain.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

interface TaskRepository {

    fun getTasksStream(): Flow<List<Task>>

    fun getTasksForDateStream(date: LocalDate): Flow<List<Task>>

    suspend fun getTaskById(id: TaskId): Task?

    suspend fun upsertTask(task: Task): TaskId

    suspend fun deleteTask(id: TaskId)

    suspend fun deleteCompletedTasks()
}
