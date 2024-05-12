package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
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

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private val gson = Gson()

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_audio_player)

        val buttonPlay = findViewById<ImageButton>(R.id.imageButton_play)

        buttonPlay.setOnClickListener {
            playbackControl()
        }

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

            preparePlayer(track.previewUrl)

        }

    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }
    private fun preparePlayer(url: String) {

        val buttonPlay = findViewById<ImageButton>(R.id.imageButton_play)

        //var url = "https://audio-ssl.itunes.apple.com/itunes-assets/AudioPreview112/v4/ac/c7/d1/acc7d13f-6634-495f-caf6-491eccb505e8/mzaf_4002676889906514534.plus.aac.p.m4a"

        mediaPlayer.setDataSource(url)

        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            //buttonPlay.isEnabled = true
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            //buttonPlay.text = "PLAY"
            playerState = STATE_PREPARED
        }
    }

    private fun startPlayer() {

        val buttonPlay = findViewById<ImageButton>(R.id.imageButton_play)

        mediaPlayer.start()

        buttonPlay.setImageResource(R.drawable.button_pause)

        playerState = STATE_PLAYING
    }

    private fun pausePlayer() {

        val buttonPlay = findViewById<ImageButton>(R.id.imageButton_play)

        mediaPlayer.pause()

        buttonPlay.setImageResource(R.drawable.button_play)

        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

}