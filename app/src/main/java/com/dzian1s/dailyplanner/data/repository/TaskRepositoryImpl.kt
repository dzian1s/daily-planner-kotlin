package com.dzian1s.dailyplanner.data.repository

import com.dzian1s.dailyplanner.data.local.TaskDao
import com.dzian1s.dailyplanner.data.mapper.toDomain
import com.dzian1s.dailyplanner.data.mapper.toEntity
import com.dzian1s.dailyplanner.domain.model.Task
import com.dzian1s.dailyplanner.domain.model.TaskId
import com.dzian1s.dailyplanner.domain.repository.TaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDate

class TaskRepositoryImpl(
    private val taskDao: TaskDao
) : TaskRepository {

    override fun getTasksStream(): Flow<List<Task>> =
        taskDao.getAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun getTasksForDateStream(date: LocalDate): Flow<List<Task>> =
        taskDao.getByDate(date).map { entities ->
            entities.map { it.toDomain() }
        }

    override suspend fun getTaskById(id: TaskId): Task? =
        taskDao.getById(id.value)?.toDomain()

    override suspend fun upsertTask(task: Task): TaskId {
        val entity = task.toEntity()
        val newId = taskDao.upsert(entity)
        // Room возвращает id строки. Для новой задачи это новый id, для старой — тот же.
        return TaskId(newId)
    }

    override suspend fun deleteTask(id: TaskId) {
        taskDao.delete(id.value)
    }

    override suspend fun deleteCompletedTasks() {
        taskDao.deleteCompleted()
    }
}
