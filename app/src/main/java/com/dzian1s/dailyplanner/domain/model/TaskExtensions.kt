package com.dzian1s.dailyplanner.domain.model

import kotlinx.datetime.*

fun Task.isCompleted() = status is TaskStatus.Completed
fun Task.isArchived() = status is TaskStatus.Archived
fun Task.isActive() = status is TaskStatus.Active

fun Task.isForDate(date: LocalDate) = this.date == date