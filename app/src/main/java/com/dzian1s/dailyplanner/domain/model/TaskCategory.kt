package com.dzian1s.dailyplanner.domain.model

data class TaskCategory(
    val id: CategoryId,
    val name: String,
    val colorHex: String? = null,
    val isDefault: Boolean = false
)