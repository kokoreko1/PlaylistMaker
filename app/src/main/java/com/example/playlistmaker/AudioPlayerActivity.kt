package com.example.playlistmaker

import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

        // Число миллисекунд в одной секунде
        private const val DELAY = 1000L

    }

    private val gson = Gson()

    private var mediaPlayer = MediaPlayer()
    private var playerState = STATE_DEFAULT

    private var mainThreadHandler: Handler? = null
    private var secondsPlay: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val buttonPlay = findViewById<ImageButton>(R.id.imageButton_play)

        // Создаём Handler, привязанный к главному потоку
        mainThreadHandler = Handler(Looper.getMainLooper())


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

        mediaPlayer.setDataSource(url)

        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
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

        when (playerState) {

            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
                startTimer()
            }
        }
    }

    private fun startTimer() {

        val startTime = System.currentTimeMillis()

        mainThreadHandler?.post(
            createUpdateTimerTask(startTime)
        )
    }

    private fun createUpdateTimerTask(startTime: Long): Runnable {

        return object : Runnable {

            override fun run() {

                val buttonPlay = findViewById<ImageButton>(R.id.imageButton_play)
                val tvPlayTime = findViewById<TextView>(R.id.textView_play_time)

                val elapsedTime = System.currentTimeMillis() - startTime

                val seconds = secondsPlay + elapsedTime / DELAY

                when (playerState) {

                    STATE_PLAYING -> {

                        tvPlayTime?.text = String.format("%d:%02d", seconds / 60, seconds % 60)

                        mainThreadHandler?.postDelayed(this, DELAY)

                    }

                    STATE_PAUSED -> {

                        secondsPlay = seconds

                    }

                    // окончание фрагмента
                    STATE_PREPARED -> {

                        secondsPlay = 0

                        buttonPlay.setImageResource(R.drawable.button_play)

                    }
                }

            }
        }
    }


}