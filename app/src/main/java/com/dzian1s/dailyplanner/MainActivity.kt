package com.dzian1s.dailyplanner

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dzian1s.dailyplanner.ui.tasklist.TaskListRoute
import com.dzian1s.dailyplanner.ui.theme.DailyPlannerTheme // или как у тебя называется тема

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DailyPlannerTheme {
                TaskListRoute()
            }
        }
    }
}
