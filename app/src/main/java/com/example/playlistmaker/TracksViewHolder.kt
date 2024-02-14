package com.example.playlistmaker

import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class TracksViewHolder(trackView: View): RecyclerView.ViewHolder(trackView) {

    private val trackName: TextView = itemView.findViewById(R.id.track_name)
    private val artistName: TextView = itemView.findViewById(R.id.artist_name)
    private val trackTime: TextView = itemView.findViewById(R.id.track_time)

    private val trackImage: ImageView = itemView.findViewById(R.id.track_icon)

    val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())

    fun bind(track: Track) {

        val application = AppPlaylistMaker()

        trackName.text = track.trackName
        artistName.text = track.artistName
        trackTime.text = dateFormat.format(track.trackTime)

        Glide.with(itemView)
            .load(track.artworkUrl100)
            .placeholder(R.drawable.barsik)
            .centerCrop()
            .transform(RoundedCorners(application.globalValRoundingRadius))
            .into(trackImage)

    }
}


