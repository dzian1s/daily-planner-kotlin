package com.dzian1s.dailyplanner.data.mapper

import com.dzian1s.dailyplanner.data.local.TaskEntity
import com.dzian1s.dailyplanner.domain.model.*
import kotlinx.datetime.DayOfWeek

private const val STATUS_ACTIVE = "ACTIVE"
private const val STATUS_COMPLETED = "COMPLETED"
private const val STATUS_ARCHIVED = "ARCHIVED"

private const val REPEAT_NONE = "NONE"
private const val REPEAT_DAILY = "DAILY"
private const val REPEAT_WEEKLY = "WEEKLY"
private const val REPEAT_MONTHLY_ON_DAY = "MONTHLY_ON_DAY"

private data class RepeatDb(
    val type: String,
    val intervalDays: Int?,
    val daysOfWeek: String?,
    val dayOfMonth: Int?
)

private fun String.toTaskPriority(): TaskPriority =
    when (this) {
        "LOW" -> TaskPriority.LOW
        "HIGH" -> TaskPriority.HIGH
        "MEDIUM" -> TaskPriority.MEDIUM
        else -> TaskPriority.MEDIUM
    }

private fun TaskPriority.toDbString(): String = name

private fun toDomainStatus(status: String, archivedReason: String?): TaskStatus =
    when (status) {
        STATUS_ACTIVE -> TaskStatus.Active
        STATUS_COMPLETED -> TaskStatus.Completed
        STATUS_ARCHIVED -> TaskStatus.Archived(archivedReason ?: "")
        else -> TaskStatus.Active
    }

private fun TaskStatus.toDb(): Pair<String, String?> =
    when (this) {
        is TaskStatus.Active -> STATUS_ACTIVE to null
        is TaskStatus.Completed -> STATUS_COMPLETED to null
        is TaskStatus.Archived -> STATUS_ARCHIVED to this.reason
    }

private fun toDomainRepeatPattern(
    type: String,
    intervalDays: Int?,
    daysOfWeek: String?,
    dayOfMonth: Int?
): RepeatPattern =
    when (type) {
        REPEAT_DAILY -> RepeatPattern.Daily(intervalDays ?: 1)
        REPEAT_WEEKLY -> {
            val days = daysOfWeek
                ?.split(",")
                ?.mapNotNull {
                    runCatching { DayOfWeek.valueOf(it) }.getOrNull()
                }
                ?.toSet()
                ?: emptySet()
            RepeatPattern.Weekly(days)
        }
        REPEAT_MONTHLY_ON_DAY -> RepeatPattern.MonthlyOnDay(dayOfMonth ?: 1)
        else -> RepeatPattern.None
    }

private fun RepeatPattern.toDb(): RepeatDb =
    when (this) {
        is RepeatPattern.None -> RepeatDb(
            type = REPEAT_NONE,
            intervalDays = null,
            daysOfWeek = null,
            dayOfMonth = null
        )
        is RepeatPattern.Daily -> RepeatDb(
            type = REPEAT_DAILY,
            intervalDays = intervalDays,
            daysOfWeek = null,
            dayOfMonth = null
        )
        is RepeatPattern.Weekly -> RepeatDb(
            type = REPEAT_WEEKLY,
            intervalDays = null,
            daysOfWeek = daysOfWeek.joinToString(",") { it.name },
            dayOfMonth = null
        )
        is RepeatPattern.MonthlyOnDay -> RepeatDb(
            type = REPEAT_MONTHLY_ON_DAY,
            intervalDays = null,
            daysOfWeek = null,
            dayOfMonth = dayOfMonth
        )
    }

private fun toDomainCategory(
    id: Long?,
    name: String?,
    colorHex: String?,
    isDefault: Boolean
): TaskCategory? =
    if (id == null || name == null) {
        null
    } else {
        TaskCategory(
            id = CategoryId(id),
            name = name,
            colorHex = colorHex,
            isDefault = isDefault
        )
    }

private fun TaskCategory?.toDb(): QuadCategory =
    if (this == null) {
        QuadCategory(null, null, null, false)
    } else {
        QuadCategory(
            id = this.id.value,
            name = this.name,
            colorHex = this.colorHex,
            isDefault = this.isDefault
        )
    }

private data class QuadCategory(
    val id: Long?,
    val name: String?,
    val colorHex: String?,
    val isDefault: Boolean
)

fun TaskEntity.toDomain(): Task {
    val statusDomain = toDomainStatus(status, archivedReason)
    val repeatDomain = toDomainRepeatPattern(
        type = repeatType,
        intervalDays = repeatIntervalDays,
        daysOfWeek = repeatDaysOfWeek,
        dayOfMonth = repeatDayOfMonth
    )
    val categoryDomain = toDomainCategory(
        id = categoryId,
        name = categoryName,
        colorHex = categoryColorHex,
        isDefault = isCategoryDefault
    )

    return Task(
        id = TaskId(id),
        title = title,
        description = description,
        date = date,
        time = time,
        priority = priority.toTaskPriority(),
        status = statusDomain,
        repeatPattern = repeatDomain,
        category = categoryDomain,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun Task.toEntity(): TaskEntity {
    val (statusDb, archivedReasonDb) = status.toDb()
    val repeatDb = repeatPattern.toDb()
    val categoryDb = category.toDb()

    return TaskEntity(
        id = id?.value ?: 0L,
        title = title,
        description = description,
        date = date,
        time = time,
        priority = priority.toDbString(),
        status = statusDb,
        archivedReason = archivedReasonDb,
        repeatType = repeatDb.type,
        repeatIntervalDays = repeatDb.intervalDays,
        repeatDaysOfWeek = repeatDb.daysOfWeek,
        repeatDayOfMonth = repeatDb.dayOfMonth,
        categoryId = categoryDb.id,
        categoryName = categoryDb.name,
        categoryColorHex = categoryDb.colorHex,
        isCategoryDefault = categoryDb.isDefault,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
