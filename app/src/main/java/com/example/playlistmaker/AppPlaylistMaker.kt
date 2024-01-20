package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class AppPlaylistMaker: Application() {

    var globalVarSavedSearchText = ""

    val globalValRoundingRadius = 10

    var globalVarDarkTheme = false

    fun switchTheme(darkThemeEnabled: Boolean) {

        globalVarDarkTheme = darkThemeEnabled

//        AppCompatDelegate.setDefaultNightMode(
//            if (darkThemeEnabled) {
//                AppCompatDelegate.MODE_NIGHT_YES
//            } else {
//                AppCompatDelegate.MODE_NIGHT_NO
//            }
//        )
    }

}