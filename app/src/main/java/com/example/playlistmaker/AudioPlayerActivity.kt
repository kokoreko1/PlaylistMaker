package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class AudioPlayerActivity : AppCompatActivity() {

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_audio_player)

        val ivBack = findViewById<ImageView>(R.id.image_back)

        // Нажатие на кнопку возврат.
        ivBack.setOnClickListener {
            finish()
        }

        val trackjson: String? = intent.getStringExtra(Constants.TRACKJSON)

        var track: Track? = null

        if (trackjson != null) {
            track = gson.fromJson(trackjson, object : TypeToken<Track>() {}.type)
        }

        if (track != null) {

            val tvTrackName = findViewById<TextView>(R.id.textView_track_name)
            tvTrackName.text = track.trackName

            val tvArtistName = findViewById<TextView>(R.id.textView_artist_name)


        }



    }
}