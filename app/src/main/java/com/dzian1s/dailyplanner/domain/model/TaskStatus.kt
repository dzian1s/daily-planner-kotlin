package com.dzian1s.dailyplanner.domain.model

sealed interface TaskStatus {
    data object Active : TaskStatus
    data object Completed : TaskStatus
    data class Archived(val reason: String) : TaskStatus
}