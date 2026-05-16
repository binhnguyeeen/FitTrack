package com.binhnguyendev.fittrack

import android.app.Application
import com.binhnguyendev.fittrack.data.db.FitTrackDatabase
import com.binhnguyendev.fittrack.data.repository.Repositories
import com.binhnguyendev.fittrack.ui.theme.AppFonts

class FitTrackApplication : Application() {

    lateinit var repositories: Repositories
        private set

    override fun onCreate() {
        super.onCreate()
        AppFonts.init(assets)
        repositories = Repositories(FitTrackDatabase.get(this))
    }
}
