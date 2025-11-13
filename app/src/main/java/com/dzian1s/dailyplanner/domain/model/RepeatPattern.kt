package com.dzian1s.dailyplanner.domain.model

import kotlinx.datetime.DayOfWeek

sealed interface RepeatPattern {
    data object None : RepeatPattern
    data class Daily(val intervalDays: Int = 1) : RepeatPattern
    data class Weekly(val daysOfWeek: Set<DayOfWeek>) : RepeatPattern
    data class MonthlyOnDay(val dayOfMonth: Int) : RepeatPattern
}