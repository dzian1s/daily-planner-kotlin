package com.dzian1s.dailyplanner.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val title: String,
    val description: String?,

    val date: LocalDate,
    val time: LocalTime?,

    // enum'ы и sealed мы храним как строки
    val priority: String,          // "LOW", "MEDIUM", "HIGH"
    val status: String,            // "ACTIVE", "COMPLETED", "ARCHIVED"
    val archivedReason: String?,   // причина, если ARCHIVED

    // RepeatPattern расплющиваем тоже в поля
    val repeatType: String,        // "NONE", "DAILY", "WEEKLY", "MONTHLY_ON_DAY"
    val repeatIntervalDays: Int?,  // для DAILY
    val repeatDaysOfWeek: String?, // для WEEKLY -> "MONDAY,TUESDAY"
    val repeatDayOfMonth: Int?,    // для MONTHLY_ON_DAY

    // Категорию тоже пока храним в самой задаче
    val categoryId: Long?,
    val categoryName: String?,
    val categoryColorHex: String?,
    val isCategoryDefault: Boolean,

    val createdAt: Long,
    val updatedAt: Long?
)
