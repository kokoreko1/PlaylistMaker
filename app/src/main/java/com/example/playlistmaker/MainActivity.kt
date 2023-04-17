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

        ///////////////////////////////
        // кнопка Поиск
        val buttonSearch = findViewById<Button>(R.id.button_search)

        buttonSearch.setOnClickListener {
            val displayIntent = Intent(this, SearchActivity::class.java)
            startActivity(displayIntent)
        }

//        val buttonSearchClickListener: View.OnClickListener = object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                Toast.makeText(this@MainActivity, "Нажали на кнопку Поиск", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        buttonSearch.setOnClickListener(buttonSearchClickListener)
//
//        // лямбда
//        buttonSearch.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Нажали на кнопку Поиск (лямбда)", Toast.LENGTH_SHORT).show()
//        }

        ///////////////////////////////
        // кнопка Медиатека
        val buttonLibrary = findViewById<Button>(R.id.button_library)

        buttonLibrary.setOnClickListener {
            val displayIntent = Intent(this, LibraryActivity::class.java)
            startActivity(displayIntent)
        }

//        val buttonLibraryClickListener: View.OnClickListener = object : View.OnClickListener {
//            override fun onClick(v: View?) {
//                Toast.makeText(this@MainActivity, "Нажали на кнопку Медиатека", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        buttonLibrary.setOnClickListener(buttonLibraryClickListener)
//
//        // лямбда
//        buttonMedia.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Нажали на кнопку Медиатека (лямбда)", Toast.LENGTH_SHORT).show()
//        }

        ///////////////////////////////
        // кнопка Настройка
        val buttonSettings = findViewById<Button>(R.id.button_settings)

        buttonSettings.setOnClickListener {
            val displayIntent = Intent(this, SettingsActivity::class.java)
            startActivity(displayIntent)
        }

//        buttonSettings.setOnClickListener {
//            Toast.makeText(this@MainActivity, "Нажали на кнопку Настройки (лямбда)", Toast.LENGTH_SHORT).show()
//        }

    }
}