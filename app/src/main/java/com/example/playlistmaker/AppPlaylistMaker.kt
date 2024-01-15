package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class AppPlaylistMaker: Application() {

    var globalVarSavedSearchText = ""

    val globalValRoundingRadius = 10

    var darkTheme = false

    override fun onCreate() {
        super.onCreate()
    }

    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }

}