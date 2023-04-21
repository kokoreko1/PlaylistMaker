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

            val androidDeveloper_url = getString(R.string.androidDeveloper_url)

            val shareIntent = Intent()

            shareIntent.action = Intent.ACTION_SEND
            shareIntent.type = "text/plain"

            shareIntent.putExtra(Intent.EXTRA_TEXT, androidDeveloper_url)

            startActivity(Intent.createChooser(shareIntent,"Share via"))

        }

        /////////////////////////////////////////
        // кнопка Написать в поддержку
        val imageSupport = findViewById<ImageView>(R.id.image_support)

        imageSupport.setOnClickListener {

            val message = getString(R.string.support_message)
            val subject = getString(R.string.support_subject)

            val shareIntent = Intent()

            shareIntent.action = Intent.ACTION_SENDTO

            shareIntent.data = Uri.parse("mailto:")

            shareIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
            shareIntent.putExtra(Intent.EXTRA_TEXT, message)
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, subject)

            startActivity(shareIntent)

        }

        /////////////////////////////////////////
        // кнопка Пользовательское соглашение
        val imageAgreement = findViewById<ImageView>(R.id.image_agreement)

        imageAgreement.setOnClickListener {

            val url = Uri.parse(getString(R.string.agreement_url))

            val shareIntent = Intent()

            shareIntent.action = Intent.ACTION_VIEW
            shareIntent.data = url

            startActivity(shareIntent)

        }

    }
}