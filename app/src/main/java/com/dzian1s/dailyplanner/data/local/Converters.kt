package com.dzian1s.dailyplanner.data.local

import androidx.room.TypeConverter
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalTime

class Converters {

    @TypeConverter
    fun fromLocalDate(date: LocalDate): String = date.toString()

    @TypeConverter
    fun toLocalDate(str: String): LocalDate = LocalDate.parse(str)

    @TypeConverter
    fun fromLocalTime(time: LocalTime?): String? = time?.toString()

    @TypeConverter
    fun toLocalTime(str: String?): LocalTime? =
        str?.let { LocalTime.parse(it) }
}
