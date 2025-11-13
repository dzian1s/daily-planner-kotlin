package com.dzian1s.dailyplanner.domain.model

import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

typealias Timestamp = Long

data class Task(
    val id: TaskId? = null,
    val title: String,
    val description: String? = null,
    val date: LocalDate,
    val time: LocalTime? = null,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val status: TaskStatus = TaskStatus.Active,
    val repeatPattern: RepeatPattern = RepeatPattern.None,
    val category: TaskCategory? = null,
    val createdAt: Timestamp,
    val updatedAt: Timestamp? = null
)
