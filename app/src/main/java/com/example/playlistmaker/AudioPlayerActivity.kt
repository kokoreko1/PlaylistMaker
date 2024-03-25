package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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

        val trackjson: String? = intent.getStringExtra(AppPlaylistMaker.TRACK_JSON)

        var track: Track? = null

        if (trackjson != null) {
            track = gson.fromJson(trackjson, object : TypeToken<Track>() {}.type)
        }

        if (track != null) {

            val tvTrackName = findViewById<TextView>(R.id.textView_track_name)
            tvTrackName.text = track.trackName

            val tvArtistName = findViewById<TextView>(R.id.textView_artist_name)
            tvArtistName.text = track.artistName

            val tvTrackTime = findViewById<TextView>(R.id.textView_tracktime_value)
            val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
            tvTrackTime.text = dateFormat.format(track.trackTime)

            val tvCollectionName = findViewById<TextView>(R.id.textView_collectionName_value)
            tvCollectionName.text = track.collectionName

            val tvReleaseYear = findViewById<TextView>(R.id.textView_releaseYear_value)

            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
            val date = LocalDate.parse(track.releaseDate, formatter)

            tvReleaseYear.text = date.year.toString()

            val tvPrimaryGenreName = findViewById<TextView>(R.id.textView_primaryGenreName_value)
            tvPrimaryGenreName.text = track.primaryGenreName

            val tvCountry = findViewById<TextView>(R.id.textView_country_value)
            tvCountry.text = track.country

            val ivAlbum = findViewById<ImageView>(R.id.imageView_album)

            Glide.with(this)
                .load(track.getCoverArtwork())
                .placeholder(R.drawable.placeholder_album)
                .centerCrop()
                .transform(RoundedCorners(GLOBAL_ROUNDING_RADIUS))
                .into(ivAlbum)

        }

    }
}