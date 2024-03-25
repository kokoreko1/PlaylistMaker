package com.example.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import android.content.SharedPreferences
import android.content.Context

const val  GLOBAL_ROUNDING_RADIUS = 1

class AppPlaylistMaker : Application() {

    var globalVarSavedSearchText = ""
    var globalVarDarkTheme: Boolean = false

    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, Context.MODE_PRIVATE)

        val darkThemeEnabled = sharedPrefs.getBoolean(DARK_THEME, false)

        switchTheme(darkThemeEnabled)

    }

    fun switchTheme(darkThemeEnabled: Boolean) {

        globalVarDarkTheme = darkThemeEnabled

        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )

        sharedPrefs
            .edit()
            .putBoolean(DARK_THEME, darkThemeEnabled)
            .apply()

    }

    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val DARK_THEME = "dark_theme"
        const val TRACK_JSON = "trackjson"
    }

}