package com.dzian1s.dailyplanner.core

import android.content.Context
import androidx.room.Room
import com.dzian1s.dailyplanner.data.local.AppDatabase
import com.dzian1s.dailyplanner.data.repository.TaskRepositoryImpl
import com.dzian1s.dailyplanner.domain.repository.TaskRepository

object AppGraph {

    private lateinit var database: AppDatabase

    val taskRepository: TaskRepository by lazy {
        TaskRepositoryImpl(database.taskDao)
    }

    fun init(context: Context) {
        database = Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "daily_planner.db"
        ).build()
    }
}
