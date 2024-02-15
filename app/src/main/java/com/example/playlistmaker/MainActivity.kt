package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // восстановим сохраненную настройку DARK_THEME
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)
        val darkTheme = sharedPrefs.getBoolean(DARK_THEME, false)
        (application as AppPlaylistMaker).switchTheme(darkTheme)

        // кнопка Поиск
        val btSearch = findViewById<Button>(R.id.button_search)

        btSearch.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

        // кнопка Медиатека
        val btLibrary = findViewById<Button>(R.id.button_library)

        btLibrary.setOnClickListener {
            val displayIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayIntent)
        }

        // кнопка Настройка
        val btSettings = findViewById<Button>(R.id.button_settings)

        btSettings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

    }

    override fun onStop() {
        super.onStop()

        // сохраним настройку DARK_THEME
        val sharedPrefs = getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        sharedPrefs.edit()
            .putBoolean(DARK_THEME, (application as AppPlaylistMaker).globalVarDarkTheme)
            .apply()

    }
    companion object {
        const val PLAYLIST_MAKER_PREFERENCES = "playlist_maker_preferences"
        const val DARK_THEME = "dark_theme"
    }
}