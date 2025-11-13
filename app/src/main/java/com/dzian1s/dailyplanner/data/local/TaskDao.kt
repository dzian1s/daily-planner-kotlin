package com.dzian1s.dailyplanner.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDate

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks")
    fun getAll(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE date = :date")
    fun getByDate(date: LocalDate): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(task: TaskEntity): Long

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun delete(id: Long)

    @Query("DELETE FROM tasks WHERE status = 'COMPLETED'")
    suspend fun deleteCompleted()
}
