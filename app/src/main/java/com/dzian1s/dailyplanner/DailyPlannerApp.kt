package com.dzian1s.dailyplanner

import android.app.Application
import com.dzian1s.dailyplanner.core.AppGraph

class DailyPlannerApp : Application() {
    override fun onCreate() {
        super.onCreate()
        AppGraph.init(this)
    }
}
