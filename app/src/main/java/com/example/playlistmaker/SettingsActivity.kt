package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        ///////////////////////////////
        // кнопка Возврат
        val imageBack = findViewById<ImageView>(R.id.image_back)

        imageBack.setOnClickListener {
            finish()
        }

        /////////////////////////////////////////
        // кнопка Поделиться приложением
        val imageShare = findViewById<ImageView>(R.id.image_share)

        imageShare.setOnClickListener {

            val androidDeveloper = "https://practicum.yandex.ru/android-developer/"

            val shareIntent = Intent()

            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"

            shareIntent.putExtra(Intent.EXTRA_TEXT, androidDeveloper)

            startActivity(Intent.createChooser(shareIntent,"Share via"))

        }

        /////////////////////////////////////////
        // кнопка Поделиться приложением
        val imageSupport = findViewById<ImageView>(R.id.image_support)

        imageSupport.setOnClickListener {

            val message = "Спасибо разработчикам и разработчицам за крутое приложение."
            val subject = "Сообщение разработчикам и разработчицам приложения Playlist Maker"

            val shareIntent = Intent()

            shareIntent.action = Intent.ACTION_SENDTO

            shareIntent.data = Uri.parse("mailto:")

            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf("kokoreko@ya.ru"))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)

            startActivity(shareIntent)

        }

        /////////////////////////////////////////
        // кнопка Пользовательское соглашение
        val imageAgreement = findViewById<ImageView>(R.id.image_agreement)

        imageAgreement.setOnClickListener {

            val url = Uri.parse("https://yandex.ru/legal/practicum_offer/")

            val shareIntent = Intent()

            shareIntent.action = Intent.ACTION_VIEW
            shareIntent.data = url

            startActivity(shareIntent)

        }

    }
}