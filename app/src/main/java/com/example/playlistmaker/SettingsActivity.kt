package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_settings)

        // Переключатель Темная тема
        val swTheme = findViewById<SwitchMaterial>(R.id.themeSwitcher)

        swTheme.isChecked = (application as AppPlaylistMaker).globalVarDarkTheme

        swTheme.setOnCheckedChangeListener { switcher, checked ->
            (applicationContext as AppPlaylistMaker).switchTheme(checked)
        }

        // кнопка Возврат
        val ivBack = findViewById<ImageView>(R.id.image_back)

        ivBack.setOnClickListener {
            finish()
        }

        // кнопка Поделиться приложением
        val ivShare = findViewById<ImageView>(R.id.image_share)

        ivShare.setOnClickListener {

            val androidDeveloperUrl = getString(R.string.androidDeveloper_url)

            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SEND
                this.type = "text/plain"
                this.putExtra(Intent.EXTRA_TEXT, androidDeveloperUrl)
            }

            startActivity(Intent.createChooser(shareIntent,"Share via"))

        }

        // кнопка Написать в поддержку
        val ivSupport = findViewById<ImageView>(R.id.image_support)

        ivSupport.setOnClickListener {

            val message = getString(R.string.support_message)
            val subject = getString(R.string.support_subject)

            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_SENDTO
                this.data = Uri.parse("mailto:")
                this.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.support_email)))
                this.putExtra(Intent.EXTRA_TEXT, message)
                this.putExtra(Intent.EXTRA_SUBJECT, subject)
            }

            startActivity(shareIntent)

        }

        // кнопка Пользовательское соглашение
        val ivAgreement = findViewById<ImageView>(R.id.image_agreement)

        ivAgreement.setOnClickListener {

            val url = Uri.parse(getString(R.string.agreement_url))

            val shareIntent = Intent().apply {
                this.action = Intent.ACTION_VIEW
                this.data = url
            }

            startActivity(shareIntent)

        }

    }
}