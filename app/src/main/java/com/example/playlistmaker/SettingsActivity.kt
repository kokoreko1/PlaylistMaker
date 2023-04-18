package com.example.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        val imageBack = findViewById<ImageView>(R.id.image_back)

        imageBack.setOnClickListener {
//            val displayIntent = Intent(this, MainActivity::class.java)
//            startActivity(displayIntent)
            finish()
        }

    }
}